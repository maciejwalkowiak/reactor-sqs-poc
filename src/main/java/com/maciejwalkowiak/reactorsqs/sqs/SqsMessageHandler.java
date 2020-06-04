package com.maciejwalkowiak.reactorsqs.sqs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.maciejwalkowiak.reactorsqs.sqs.SqsProperties.ListenerProperties;
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
	private final ListenerNameResolver listenerNameResolver;

	public SqsMessageHandler(SqsAsyncClient sqs, SqsProperties sqsProperties, ListenerNameResolver listenerNameResolver) {
		this.sqs = sqs;
		this.sqsProperties = sqsProperties;
		this.listenerNameResolver = listenerNameResolver;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Set<Method> methods = MethodIntrospector.selectMethods(bean.getClass(),
				(MethodFilter) method -> method.isAnnotationPresent(SqsListener.class));

		for (Method method : methods) {
			SqsListener declaredAnnotation = method.getDeclaredAnnotation(SqsListener.class);
			String listenerName = listenerNameResolver.resolve(beanName, method.getName(), declaredAnnotation);
			ListenerProperties listenerProperties = sqsProperties.getListenerProperties(listenerName);
			logger.info("Registering SqsListenerContainer: {}", listenerName);
			SqsListenerContainer container = new SqsListenerContainer(
					listenerName,
					listenerProperties,
					sqs,
					bean,
					method
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
