package com.hvs.outbox.core.port

import com.hvs.outbox.core.domain.jpa.OutboxMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface OutboxRepository: JpaRepository<OutboxMessage, UUID> {

    fun findOutboxMessage(): OutboxMessage?
}