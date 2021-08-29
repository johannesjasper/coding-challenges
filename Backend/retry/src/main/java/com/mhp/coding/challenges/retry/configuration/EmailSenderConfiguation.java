package com.mhp.coding.challenges.retry.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static java.lang.Math.pow;


/**
 * Sets up various RabbitMQ queues and exchanges.
 * This allows for async email sending and deplayed retries.
 * This is also tolerant to application restarts and the deployment of multiple instances of the service.
 * <p>
 * Failed commands are sent to a temporary DLQ.
 * From there they are sent to the original email command queue via a delayed exchange with increasing backoffs.
 * After the max amount of retries is reached, the command is sent to a final DLQ.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class EmailSenderConfiguation {

    public static final String EMAIL_COMMAND_QUEUE_NAME = "email-commands";

    public static final String TMP_DLQ = EMAIL_COMMAND_QUEUE_NAME + ".tmp_dlq";
    private static final String FINAL_DLQ = EMAIL_COMMAND_QUEUE_NAME + ".final_dlq";
    private static final String DLX = EMAIL_COMMAND_QUEUE_NAME + ".dlx";
    public static final String DELAY_EXCHANGE = "dlq_rerouter";

    private static final String X_RETRIES_HEADER = "x-retries";

    @Value("${email.retryBackoffMillis}")
    private int retryBackoffMillis = 5000;
    @Value("${email.maxRetries}")
    private int maxRetries = 5;

    private final RabbitTemplate rabbitTemplate;

    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX);
    }

    /**
     * Defines a delayed exchange, see https://github.com/rabbitmq/rabbitmq-delayed-message-exchange
     */
    @Bean
    public DirectExchange delayExchange() {
        DirectExchange exchange = new DirectExchange(DELAY_EXCHANGE);
        exchange.setDelayed(true);
        return exchange;
    }


    /**
     * Defines the email command queue. Forwards failed messages to DLX
     */
    @Bean
    Queue emailCommandsQueue() {
        return QueueBuilder.durable(EMAIL_COMMAND_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX)
                .build();
    }

    @Bean
    Queue temporaryDLQ() {
        return QueueBuilder.durable(TMP_DLQ).build();
    }

    /**
     * Forwards messages from DLX to the temporary DLQ.
     */
    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(temporaryDLQ()).to(deadLetterExchange());
    }

    @Bean
    public Queue finalDLQ() {
        return new Queue(FINAL_DLQ);
    }


    /**
     * Reroutes commands from the delayed exchange back to the original command queue.
     */
    @Bean
    public Binding delayedBinding() {
        return BindingBuilder.bind(new Queue(EMAIL_COMMAND_QUEUE_NAME)).to(delayExchange()).with(EMAIL_COMMAND_QUEUE_NAME);
    }


    /**
     * Reads failed messages from the temporary DLQ.
     * Tracks the number of retries and applies an exponential backoff.
     * Sends the message to the delayed exchange or the final DLQ, depending on the number of retires.
     */
    @RabbitListener(queues = TMP_DLQ)
    public void rePublish(Message failedMessage) {
        Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
        int retriesHeader = (int) headers.getOrDefault(X_RETRIES_HEADER, 0);

        if (retriesHeader < this.maxRetries) {
            int backOffMillis = this.retryBackoffMillis * (int) pow(2, retriesHeader);
            headers.put(X_RETRIES_HEADER, retriesHeader + 1);
            headers.put("x-delay", backOffMillis);

            log.info("Retrying failed message with {} previous retries after {}ms", retriesHeader, backOffMillis);
            this.rabbitTemplate.send(DELAY_EXCHANGE, EMAIL_COMMAND_QUEUE_NAME, failedMessage);
        } else {
            log.warn("Giving up retrying failed message with {} previous retries", retriesHeader);
            this.rabbitTemplate.send(FINAL_DLQ, failedMessage);
        }
    }
}
