package com.hvs.kotlinspringplayground.outbox.publisher

import org.springframework.scheduling.annotation.Scheduled

class PollingPublisherJob(
    private val pollingPublisherService: PollingPublisherService
) {

    @Scheduled(fixedRateString = "\${outbox.publisher.polling.rate:PT10S}")
    fun scheduledTaskPollMessage() {
        pollingPublisherService.pollAndPublishAll()
    }
}
