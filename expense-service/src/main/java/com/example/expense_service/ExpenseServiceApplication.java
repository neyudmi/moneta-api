package com.example.expense_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class ExpenseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseServiceApplication.class, args);
	}

}
