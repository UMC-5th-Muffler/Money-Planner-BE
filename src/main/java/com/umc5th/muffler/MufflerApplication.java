package com.umc5th.muffler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MufflerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MufflerApplication.class, args);
	}

}
