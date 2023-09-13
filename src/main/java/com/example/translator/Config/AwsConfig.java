package com.example.translator.Config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;

import com.example.translator.Utils.AuthUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
    @Value("${aws.region}")
    private String REGION;

    AuthUtils authUtils;
    @Autowired
    public AwsConfig(AuthUtils authUtils){
        this.authUtils = authUtils;
    }
    @Bean
    AmazonTranslate getAmazonTranslate(){
        return AmazonTranslateClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(authUtils.getAwsCredentials().getCredentials()))
                .withRegion(REGION)
                .build();
    }

}
