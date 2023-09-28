package com.example.translator.Task;

import com.example.translator.DTO.TranslationDTO;
import com.example.translator.Service.Queue.PollPerformedListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PollSqsTask implements Runnable {

    @Value("${aws.sqs.url}")
    private String QUEUE_URL;

    @Value("${poll.maxMessagesPerPoll}")
    private Integer MAX_MESSAGES_PER_POLL;

    @Value("${poll.timeSeconds}")
    private Integer POLL_TIME;

    private final SqsClient sqsClient;

    private final ObjectMapper objectMapper;

    @Setter
    private PollPerformedListener pollPerformedEvent;

    @Setter
    private Map<String, TranslationDTO> responses;

    @Autowired
    public PollSqsTask(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        ReceiveMessageRequest incomingMsg = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(MAX_MESSAGES_PER_POLL)
                .waitTimeSeconds(POLL_TIME)
                .build();
        log.info("Sent poll to SQS");
        List<Message> receivedMessages = sqsClient.receiveMessage(incomingMsg).messages();

        receivedMessages.stream().map(m -> DeleteMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .receiptHandle(m.receiptHandle())
                        .build())
                .forEach(sqsClient::deleteMessage);

        receivedMessages.forEach(m -> {
            try {
                TranslationDTO request = objectMapper.readValue(m.body(), TranslationDTO.class);
                String messageId = m.messageId();
                Optional.of(responses).ifPresent(r -> r.put(messageId, request));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        Optional.of(pollPerformedEvent).ifPresent(PollPerformedListener::pollPerformed);
    }
}
