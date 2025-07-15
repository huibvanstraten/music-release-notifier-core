package com.hvs.outbox.application.usecase

interface PollingPublisherService {

    fun pollAndPublishAll()
}