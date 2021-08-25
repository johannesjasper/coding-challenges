package com.mhp.coding.challenges.dependency.notifications

import com.mhp.coding.challenges.dependency.inquiry.events.InquiryCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EventListener(
    val emailHandler: EmailHandler,
    val pushNotificationHandler: PushNotificationHandler,
) {
    @EventListener
    fun handleInquiryCreated(inquiryCreatedEvent: InquiryCreatedEvent) {
        inquiryCreatedEvent.inquiry.let {
            println("Inquiry created $it")
            emailHandler.sendEmail(it)
            pushNotificationHandler.sendNotification(it)
        }
    }
}
