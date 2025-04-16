package com.innominds.nokia.ngds.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.innominds.nokia.ngds.*"})
public class NokiaNgDsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NokiaNgDsApplication.class, args);
	}
}


