package com.hvs.kotlinspringplayground.outbox.repository

import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface PostgresOutboxRepository: OutboxRepository {

    @Query("SELECT * FROM outbox_message ORDER BY created_on ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    override fun findOutboxMessage(): OutboxMessage?
}