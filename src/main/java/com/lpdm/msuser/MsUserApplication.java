package com.lpdm.msuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lpdm.msuser.controllers.SessionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient

public class MsUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUserApplication.class, args);
	}

	@Bean
	public SessionController getSession(){
		return new SessionController();
	}

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void init(){
		objectMapper.registerModule(new JavaTimeModule());
	}
}
