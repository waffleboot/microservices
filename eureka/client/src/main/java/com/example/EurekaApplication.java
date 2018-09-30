package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@SpringBootApplication
public class EurekaApplication {

  @Autowired
  private DiscoveryClient discoveryClient;

	public static void main(String[] args) {
    DiscoveryClient discoveryClient = SpringApplication.run(EurekaApplication.class,args)
                                                       .getBean(EurekaApplication.class)
                                                       .discoveryClient;
    discoveryClient.getServices().forEach(System.out::println);
    discoveryClient.getInstances("hello").forEach(i -> {
      System.out.println("----");
      System.out.println(i.getHost());
      System.out.println(i.getPort());
      System.out.println(i.getUri());
      i.getMetadata().forEach((k,v) -> {
        System.out.println(k + '=' + v);
      });
    });
	}

}
