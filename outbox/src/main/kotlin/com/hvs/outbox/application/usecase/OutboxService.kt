package com.hvs.outbox.application.usecase

import com.hvs.outbox.core.domain.BaseEvent
import com.hvs.outbox.core.domain.jpa.OutboxMessage
import java.util.UUID
import java.util.function.Supplier

interface OutboxService {
    fun send(eventSupplier: Supplier<BaseEvent>)

    fun getOldestMessage(): OutboxMessage?

    fun deleteMessage(id: UUID)
}