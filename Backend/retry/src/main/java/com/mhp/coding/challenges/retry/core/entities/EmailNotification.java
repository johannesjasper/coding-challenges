package com.mhp.coding.challenges.retry.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotification {

    @NotBlank
    private String recipient;

    @NotBlank
    private String subject;

    @NotBlank
    private String text;
}
