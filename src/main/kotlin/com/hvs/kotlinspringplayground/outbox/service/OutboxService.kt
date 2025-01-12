package com.hvs.kotlinspringplayground.outbox.service

import com.hvs.kotlinspringplayground.outbox.domain.BaseEvent
import java.util.function.Supplier

interface OutboxService {
    fun send(eventSupplier: Supplier<BaseEvent>)
}