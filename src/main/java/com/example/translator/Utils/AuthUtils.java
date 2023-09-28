package com.example.translator.Utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.example.translator.DTO.AwsKeysDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.util.Collections;


@Component
public class AuthUtils {

    @Value("${aws.keys}")
    private String URL_KEYS;

    public AWSStaticCredentialsProvider getAwsCredentials() {
        AwsKeysDTO credentials = performCredentialsRequest();
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        credentials.getAccessKey()
                        , credentials.getSecretKey()));
    }

    private AwsKeysDTO performCredentialsRequest() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<AwsKeysDTO> result =
                restTemplate.exchange(URL_KEYS, HttpMethod.GET, entity, AwsKeysDTO.class);
        return result.getBody();
    }

    public StaticCredentialsProvider getAwsCredentialsV2() {
        AwsKeysDTO credentials = performCredentialsRequest();
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(credentials.getAccessKey(),
                credentials.getSecretKey());
        return StaticCredentialsProvider.create(awsBasicCredentials);
    }
}
