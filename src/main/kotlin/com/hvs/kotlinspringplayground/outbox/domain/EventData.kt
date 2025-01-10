package com.hvs.kotlinspringplayground.outbox.domain

import com.fasterxml.jackson.databind.node.ContainerNode

data class EventData(
    val result: ContainerNode<*>?,
)
