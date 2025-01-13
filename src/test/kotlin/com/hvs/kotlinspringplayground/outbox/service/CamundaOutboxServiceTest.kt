package com.hvs.kotlinspringplayground.outbox.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.outbox.domain.BaseEvent
import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import com.hvs.kotlinspringplayground.outbox.exception.OutboxTransactionReadOnlyException
import com.hvs.kotlinspringplayground.outbox.repository.OutboxRepository
import com.hvs.kotlinspringplayground.outbox.service.impl.CamundaOutboxService
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.net.URI
import java.time.LocalDateTime
import java.util.UUID
import java.util.function.Supplier

@ExtendWith(MockitoExtension::class)
class CamundaOutboxServiceTest {

    @Mock
    private lateinit var outboxRepository: OutboxRepository

    private val cloudEventSource = "http://test-source"

    private lateinit var outboxService: CamundaOutboxService

    @BeforeEach
    fun setUp() {
        outboxService = CamundaOutboxService(
            outboxRepository = outboxRepository,
            cloudEventSource = cloudEventSource
        )
    }

    @Nested
    inner class SendSupplierBaseEventTests {

        @Test
        fun `send(eventSupplier) builds and stores the CloudEvent`() {
            // GIVEN
            val mockEvent = mock(BaseEvent::class.java).apply {
                whenever(result).thenReturn(jacksonObjectMapper().createObjectNode())
                whenever(type).thenReturn("some-type")
                whenever(date).thenReturn(LocalDateTime.now())
                whenever(id).thenReturn(UUID.randomUUID())
            }

            val supplier = Supplier { mockEvent }

            var savedMessage: OutboxMessage? = null
            whenever(outboxRepository.save(any())).thenAnswer { invocation ->
                savedMessage = invocation.arguments[0] as OutboxMessage
                savedMessage
            }

            // WHEN
            outboxService.send(supplier)

            // THEN
            verify(outboxRepository, times(1)).save(any<OutboxMessage>())
            assertNotNull(savedMessage)

            val storedJson = savedMessage!!.message
            val cloudEventFormat = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)!!
            val event = cloudEventFormat.deserialize(storedJson.toByteArray())

            assertEquals(mockEvent.id.toString(), event.id)
            assertEquals(URI(cloudEventSource), event.source)
            assertEquals("some-type", event.type)
        }
    }

    @Nested
    inner class SendStringTests {

        @Test
        fun `send(message) saves OutboxMessage if transaction is not read-only`() {
            // GIVEN
            val message = "Test plain message"
            var savedMessage: OutboxMessage? = null

            whenever(outboxRepository.save(any())).thenAnswer { invocation ->
                savedMessage = invocation.arguments[0] as OutboxMessage
                savedMessage
            }

            // WHEN
            outboxService.send(message)

            // THEN
            verify(outboxRepository, times(1)).save(any<OutboxMessage>())
            assertEquals(message, savedMessage?.message)
        }

        @Test
        fun `send(message) throws OutboxTransactionReadOnlyException if transaction is read-only`() {
            // GIVEN
            val message = "Read-only message"

            mockStatic(TransactionSynchronizationManager::class.java).use { mockedStatic ->
                mockedStatic.`when`<Boolean> {
                    TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                }.thenReturn(true)

                // WHEN & THEN
                assertThrows<OutboxTransactionReadOnlyException> {
                    outboxService.send(message)
                }
            }
        }
    }

    @Nested
    inner class GetOldestMessageTests {

        @Test
        fun `getOldestMessage calls outboxRepository findOutboxMessage`() {
            // WHEN
            outboxService.getOldestMessage()

            // THEN
            verify(outboxRepository, times(1)).findOutboxMessage()
        }
    }

    @Nested
    inner class DeleteMessageTests {

        @Test
        fun `deleteMessage calls outboxRepository deleteById`() {
            // GIVEN
            val messageId = UUID.randomUUID()

            // WHEN
            outboxService.deleteMessage(messageId)

            // THEN
            verify(outboxRepository, times(1)).deleteById(eq(messageId))
        }
    }
}
