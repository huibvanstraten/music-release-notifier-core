package com.hvs.kotlinspringplayground.rabbitmq.publisher

import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import com.hvs.kotlinspringplayground.outbox.exception.MessagePublishingFailed
import com.hvs.kotlinspringplayground.outbox.publisher.MessagePublisher
import mu.KLogger
import mu.KotlinLogging
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RabbitMQMessagePublisher(
    private val topicExchange: TopicExchange,
    private val binding: Binding,
    private val rabbitMQTemplate: RabbitTemplate,
    private val deliveryTimeout: Duration,
) : MessagePublisher {


    private val routingKey: String
        get() = binding.routingKey

    init {
        require(rabbitMQTemplate.connectionFactory.isPublisherConfirms) {
            "The RabbitMQ outbox publisher requires correlated publisher-confirm-type!"
        }
        require(rabbitMQTemplate.connectionFactory.isPublisherReturns) {
            "The RabbitMQ outbox publisher requires publisher-returns to be enabled!"
        }
        require(rabbitMQTemplate.isMandatoryFor(Message("test".toByteArray()))) {
            "The RabbitMQ outbox publisher requires messages to be mandatory!"
        }
    }

    override fun publish(message: OutboxMessage) {
        val correlationData = CorrelationData(UUID.randomUUID().toString())
        logger.trace {
            "Sending message to RabbitMQ: " +
                "routingKey=$routingKey, " +
                "msgId=${message.id}, " +
                "correlationId=${correlationData.id}"
        }

        rabbitMQTemplate.convertAndSend(topicExchange.name, routingKey, message.message, correlationData)

        try {
            val result = correlationData.future.get(deliveryTimeout.toMillis(), TimeUnit.MILLISECONDS)

            if (result == null || !result.isAck) {
                throw MessagePublishingFailed(
                    "Outbox message was NOT acknowledged: " +
                        "reason=${result?.reason}, routingKey=$routingKey, msgId=${message.id}, " +
                        "correlationId=${correlationData.id}"
                )
            }

            if (correlationData.returned != null) {
                val returned = correlationData.returned!!
                throw MessagePublishingFailed(
                    "Could not deliver outbox message: " +
                        "routingKey=${returned.routingKey}, code=${returned.replyCode}, " +
                        "msg=${returned.replyText}, msgId=${message.id}, " +
                        "correlationId=${correlationData.id}"
                )
            }
        } catch (_: TimeoutException) {
            throw MessagePublishingFailed(
                "Outbox message delivery was not confirmed in time: " +
                    "routingKey=$routingKey, msgId=${message.id}, correlationId=${correlationData.id}"
            )
        }
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
