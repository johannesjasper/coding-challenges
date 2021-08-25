package com.mhp.coding.challenges.dependency.inquiry

import com.mhp.coding.challenges.dependency.infra.MessageBus
import com.mhp.coding.challenges.dependency.inquiry.events.InquiryCreatedEvent
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class InquiryService(
    @Autowired private val messageBus: MessageBus,
) {
    fun create(inquiry: Inquiry) {
        messageBus.publish(InquiryCreatedEvent(this, inquiry))
        logger.info {
            "User sent inquiry: $inquiry"
        }
    }
}

data class Inquiry(
    var username: String,
    var recipient: String,
    var text: String,
)
