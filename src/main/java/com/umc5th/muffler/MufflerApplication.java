package com.umc5th.muffler;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(
		servers = {
				@Server(url="https://muffler.world", description = "Deploy Server url"),
				@Server(url="http://localhost:8080", description = "Local Server url")
		}
)
public class MufflerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MufflerApplication.class, args);
	}

}
