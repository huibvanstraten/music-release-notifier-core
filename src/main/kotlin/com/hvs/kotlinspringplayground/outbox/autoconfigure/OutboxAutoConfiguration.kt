package com.hvs.kotlinspringplayground.outbox.autoconfigure

import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import com.hvs.kotlinspringplayground.outbox.publisher.MessagePublisher
import com.hvs.kotlinspringplayground.outbox.publisher.PollingPublisherJob
import com.hvs.kotlinspringplayground.outbox.publisher.PollingPublisherService
import com.hvs.kotlinspringplayground.outbox.repository.OutboxRepository
import com.hvs.kotlinspringplayground.outbox.repository.PostgresOutboxRepository
import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import com.hvs.kotlinspringplayground.outbox.service.impl.CamundaOutboxService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.PlatformTransactionManager
import java.util.UUID

@Configuration
@EnableScheduling
@EnableJpaRepositories(basePackageClasses = [OutboxRepository::class])
@EntityScan("com.hvs.kotlinspringplayground.outbox.domain.jpa")
@AutoConfigureAfter(DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class)
class OutboxAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "outbox", name = ["database"], havingValue = "postgres")
    fun postgresOutboxMessageRepository(): JpaRepositoryFactoryBean<OutboxRepository, OutboxMessage, UUID> {
        return JpaRepositoryFactoryBean(PostgresOutboxRepository::class.java)
    }

    @Bean
    fun camundaOutboxService(
        outboxRepository: OutboxRepository,
        @Value("\${outbox.publisher.cloudevent-source}") cloudEventSource: String,
        ): OutboxService = CamundaOutboxService(
        outboxRepository = outboxRepository,
        cloudEventSource = cloudEventSource
    )

    @Bean
    fun pollingPublisherService(
        camundaOutboxService: CamundaOutboxService,
        messagePublisher: MessagePublisher,
        platformTransactionManager: PlatformTransactionManager
    ): PollingPublisherService {
        return PollingPublisherService(
            outboxService = camundaOutboxService,
            messagePublisher = messagePublisher,
            platformTransactionManager = platformTransactionManager,
        )
    }

    @Bean
    fun pollingPublisherJob(
        pollingPublisherService: PollingPublisherService
    ): PollingPublisherJob = PollingPublisherJob(
        pollingPublisherService = pollingPublisherService,
    )
}