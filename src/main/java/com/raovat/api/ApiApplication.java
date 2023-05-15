package com.raovat.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
		System.out.println("""
                🚀 Api doc ready at http://localhost:8080/swagger-ui/index.html
                """);
	}

}
