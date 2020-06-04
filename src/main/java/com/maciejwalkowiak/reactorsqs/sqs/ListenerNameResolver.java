package com.maciejwalkowiak.reactorsqs.sqs;

public interface ListenerNameResolver {
	String resolve(String beanName, String methodName, SqsListener sqsListener);
}
