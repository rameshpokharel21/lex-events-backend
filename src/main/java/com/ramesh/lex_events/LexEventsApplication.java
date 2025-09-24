package com.ramesh.lex_events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class LexEventsApplication {

	public static void main(String[] args) {
		log.info("main started");
		SpringApplication.run(LexEventsApplication.class, args);
	}

}
