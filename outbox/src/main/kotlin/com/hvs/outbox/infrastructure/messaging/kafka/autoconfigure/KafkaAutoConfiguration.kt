package com.hvs.outbox.infrastructure.messaging.kafka.autoconfigure

import com.hvs.outbox.core.domain.BaseEvent
import com.hvs.outbox.core.port.MessagePublisher
import com.hvs.outbox.infrastructure.messaging.kafka.KafkaMessagePublisher
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@EnableConfigurationProperties(KafkaConfigProperties::class)
@ConditionalOnProperty(prefix = "kafka", name = ["enabled"], havingValue = "true")
class KafkaAutoConfiguration(private val props: KafkaConfigProperties) {


    // Spring provides a default instance if not configured manually like below
//    @Bean
//    fun producerFactory(): ProducerFactory<String, Any> {
//        val configs = mapOf(
//            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG     to props.bootstrapServers,
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG   to StringSerializer::class.java,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
//            ProducerConfig.ACKS_CONFIG                  to "all",
//            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG    to props.deliveryTimeout.toMillis().toInt()
//        )
//        return DefaultKafkaProducerFactory(configs)
//    }

    // Spring provides a default instance if not configured manually like below
//    @Bean
//    fun kafkaTemplate(pf: ProducerFactory<String, BaseEvent>): KafkaTemplate<String, BaseEvent> =
//        KafkaTemplate(pf)

    @Bean
    fun kafkaMessagePublisher(template: KafkaTemplate<String, Any>): MessagePublisher =
        KafkaMessagePublisher(
            kafkaTemplate   = template,
            topic           = props.topic,
            deliveryTimeout = props.deliveryTimeout
        )
}
