package com.hvs.kotlinspringplayground.outbox.exception

class OutboxTransactionReadOnlyException :
    RuntimeException("Failed to send outbox message. Reason: current transaction is read-only")