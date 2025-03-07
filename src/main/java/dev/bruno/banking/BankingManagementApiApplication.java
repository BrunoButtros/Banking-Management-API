package dev.bruno.banking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Banking Management API",
				version = "1.0",
				description = "API de gestão financeira para clientes bancários"
		)
)
public class BankingManagementApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankingManagementApiApplication.class, args);
	}
}
