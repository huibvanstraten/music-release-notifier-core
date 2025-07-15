package com.hvs.outbox.core.port

import com.hvs.outbox.core.domain.jpa.OutboxMessage

interface MessagePublisher {

    fun publish(message: OutboxMessage)
}