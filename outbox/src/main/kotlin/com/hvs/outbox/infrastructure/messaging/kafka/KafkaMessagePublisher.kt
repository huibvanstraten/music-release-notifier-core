package com.hvs.outbox.infrastructure.messaging.kafka

import com.hvs.outbox.core.domain.jpa.OutboxMessage
import com.hvs.outbox.core.exception.MessagePublishingFailed
import com.hvs.outbox.core.port.MessagePublisher
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class KafkaMessagePublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val topic: String,
    private val deliveryTimeout: Duration
) : MessagePublisher {

    private val logger = KotlinLogging.logger {}

    override fun publish(message: OutboxMessage) {
        logger.trace {
            "Sending OutboxMessage to Kafka topic=$topic, msgId=${message.id}"
        }

        val future = kafkaTemplate.send(topic, message.id.toString(), message.message)

        try {
            future.get(deliveryTimeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (_: TimeoutException) {
            throw MessagePublishingFailed(
                "Kafka send ack not received in ${deliveryTimeout.toMillis()}ms for msgId=${message.id}"
            )
        } catch (e: Exception) {
            throw MessagePublishingFailed(
                "Error sending OutboxMessage msgId=${message.id} to Kafka: ${e.message}"
            )
        }
    }
}
