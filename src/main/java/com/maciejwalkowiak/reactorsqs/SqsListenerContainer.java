package com.maciejwalkowiak.reactorsqs;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import org.springframework.beans.factory.DisposableBean;

public class SqsListenerContainer implements DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(SqsMessageHandler.class);
	private final String listenerName;
	private final SqsListener sqsListener;
	private final SqsAsyncClient sqs;
	private final Object target;
	private final Method targetMethod;
	private final SqsProperties sqsProperties;

	private Disposable disposable;
	private Scheduler scheduler;

	private String queueUrl;

	public SqsListenerContainer(String listenerName, SqsListener sqsListener, SqsAsyncClient sqs, Object target, Method targetMethod, SqsProperties sqsProperties) {
		this.listenerName = listenerName;
		this.sqsListener = sqsListener;
		this.sqs = sqs;
		this.target = target;
		this.targetMethod = targetMethod;
		this.sqsProperties = sqsProperties;
	}

	public void register() {
		this.scheduler = Schedulers.newBoundedElastic(sqsProperties.getMaxThreads(), sqsProperties.getMaxTasks(), listenerName);
		this.queueUrl = getQueueUrl();
		this.disposable = Mono.fromFuture(this::receiveMessages)
				.doOnNext(response -> logger.info("Received {} messages", response.messages().size()))
				.flatMapIterable(ReceiveMessageResponse::messages)
				.publishOn(scheduler)
				.repeat()
				.flatMap(m -> Mono.defer(() -> {
					try {
						targetMethod.invoke(target, m);
						if (sqsListener.deletionPolicy().deleteOnSuccess()) {
							return Mono.fromFuture(() -> deleteMessage(m));
						} else {
							return Mono.empty();
						}
					}
					catch (Exception e) {
						handleError(e, m);
						return Mono.empty();
					}
				}).subscribeOn(scheduler))
				.subscribe();
	}

	private String getQueueUrl() {
		try {
			return sqs.getQueueUrl(GetQueueUrlRequest.builder().queueName(sqsListener.value()).build())
					.get(5, TimeUnit.SECONDS)
					.queueUrl();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CompletableFuture<DeleteMessageResponse> deleteMessage(Message m) {
		return sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(m.receiptHandle()).build());
	}

	private void handleError(Throwable ex, Message m) {
		logger.error("Failed to handle message: {}", m, ex);
		if (sqsListener.deletionPolicy().deleteOnError()) {
			try {
				deleteMessage(m).get();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private CompletableFuture<ReceiveMessageResponse> receiveMessages() {
		logger.info("Fetching messages [{}}]", listenerName);
		return sqs.receiveMessage(ReceiveMessageRequest.builder()
				.queueUrl(queueUrl)
				.maxNumberOfMessages(sqsProperties.getMaxNumberOfMessages())
				.visibilityTimeout(sqsProperties.getVisibilityTimeout())
				.waitTimeSeconds(sqsProperties.getWaitTimeSeconds())
				.build());
	}


	@Override
	public void destroy() throws Exception {
		disposable.dispose();
		scheduler.dispose();
	}
}
