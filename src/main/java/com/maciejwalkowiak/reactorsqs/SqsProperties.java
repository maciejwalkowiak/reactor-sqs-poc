package com.maciejwalkowiak.reactorsqs;

import software.amazon.awssdk.regions.Region;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws.sqs")
public class SqsProperties {
	private Region region;
	private int maxThreads = 20;
	private int maxTasks = 200;
	private int maxNumberOfMessages = 10;
	private int visibilityTimeout = 5;
	private int waitTimeSeconds = 5;

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getMaxTasks() {
		return maxTasks;
	}

	public void setMaxTasks(int maxTasks) {
		this.maxTasks = maxTasks;
	}

	public int getMaxNumberOfMessages() {
		return maxNumberOfMessages;
	}

	public void setMaxNumberOfMessages(int maxNumberOfMessages) {
		this.maxNumberOfMessages = maxNumberOfMessages;
	}

	public int getVisibilityTimeout() {
		return visibilityTimeout;
	}

	public void setVisibilityTimeout(int visibilityTimeout) {
		this.visibilityTimeout = visibilityTimeout;
	}

	public int getWaitTimeSeconds() {
		return waitTimeSeconds;
	}

	public void setWaitTimeSeconds(int waitTimeSeconds) {
		this.waitTimeSeconds = waitTimeSeconds;
	}
}
