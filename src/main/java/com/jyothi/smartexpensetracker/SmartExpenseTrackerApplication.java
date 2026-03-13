package com.jyothi.smartexpensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartExpenseTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartExpenseTrackerApplication.class, args);
	}

}
