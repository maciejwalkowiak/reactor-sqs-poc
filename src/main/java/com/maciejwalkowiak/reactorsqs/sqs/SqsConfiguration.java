package com.maciejwalkowiak.reactorsqs.sqs;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@ConditionalOnProperty(name = SqsProperties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
public class SqsConfiguration {

	@Bean
	SqsAsyncClient sqsAsyncClient(SqsProperties sqsProperties) {
		return SqsAsyncClient.builder().region(sqsProperties.getDefaultListener().getRegion()).build();
	}

	@Bean
	SqsMessageHandler sqsMessageHandler(SqsProperties sqsProperties) {
		return new SqsMessageHandler(sqsAsyncClient(sqsProperties), sqsProperties, listenerNameResolver());
	}

	@Bean
	ListenerNameResolver listenerNameResolver() {
		return new BeanMethodNameListenerNameResolver();
	}
}
