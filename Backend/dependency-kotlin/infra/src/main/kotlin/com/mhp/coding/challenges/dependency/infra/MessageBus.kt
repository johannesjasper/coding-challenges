package com.mhp.coding.challenges.dependency.infra

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class MessageBus(@Autowired private val applicationEventPublisher: ApplicationEventPublisher) {
    fun publish(event: ApplicationEvent) {
        // TODO store in event log
        // TODO use proper broker such as RMQ
        logger.debug { "Publishing event $event" }
        applicationEventPublisher.publishEvent(event)
    }
}
