package com.cires.technlogies.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Cires technologies",
				version = "1.0",
				description = "Cires Technologies, filiale du Groupe Tanger Med, est une entité spécialisée dans le développement de solutions de Sécurité, Cyber-sécurité et Systèmes d’Informations pour des structures complexes et/ou stratégiques.\n" +
						"\n" +
						"Cires Technologies dispose d’une gamme diversifiée de solutions complète et de services couvrant les applications métier de sécurité électronique, d’IT & Télécommunication, de centres de données et de services Cloud."
		)
)
public class DemoApplication {
	@Bean
	public ModelMapper modelMapper () {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
