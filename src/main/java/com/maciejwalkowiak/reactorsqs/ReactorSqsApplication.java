package com.maciejwalkowiak.reactorsqs;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(SqsProperties.class)
public class ReactorSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactorSqsApplication.class, args);
	}

	@Bean
	SqsAsyncClient sqsAsyncClient(SqsProperties sqsProperties) {
		return SqsAsyncClient.builder().region(sqsProperties.getRegion()).build();
	}

	@Bean
	SqsMessageHandler sqsMessageHandler(SqsProperties sqsProperties) {
		return new SqsMessageHandler(sqsAsyncClient(sqsProperties), sqsProperties);
	}

}

