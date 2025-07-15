package com.hvs.outbox.infrastructure.persistence

import com.hvs.outbox.core.domain.jpa.OutboxMessage
import com.hvs.outbox.core.port.OutboxRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface PostgresOutboxRepository: OutboxRepository {

    @Query("SELECT * FROM outbox_message ORDER BY created_on ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    override fun findOutboxMessage(): OutboxMessage?
}