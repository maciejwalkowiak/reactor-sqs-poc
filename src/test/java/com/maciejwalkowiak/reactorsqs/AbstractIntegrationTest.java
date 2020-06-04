package com.maciejwalkowiak.reactorsqs;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.lifecycle.Startables;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
abstract class AbstractIntegrationTest {
	private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

	static class Initializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		static LocalStackContainer localstack = new LocalStackContainer().withServices(Service.SQS).withReuse(true);

		public static Map<String, String> getProperties() {
			Startables.deepStart(Stream.of(localstack)).join();

			Map<String, String> properties = new HashMap<>();
			properties.put("cloud.aws.sqs.default-listener.endpoint",
					localstack.getEndpointOverride(Service.SQS).toString());

			return properties;
		}

		@Override
		public void initialize(ConfigurableApplicationContext context) {
			ConfigurableEnvironment env = context.getEnvironment();
			env.getPropertySources().addFirst(new MapPropertySource(
					"testcontainers",
					(Map) getProperties()
			));

			logger.info("SQS exposed under {}", localstack.getEndpointOverride(Service.SQS));

			// create queues
			AmazonSQS sqs = AmazonSQSClientBuilder
					.standard()
					.withEndpointConfiguration(localstack.getEndpointConfiguration(Service.SQS)).build();
			sqs.createQueue("my-new-queue");
		}
	}
}
