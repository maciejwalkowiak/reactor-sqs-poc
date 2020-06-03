package com.maciejwalkowiak.reactorsqs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ReflectionUtils.MethodFilter;

public class SqsMessageHandler implements BeanPostProcessor, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(SqsMessageHandler.class);

	private final SqsAsyncClient sqs;
	private final List<SqsListenerContainer> containers = new ArrayList<>();
	private final SqsProperties sqsProperties;

	public SqsMessageHandler(SqsAsyncClient sqs, SqsProperties sqsProperties) {
		this.sqs = sqs;
		this.sqsProperties = sqsProperties;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Set<Method> methods = MethodIntrospector.selectMethods(bean.getClass(),
				(MethodFilter) method -> method.isAnnotationPresent(SqsListener.class));

		for (Method method : methods) {
			String listenerName = beanName + "#" + method.getName();
			logger.info("Registering SqsListenerContainer: {}", listenerName);
			SqsListenerContainer container = new SqsListenerContainer(
					listenerName,
					method.getDeclaredAnnotation(SqsListener.class),
					sqs,
					bean,
					method,
					sqsProperties
			);
			container.register();
			containers.add(container);
		}

		return bean;
	}

	@Override
	public void destroy() throws Exception {
		for (SqsListenerContainer container : this.containers) {
			container.destroy();
		}
	}
}
