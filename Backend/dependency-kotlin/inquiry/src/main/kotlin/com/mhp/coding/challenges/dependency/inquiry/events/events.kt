package com.mhp.coding.challenges.dependency.inquiry.events

import com.mhp.coding.challenges.dependency.inquiry.Inquiry
import org.springframework.context.ApplicationEvent

class InquiryCreatedEvent(origin: Any, val inquiry: Inquiry) : ApplicationEvent(origin)
