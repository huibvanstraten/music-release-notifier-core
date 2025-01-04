package com.hvs.kotlinspringplayground.outbox.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.outbox.domain.BaseEvent
import com.hvs.kotlinspringplayground.outbox.domain.EventData
import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import com.hvs.kotlinspringplayground.outbox.exception.OutboxTransactionReadOnlyException
import com.hvs.kotlinspringplayground.outbox.repository.OutboxRepository
import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import mu.KotlinLogging
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.net.URI
import java.time.ZonedDateTime
import java.util.UUID
import java.util.function.Supplier
import kotlin.text.Charsets.UTF_8

open class CamundaOutboxService(
    private val outboxRepository: OutboxRepository,
    private val cloudEventSource: String
) : OutboxService {

    @Transactional(propagation = Propagation.MANDATORY) // Check later if there is a Transactional above, otherwise change propagation
    override fun send(eventSupplier: Supplier<BaseEvent>) {
        val baseEvent = eventSupplier.get()
        val eventData = EventData(baseEvent.result)

        val cloudEvent = CloudEventBuilder.v1()
            .withId(baseEvent.id.toString())
            .withSource(URI(cloudEventSource))
            .withTime(baseEvent.date.atOffset(ZonedDateTime.now().offset))
            .withType(baseEvent.type)
            .withDataContentType("application/json")
            .withData(jacksonObjectMapper().writeValueAsBytes(eventData))
            .build()
        val serializedCloudEvent = EventFormatProvider
            .getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE)!!
            .serialize(cloudEvent)
        val serializedCloudEventString = String(serializedCloudEvent, UTF_8)

        send(serializedCloudEventString)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    open fun send(message: String) {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            throw OutboxTransactionReadOnlyException()
        }

        val outboxMessage = OutboxMessage(
            message = message
        )
        logger.debug { "Saving OutboxMessage '${outboxMessage.id}'" }
        outboxRepository.save(outboxMessage)
    }

    open fun getOldestMessage() = outboxRepository.findOutboxMessage()

    open fun deleteMessage(id: UUID) = outboxRepository.deleteById(id)

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}