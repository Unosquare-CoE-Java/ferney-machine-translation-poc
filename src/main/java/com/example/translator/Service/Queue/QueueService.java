package com.example.translator.Service.Queue;

import com.example.translator.DTO.TranslationDTO;
import com.example.translator.Exceptions.NoResponseException;
import com.example.translator.Task.PollSqsTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class QueueService implements PollPerformedListener {
    private Map<String, TranslationDTO> responses;
    @Value("${responseTimeout}")
    private Integer responseTimeout;
    @Value("${responseEnquiryTime}")
    private Integer responseEnquiryTime;
    @Value("${aws.sqs.url}")
    private String QUEUE_URL;
    @Value("${poll.delayBetweenPolls}")
    private Integer DELAY_BETWEEN_POLLS;
    @Value("${poll.initialDelay}")
    private Integer INITIAL_DELAY_POLLING;
    @Value("${poll.maxMessagesPerPoll}")
    private Integer MAX_MESSAGES_PER_POLL;
    private final SqsClient sqsClient;
    private final PollSqsTask pollSqsTask;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService executorService;

    @Autowired
    public QueueService(SqsClient sqsClient,
                        PollSqsTask pollSqsTask,
                        ObjectMapper objectMapper,
                        @Value("${poll.initialDelay}") Integer initialDelayPolling,
                        @Value("${poll.delayBetweenPolls}") Integer delayBetweenPolls) {

        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;

        responses = new ConcurrentHashMap<String, TranslationDTO>();
        this.pollSqsTask = pollSqsTask;
        pollSqsTask.setResponses(responses);
        pollSqsTask.setPollPerformedEvent(this);

        executorService = Executors.newSingleThreadScheduledExecutor();
        startPolling(initialDelayPolling, delayBetweenPolls);
    }

    public TranslationDTO getResponse(String messageId) throws InterruptedException, NoResponseException {

        Integer responseTime = 0;
        while (!responses.containsKey(messageId) || responseTime > responseTimeout) {
            Thread.sleep(responseEnquiryTime);
            responseTime += responseEnquiryTime;
        }
        if (!responses.containsKey(messageId)) {
            throw new NoResponseException(messageId);
        }
        TranslationDTO response = responses.get(messageId);
        responses.remove(messageId);
        if (responses.size() == 0 && executorService.isShutdown()) {
            startPolling(this.INITIAL_DELAY_POLLING, this.DELAY_BETWEEN_POLLS);
        }
        return response;
    }

    public String sendRequestToQueue(TranslationDTO request) throws JsonProcessingException {

        SendMessageRequest messageRequest = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(objectMapper.writeValueAsString(request))
                .build();

        return sqsClient.sendMessage(messageRequest).messageId();
    }

    public void pollPerformed() {
        if (responses.size() >= MAX_MESSAGES_PER_POLL) {
            stopPolling();
        }
    }

    public void startPolling(Integer InitialDelayPolling, Integer DelayBetweenPolls) {
        executorService.scheduleWithFixedDelay(pollSqsTask
                , InitialDelayPolling
                , DelayBetweenPolls
                , TimeUnit.MILLISECONDS);
    }

    public void stopPolling() {
        executorService.shutdown();
    }

}
