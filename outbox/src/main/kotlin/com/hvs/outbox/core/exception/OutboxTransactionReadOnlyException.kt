package com.hvs.outbox.core.exception

class OutboxTransactionReadOnlyException :
    RuntimeException("Failed to send outbox message. Reason: current transaction is read-only")