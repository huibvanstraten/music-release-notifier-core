package com.hvs.outbox.infrastructure.usecase

import com.hvs.outbox.application.usecase.OutboxService
import com.hvs.outbox.application.usecase.PollingPublisherService
import com.hvs.outbox.core.port.MessagePublisher
import mu.KotlinLogging
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.atomic.AtomicBoolean

open class PollingPublisherImplService(
    private val outboxService: OutboxService,
    private val messagePublisher: MessagePublisher,
    private val platformTransactionManager: PlatformTransactionManager
): PollingPublisherService {
    private val polling = AtomicBoolean(false)

    init {
        logger.info { "Using ${messagePublisher::class.qualifiedName} as outbox message publisher." }
    }

    /**
     * Poll messages from the outbox table and publishes them in the correct order.
     */
    override fun pollAndPublishAll() {
        if (polling.compareAndSet(false, true)) {
            try {
                do {
                    TransactionTemplate(platformTransactionManager).executeWithoutResult {
                        val oldestMessage = outboxService.getOldestMessage()
                        if (oldestMessage != null) {
                            logger.debug { "Sending OutboxMessage '${oldestMessage.id}'" }
                            messagePublisher.publish(oldestMessage)
                            outboxService.deleteMessage(oldestMessage.id)
                        } else {
                            polling.set(false)
                        }
                    }
                } while (polling.get())
            } catch (e: Exception) {
                throw RuntimeException("Failed to poll and publish outbox messages", e)
            } finally {
                polling.set(false)
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}