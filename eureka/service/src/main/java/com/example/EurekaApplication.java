package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class EurekaApplication {

  @GetMapping("/")
  public String home() {
    return "Hello, world";
  }

	public static void main(String[] args) {
    SpringApplication.run(EurekaApplication.class,args);
	}

}
