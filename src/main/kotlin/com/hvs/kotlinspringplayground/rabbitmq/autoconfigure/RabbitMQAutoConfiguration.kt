package com.hvs.kotlinspringplayground.rabbitmq.autoconfigure

import com.hvs.kotlinspringplayground.outbox.publisher.MessagePublisher
import com.hvs.kotlinspringplayground.rabbitmq.publisher.RabbitMQMessagePublisher
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableConfigurationProperties(RabbitMQConfigProperties::class)
class RabbitMQAutoConfiguration(
    private val rabbitMQConfigProperties: RabbitMQConfigProperties,
) {

    @Bean
    fun queue(): Queue =
        Queue(rabbitMQConfigProperties.queue)

    @Bean
    fun topicExchange(): TopicExchange =
        TopicExchange(rabbitMQConfigProperties.exchange)

    @Bean
    fun binding(
        queue: Queue,
        topicExchange: TopicExchange
    ): Binding =
        BindingBuilder.bind(queue)
            .to(topicExchange)
            .with(rabbitMQConfigProperties.routingKey)

    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter =
        Jackson2JsonMessageConverter()

    @Bean
    fun rabbitMQMessagePublisher(
        topicExchange: TopicExchange,
        binding: Binding,
        rabbitTemplate: RabbitTemplate
    ): MessagePublisher {
        return RabbitMQMessagePublisher(
            topicExchange = topicExchange,
            binding = binding,
            rabbitMQTemplate = rabbitTemplate,
            deliveryTimeout = Duration.ofSeconds(rabbitMQConfigProperties.deliveryTimeout)
        )
    }
}
