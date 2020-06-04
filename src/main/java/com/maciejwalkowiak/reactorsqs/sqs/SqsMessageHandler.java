package com.maciejwalkowiak.reactorsqs.sqs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.maciejwalkowiak.reactorsqs.sqs.SqsProperties.ListenerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ReflectionUtils.MethodFilter;

public class SqsMessageHandler implements BeanPostProcessor, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(SqsMessageHandler.class);

	private final SqsClientProvider sqs;
	private final List<SqsListenerContainer> containers = new ArrayList<>();
	private final SqsProperties sqsProperties;
	private final ListenerNameResolver listenerNameResolver;
	private final AwsRegionProvider awsRegionProvider;

	public SqsMessageHandler(SqsClientProvider sqs, SqsProperties sqsProperties, ListenerNameResolver listenerNameResolver, AwsRegionProvider awsRegionProvider) {
		this.sqs = sqs;
		this.sqsProperties = sqsProperties;
		this.listenerNameResolver = listenerNameResolver;
		this.awsRegionProvider = awsRegionProvider;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Set<Method> methods = MethodIntrospector.selectMethods(bean.getClass(),
				(MethodFilter) method -> method.isAnnotationPresent(SqsListener.class));

		for (Method method : methods) {
			SqsListener declaredAnnotation = method.getDeclaredAnnotation(SqsListener.class);
			String listenerName = listenerNameResolver.resolve(beanName, method.getName(), declaredAnnotation);
			logger.info("Registering SqsListenerContainer: {}", listenerName);

			ListenerProperties listenerProperties = sqsProperties.getListenerProperties(listenerName);
			Region region = resolveRegion(listenerProperties);

			SqsListenerContainer container = new SqsListenerContainer(
					listenerName,
					listenerProperties,
					sqs.getClient(region, listenerProperties.getEndpoint()),
					bean,
					method
			);
			container.register();
			containers.add(container);
		}

		return bean;
	}

	private Region resolveRegion(ListenerProperties listenerProperties) {
		if (listenerProperties.getRegion() != null) {
			return listenerProperties.getRegion();
		} else {
			return this.awsRegionProvider.getRegion();
		}
	}

	@Override
	public void destroy() throws Exception {
		for (SqsListenerContainer container : this.containers) {
			container.destroy();
		}
	}
}
