package com.hvs.outbox.infrastructure.messaging.rabbitmq.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rabbitmq")
data class RabbitMQConfigProperties(
    val queue: String,
    val exchange: String,
    val routingKey: String,
    val deliveryTimeout: Long,
    val enabled: Boolean
)