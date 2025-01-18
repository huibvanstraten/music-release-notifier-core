package com.hvs.kotlinspringplayground.outbox.publisher

import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import com.hvs.kotlinspringplayground.outbox.service.impl.CamundaOutboxService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import java.util.UUID

private val MESSAGE_ID_1 = UUID.randomUUID()
private val MESSAGE_ID_2 = UUID.randomUUID()
private val MESSAGE_ID_3 = UUID.randomUUID()

@ExtendWith(MockitoExtension::class)
class PollingPublisherServiceTest {

    @Mock
    private lateinit var outboxService: CamundaOutboxService

    @Mock
    private lateinit var messagePublisher: MessagePublisher

    @Mock
    private lateinit var transactionManager: PlatformTransactionManager

    @InjectMocks
    private lateinit var pollingPublisherService: PollingPublisherService

    @Nested
    inner class NoMessagesScenario {

        @Test
        fun `pollAndPublishAll does nothing if no outbox messages are found`() {
            // GIVEN
            whenever(outboxService.getOldestMessage()).thenReturn(null)

            // WHEN
            pollingPublisherService.pollAndPublishAll()

            // THEN
            verify(outboxService, times(1)).getOldestMessage()
            verify(messagePublisher, never()).publish(any<OutboxMessage>())
            verify(outboxService, never()).deleteMessage(any<UUID>())
        }
    }

    @Nested
    inner class SingleMessageScenario {

        @Test
        fun `pollAndPublishAll publishes and deletes a single outbox message`() {
            // GIVEN
            val outboxMessage = OutboxMessage(
                id = MESSAGE_ID_1,
                message = "test-message",
                createdOn = LocalDateTime.now(),
            )

            whenever(outboxService.getOldestMessage()).thenReturn(outboxMessage).thenReturn(null)

            // WHEN
            pollingPublisherService.pollAndPublishAll()

            // THEN
            verify(outboxService, times(2)).getOldestMessage()
            verify(messagePublisher, times(1)).publish(outboxMessage)
            verify(outboxService, times(1)).deleteMessage(outboxMessage.id)
        }
    }

    @Nested
    inner class MultipleMessagesScenario {

        @Test
        fun `pollAndPublishAll processes multiple messages in order until none remain`() {
            // GIVEN
            val msg1 = OutboxMessage(id = MESSAGE_ID_1, message = "message1")
            val msg2 = OutboxMessage(id = MESSAGE_ID_2, message = "message2")
            val msg3 = OutboxMessage(id = MESSAGE_ID_3, message = "message3")

            whenever(outboxService.getOldestMessage())
                .thenReturn(msg1)
                .thenReturn(msg2)
                .thenReturn(msg3)
                .thenReturn(null)

            // WHEN
            pollingPublisherService.pollAndPublishAll()

            // THEN
            verify(outboxService, times(4)).getOldestMessage()
            verify(messagePublisher).publish(msg1)
            verify(messagePublisher).publish(msg2)
            verify(messagePublisher).publish(msg3)
            verify(outboxService).deleteMessage(msg1.id)
            verify(outboxService).deleteMessage(msg2.id)
            verify(outboxService).deleteMessage(msg3.id)
        }
    }

    @Nested
    inner class ExceptionScenario {

        @Test
        fun `pollAndPublishAll throws RuntimeException if an exception occurs inside the loop`() {
            // GIVEN
            val msg = OutboxMessage(id = MESSAGE_ID_1, message = "failing-message")
            whenever(outboxService.getOldestMessage()).thenReturn(msg)

            doThrow(RuntimeException("Publisher error")).whenever(messagePublisher).publish(msg)

            // WHEN & THEN
            val ex = assertThrows<RuntimeException> {
                pollingPublisherService.pollAndPublishAll()
            }
            assertTrue(ex.message!!.contains("Failed to poll and publish outbox messages"))

            verify(outboxService, times(1)).getOldestMessage()
            verify(messagePublisher, times(1)).publish(msg)
            verify(outboxService, never()).deleteMessage(any())
        }
    }
}
