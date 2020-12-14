package com.mitocode;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig {

	public static final Contact DEFAULT_CONTACT = new Contact("MitoCode Network","http://www.mitocode.com",
			"cursos@mitocodenetwork.com");
	public static final ApiInfo DEFAULT_API_INFO = new ApiInfo("MitoRest webflux api Documentation","MitoRest webflux api Documentation","",
			"PREMIUM", DEFAULT_CONTACT, "Apache 2.0", "http:// apache ...",
			new ArrayList<>());
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(DEFAULT_API_INFO)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
}
