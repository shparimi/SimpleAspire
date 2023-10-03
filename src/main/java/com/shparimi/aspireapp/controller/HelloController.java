package com.shparimi.aspireapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloController {

	@GetMapping("/")
	public String index() {
		log.info("Testing log");
		return "Greetings from Spring Boot!";
	}
	
	

}
