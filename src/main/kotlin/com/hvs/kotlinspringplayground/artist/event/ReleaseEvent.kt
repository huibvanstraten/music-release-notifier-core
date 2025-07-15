package com.hvs.kotlinspringplayground.artist.event

import com.fasterxml.jackson.databind.node.ObjectNode
import com.hvs.outbox.core.domain.BaseEvent

class ReleaseEvent(id: String, release: ObjectNode): BaseEvent(
    type = "com.hvs.musicreleasenotifiercore.spotify",
    resultType = "release",
    resultId = id,
    result = release
)