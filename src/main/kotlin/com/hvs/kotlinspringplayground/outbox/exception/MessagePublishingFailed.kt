package com.hvs.kotlinspringplayground.outbox.exception

class MessagePublishingFailed(message: String) : RuntimeException(message)