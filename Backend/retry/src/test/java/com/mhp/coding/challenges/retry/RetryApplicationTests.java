package com.mhp.coding.challenges.retry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhp.coding.challenges.retry.configuration.EmailSenderConfiguation;
import com.mhp.coding.challenges.retry.core.entities.EmailNotification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"email.retryBackoffMillis=1"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
class RetryApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JavaMailSender emailSender;

    private final EmailNotification notification = new EmailNotification("test@email.com", "subject", "text");

    @SneakyThrows
    private void sendRequest() {
        this.mvc.perform(MockMvcRequestBuilders.post("/v1/emails")
                        .content(mapper.writeValueAsString(notification))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void sendsEmail() {
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        sendRequest();

        verify(emailSender, timeout(1000).times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendsEmail_withOneRetry() {
        doThrow(RuntimeException.class).doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        sendRequest();

        verify(emailSender, timeout(1000).times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendsEmail_withMaxRetries() {
        doThrow(RuntimeException.class).when(emailSender).send(any(SimpleMailMessage.class));

        sendRequest();

        verify(emailSender, timeout(1000).times(5)).send(any(SimpleMailMessage.class));
    }
}
