package com.mhp.coding.challenges.retry.outbound;

import com.mhp.coding.challenges.retry.core.entities.EmailNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailCommand {
    @NotBlank
    private String recipient;

    @NotBlank
    private String subject;

    @NotBlank
    private String text;

    public static SendEmailCommand fromNotification(EmailNotification emailNotification) {
        return new SendEmailCommand(
                emailNotification.getRecipient(),
                emailNotification.getSubject(),
                emailNotification.getText()
        );
    }
}
