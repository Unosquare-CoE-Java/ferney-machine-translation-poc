package com.example.translator.Service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
public class TranslatorService {
    @Value("${amazon.region}")
    private String REGION;
    @Value("${amazon.accessKey}")
    private String ACCESS_KEY;
    @Value("${amazon.secretKey}")
    private String SECRET_KEY;
    public String translate(String source, String target, String parse){
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonTranslate translate = AmazonTranslateClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(REGION)
                .build();
        TranslateTextRequest request = new TranslateTextRequest()
                .withText(parse)
                .withSourceLanguageCode(source)
                .withTargetLanguageCode(target);
        TranslateTextResult result  = translate.translateText(request);
        return result.getTranslatedText();
    }
}
