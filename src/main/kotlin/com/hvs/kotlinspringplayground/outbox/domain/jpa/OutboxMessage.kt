package com.hvs.kotlinspringplayground.outbox.domain.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "outbox_message")
class OutboxMessage(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "message", nullable = false)
    val message: String,

    @Column(name = "created_on", nullable = false)
    val createdOn: LocalDateTime = LocalDateTime.now()
)