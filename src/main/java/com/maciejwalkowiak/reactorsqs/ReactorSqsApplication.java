package com.maciejwalkowiak.reactorsqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactorSqsApplication {

	public static void main(String[] args) {
		createSpringApplication().run(args);
	}

	public static SpringApplication createSpringApplication() {
		return new SpringApplication(ReactorSqsApplication.class);
	}
}

