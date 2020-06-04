package com.maciejwalkowiak.reactorsqs;

import com.maciejwalkowiak.reactorsqs.sqs.BeanMethodNameListenerNameResolver;
import com.maciejwalkowiak.reactorsqs.sqs.ListenerNameResolver;
import com.maciejwalkowiak.reactorsqs.sqs.SqsMessageHandler;
import com.maciejwalkowiak.reactorsqs.sqs.SqsProperties;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReactorSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactorSqsApplication.class, args);
	}
}

