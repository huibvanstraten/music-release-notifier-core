package com.hvs.outbox.infrastructure.usecase

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.outbox.core.domain.BaseEvent
import com.hvs.outbox.core.domain.EventData
import com.hvs.outbox.core.domain.jpa.OutboxMessage
import com.hvs.outbox.core.exception.OutboxTransactionReadOnlyException
import com.hvs.outbox.core.port.OutboxRepository
import com.hvs.outbox.application.usecase.OutboxService
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

open class CamundaOutboxService(
    private val outboxRepository: OutboxRepository,
    private val cloudEventSource: String
) : OutboxService {

    @Transactional(propagation = Propagation.MANDATORY)
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
        val serializedCloudEventString = String(serializedCloudEvent, Charsets.UTF_8)

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

    override fun getOldestMessage() = outboxRepository.findOutboxMessage()

    override fun deleteMessage(id: UUID) = outboxRepository.deleteById(id)

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}