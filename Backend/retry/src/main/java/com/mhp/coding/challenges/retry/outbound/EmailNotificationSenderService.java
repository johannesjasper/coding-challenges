package com.mhp.coding.challenges.retry.outbound;

import com.mhp.coding.challenges.retry.configuration.EmailSenderConfiguation;
import com.mhp.coding.challenges.retry.core.entities.EmailNotification;
import com.mhp.coding.challenges.retry.core.outbound.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSenderService implements NotificationSender {

    private static final String SENDER_ADDRESS = "info@mhp.com";

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public void sendEmail(@Valid @NotNull EmailNotification emailNotification) {
        log.info("Sending command to send email for {}", emailNotification.getRecipient());
        SendEmailCommand command = SendEmailCommand.fromNotification(emailNotification);
        rabbitTemplate.convertAndSend(EmailSenderConfiguation.EMAIL_COMMAND_QUEUE_NAME, command);
    }

    @RabbitListener(queues = EmailSenderConfiguation.EMAIL_COMMAND_QUEUE_NAME)
    public void receiveMessage(SendEmailCommand command) {
        log.info("Received SendEmailCommand '{}", command);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(SENDER_ADDRESS);
            mailMessage.setTo(command.getRecipient());
            mailMessage.setSubject(command.getSubject());
            mailMessage.setText(command.getText());

            log.info("Sending email to {}", command.getRecipient());
            mailSender.send(mailMessage);
        } catch (Exception e) {
            log.error("Failed to send email to recipient: {}", command.getRecipient(), e);
            throw e;
        }

    }

}
