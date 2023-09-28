package com.example.translator.Config;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.example.translator.Utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;


@Configuration
public class AwsConfig {
    @Value("${aws.region}")
    private String REGION;

    AuthUtils authUtils;

    @Autowired
    public AwsConfig(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    @Bean
    AmazonTranslate getAmazonTranslate() {
        return AmazonTranslateClient.builder()
                .withCredentials(authUtils.getAwsCredentials())
                .withRegion(REGION)
                .build();
    }

    @Bean
    SqsClient getSqsClient() {
        return SqsClient
                .builder()
                .credentialsProvider(authUtils.getAwsCredentialsV2())
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    SqsAsyncClient getSqsAsyncClient() {
        return SqsAsyncClient.builder()
                .credentialsProvider(authUtils.getAwsCredentialsV2())
                .region(Region.US_EAST_1).build();
    }


}
