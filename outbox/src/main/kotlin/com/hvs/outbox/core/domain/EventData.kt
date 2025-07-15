package com.hvs.outbox.core.domain

import com.fasterxml.jackson.databind.node.ContainerNode

data class EventData(
    val result: ContainerNode<*>?,
)
