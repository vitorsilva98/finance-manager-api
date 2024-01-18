package br.com.finance.manager.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@SpringBootApplication
public class FinanceManagerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceManagerApiApplication.class, args);
		log.info("Application started and ready to receive requests");
	}
}
