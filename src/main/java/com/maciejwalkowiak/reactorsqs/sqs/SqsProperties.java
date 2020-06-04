package com.maciejwalkowiak.reactorsqs.sqs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.tools.Diagnostic.Kind;

import software.amazon.awssdk.regions.Region;

import org.springframework.boot.configurationprocessor.metadata.InvalidConfigurationMetadataException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SqsProperties.PREFIX)
public class SqsProperties {
	public static final String PREFIX = "cloud.aws.sqs";
	private boolean enabled = true;
	private DefaultListenerProperties defaultListener = new DefaultListenerProperties();
	private Map<String, ListenerProperties> listeners = new HashMap<>();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public DefaultListenerProperties getDefaultListener() {
		return defaultListener;
	}

	public void setDefaultListener(DefaultListenerProperties defaultListener) {
		this.defaultListener = defaultListener;
	}

	public Map<String, ListenerProperties> getListeners() {
		return listeners;
	}

	public void setListeners(Map<String, ListenerProperties> listeners) {
		this.listeners = listeners;
	}

	public ListenerProperties getListenerProperties(String listenerName) {
		ListenerProperties listenerProperties = this.listeners.get(listenerName);
		if (listenerProperties != null) {
			return this.defaultListener.apply(listenerProperties);
		} else {
			throw new InvalidConfigurationMetadataException("asd", Kind.ERROR);
		}
	}

	public static class DefaultListenerProperties {
		private Region region;
		private URI endpoint;
		private SqsMessageDeletionPolicy deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS;
		private int maxThreads = 20;
		private int maxTasks = 200;
		private int maxNumberOfMessages = 10;
		private int visibilityTimeout = 5;
		private int waitTimeSeconds = 5;

		public ListenerProperties apply(ListenerProperties listenerProperties) {
			ListenerProperties target = new ListenerProperties();
			target.setRegion(listenerProperties.getRegion() != null ? listenerProperties.getRegion() : this.region);
			target.setQueueName(listenerProperties.getQueueName());
			target.setDeletionPolicy(listenerProperties.getDeletionPolicy() != null ? listenerProperties.getDeletionPolicy() : this.deletionPolicy);
			target.setMaxThreads(listenerProperties.getMaxThreads());
			target.setMaxTasks(listenerProperties.getMaxTasks());
			target.setMaxNumberOfMessages(listenerProperties.getMaxNumberOfMessages());
			target.setVisibilityTimeout(listenerProperties.getVisibilityTimeout());
			target.setWaitTimeSeconds(listenerProperties.getWaitTimeSeconds());
			target.setEndpoint(listenerProperties.getEndpoint() != null ? listenerProperties.getEndpoint() : this.endpoint);
			return target;
		}

		public SqsMessageDeletionPolicy getDeletionPolicy() {
			return deletionPolicy;
		}

		public void setDeletionPolicy(SqsMessageDeletionPolicy deletionPolicy) {
			this.deletionPolicy = deletionPolicy;
		}

		public Region getRegion() {
			return region;
		}

		public void setRegion(Region region) {
			this.region = region;
		}

		public URI getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(URI endpoint) {
			this.endpoint = endpoint;
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

	public static class ListenerProperties extends DefaultListenerProperties{
		private String queueName;

		public String getQueueName() {
			return queueName;
		}

		public void setQueueName(String queueName) {
			this.queueName = queueName;
		}
	}
}
