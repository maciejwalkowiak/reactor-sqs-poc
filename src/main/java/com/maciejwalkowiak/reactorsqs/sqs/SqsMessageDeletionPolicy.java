package com.maciejwalkowiak.reactorsqs.sqs;

public enum SqsMessageDeletionPolicy {
	ALWAYS,
	NEVER,
	ON_SUCCESS;

	public boolean deleteOnSuccess() {
		return this == ALWAYS || this == ON_SUCCESS;
	}

	public boolean deleteOnError() {
		return this == ALWAYS;
	}
}
