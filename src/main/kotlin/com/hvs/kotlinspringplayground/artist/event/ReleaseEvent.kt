package com.hvs.kotlinspringplayground.artist.event

import com.fasterxml.jackson.databind.node.ObjectNode
import com.hvs.kotlinspringplayground.outbox.domain.BaseEvent

class ReleaseEvent(id: String, release: ObjectNode): BaseEvent(
    type = "com.hvs.kotlinspringplayground.tidal",
    resultType = "release",
    resultId = id,
    result = release
)