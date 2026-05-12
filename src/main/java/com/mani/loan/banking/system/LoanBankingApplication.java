package com.mani.loan.banking.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "${secrets.file}", ignoreResourceNotFound = true)
public class LoanBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanBankingApplication.class, args);
	}

}
