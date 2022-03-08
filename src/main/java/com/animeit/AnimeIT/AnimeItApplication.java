package com.animeit.AnimeIT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import com.animeit.AnimeIT.config.SwaggerConfiguration;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfiguration.class)
public class AnimeItApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimeItApplication.class, args);
	}

}
