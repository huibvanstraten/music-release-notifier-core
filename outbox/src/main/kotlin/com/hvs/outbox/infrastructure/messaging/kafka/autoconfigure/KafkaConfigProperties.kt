package com.hvs.outbox.infrastructure.messaging.kafka.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "kafka")
data class KafkaConfigProperties(
    val topic: String,
    val deliveryTimeout: Duration,
    val enabled: Boolean
)
