package com.hvs.kotlinspringplayground.outbox.publisher

import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage

interface MessagePublisher {

    fun publish(message: OutboxMessage)
}
