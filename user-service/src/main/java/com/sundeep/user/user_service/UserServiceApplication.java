package com.sundeep.user.user_service;

import com.sundeep.user.user_service.dto.UserServiceInfoDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(value = UserServiceInfoDTO.class)
@OpenAPIDefinition(
		info = @Info(
				title = "User Service API Documentation",
				version = "1.0",
				description = "Documentation for User Service APIs",
                contact = @Contact(
                        name = "Sundeep Kumar Singh",
                        email = "sundeepkumarsinngh0@gmail.com"
                )
		)
)
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
