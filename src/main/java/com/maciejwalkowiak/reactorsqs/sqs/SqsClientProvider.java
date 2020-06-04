package com.maciejwalkowiak.reactorsqs.sqs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

public class SqsClientProvider {
	private final Map<ClientKey, SqsAsyncClient> clients = new HashMap<>();

	SqsAsyncClient getClient(Region region, URI endpoint) {
		ClientKey key = new ClientKey(region, endpoint);
		SqsAsyncClient sqsAsyncClient = clients.get(key);
		if (sqsAsyncClient == null) {
			sqsAsyncClient = buildClient(key);
			this.clients.put(key, sqsAsyncClient);
		}
		return sqsAsyncClient;
	}

	private SqsAsyncClient buildClient(ClientKey key) {
		SqsAsyncClientBuilder builder = SqsAsyncClient.builder();
		if (key.endpoint != null) {
			builder.endpointOverride(key.endpoint);
		}
		return builder.region(key.region).build();
	}

	private static class ClientKey {
		private final Region region;
		private final URI endpoint;

		public ClientKey(Region region, URI endpoint) {
			this.region = region;
			this.endpoint = endpoint;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ClientKey clientKey = (ClientKey) o;
			return Objects.equals(region, clientKey.region) &&
					Objects.equals(endpoint, clientKey.endpoint);
		}

		@Override
		public int hashCode() {
			return Objects.hash(region, endpoint);
		}
	}
}
