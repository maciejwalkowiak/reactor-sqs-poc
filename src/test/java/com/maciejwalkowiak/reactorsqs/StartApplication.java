package com.maciejwalkowiak.reactorsqs;

import org.springframework.boot.SpringApplication;

public class StartApplication {

	public static void main(String[] args) {
		SpringApplication application = ReactorSqsApplication.createSpringApplication();
		application.addInitializers(new AbstractIntegrationTest.Initializer());
		application.run(args);
	}
}
