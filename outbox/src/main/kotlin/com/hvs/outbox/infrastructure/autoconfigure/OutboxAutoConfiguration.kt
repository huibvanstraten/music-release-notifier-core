package com.hvs.outbox.infrastructure.autoconfigure

import com.hvs.outbox.core.domain.jpa.OutboxMessage
import com.hvs.outbox.core.port.MessagePublisher
import com.hvs.outbox.infrastructure.PollingPublisherJob
import com.hvs.outbox.infrastructure.usecase.PollingPublisherImplService
import com.hvs.outbox.core.port.OutboxRepository
import com.hvs.outbox.infrastructure.persistence.PostgresOutboxRepository
import com.hvs.outbox.application.usecase.OutboxService
import com.hvs.outbox.application.usecase.PollingPublisherService
import com.hvs.outbox.infrastructure.usecase.CamundaOutboxService
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
@EntityScan("com.hvs.outbox.core.domain.jpa")
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
        ): CamundaOutboxService = CamundaOutboxService(
        outboxRepository = outboxRepository,
        cloudEventSource = cloudEventSource
    )

    @Bean
    fun pollingPublisherService(
        outboxService: OutboxService,
        messagePublisher: MessagePublisher,
        platformTransactionManager: PlatformTransactionManager
    ): PollingPublisherImplService {
        return PollingPublisherImplService(
            outboxService = outboxService,
            messagePublisher = messagePublisher,
            platformTransactionManager = platformTransactionManager,
        )
    }

    @Bean
    fun pollingPublisherJob(
        pollingPublisherService: PollingPublisherService,
    ): PollingPublisherJob = PollingPublisherJob(
        pollingPublisherService = pollingPublisherService,
    )
}