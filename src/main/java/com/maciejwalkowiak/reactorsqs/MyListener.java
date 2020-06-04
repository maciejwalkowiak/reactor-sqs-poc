package com.maciejwalkowiak.reactorsqs;

import java.util.concurrent.ThreadLocalRandom;

import com.maciejwalkowiak.reactorsqs.sqs.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.model.Message;

import org.springframework.stereotype.Component;

@Component
public class MyListener {
	private static final Logger LOG = LoggerFactory.getLogger(MyListener.class);

	@SqsListener
	public void handle(Message message) throws InterruptedException {
		LOG.info("Handling message: {}", message.body());
		Thread.sleep(ThreadLocalRandom.current().nextInt(10, 3000));
	}
}
