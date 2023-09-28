package com.example.translator.Service;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.example.translator.DTO.TranslationDTO;
import com.example.translator.Exceptions.NoResponseException;
import com.example.translator.Service.Queue.QueueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TranslatorService {

    private final AmazonTranslate amazonTranslate;

    private final QueueService queueService;

    @Value("${aws.sqs.url}")
    private String QUEUE_URL;

    @Autowired
    public TranslatorService(AmazonTranslate amazonTranslate, QueueService queueService) {
        this.amazonTranslate = amazonTranslate;
        this.queueService = queueService;
    }

    public TranslationDTO translate(TranslationDTO tr) {
        TranslateTextRequest request = new TranslateTextRequest()
                .withText(tr.getTextToTranslate())
                .withSourceLanguageCode(tr.getSourceLanguage())
                .withTargetLanguageCode(tr.getTargetLanguage());

        TranslateTextResult result = amazonTranslate.translateText(request);
        tr.setTranslatedText(result.getTranslatedText());
        tr.setTimestamp(LocalDateTime.now());
        return tr;
    }

    public TranslationDTO requestTraslation(TranslationDTO translationRequest) throws NoResponseException, InterruptedException, JsonProcessingException {
        String requestId = queueService.sendRequestToQueue(translationRequest);
        TranslationDTO request = queueService.getResponse(requestId);
        TranslationDTO translated = translate(request);
        return translate(request);
    }


}
