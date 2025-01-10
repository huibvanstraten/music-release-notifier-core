package com.hvs.kotlinspringplayground.outbox.domain

import com.fasterxml.jackson.databind.node.ContainerNode
import java.time.LocalDateTime
import java.util.UUID

abstract class BaseEvent(
    val id: UUID = UUID.randomUUID(),
    val type: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val resultType: String?,
    val resultId: String?,
    val result: ContainerNode<*>?,
)