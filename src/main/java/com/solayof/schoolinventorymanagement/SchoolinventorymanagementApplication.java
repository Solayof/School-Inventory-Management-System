package com.solayof.schoolinventorymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enables Spring's scheduled task execution
public class SchoolinventorymanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolinventorymanagementApplication.class, args);
	}

}
