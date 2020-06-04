package com.maciejwalkowiak.reactorsqs.sqs;

import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@ConditionalOnProperty(name = SqsProperties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
public class SqsConfiguration {

	private final SqsProperties sqsProperties;

	public SqsConfiguration(SqsProperties sqsProperties) {
		this.sqsProperties = sqsProperties;
	}

	@Bean
	SqsClientProvider sqsClientProvider() {
		return new SqsClientProvider();
	}

	@Bean
	SqsMessageHandler sqsMessageHandler() {
		return new SqsMessageHandler(sqsClientProvider(), sqsProperties, listenerNameResolver(), regionProvider());
	}

	@Bean
	ListenerNameResolver listenerNameResolver() {
		return new BeanMethodNameListenerNameResolver();
	}

	@Bean
	AwsRegionProvider regionProvider() {
		return new DefaultAwsRegionProviderChain();
	}
}
