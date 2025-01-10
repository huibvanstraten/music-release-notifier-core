package com.hvs.kotlinspringplayground.outbox.repository

import com.hvs.kotlinspringplayground.outbox.domain.jpa.OutboxMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface OutboxRepository: JpaRepository<OutboxMessage, UUID> {

    fun findOutboxMessage(): OutboxMessage?
}