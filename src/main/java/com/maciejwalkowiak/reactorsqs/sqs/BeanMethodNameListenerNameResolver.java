package com.maciejwalkowiak.reactorsqs.sqs;

public class BeanMethodNameListenerNameResolver implements ListenerNameResolver {
	@Override
	public String resolve(String beanName, String methodName, SqsListener sqsListener) {
		return !"".equals(sqsListener.value()) ? sqsListener.value() : beanName + "-" + methodName;
	}
}
