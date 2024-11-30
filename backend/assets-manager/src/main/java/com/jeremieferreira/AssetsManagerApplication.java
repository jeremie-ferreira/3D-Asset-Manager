package com.jeremieferreira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching  
@EnableAsync
@EnableScheduling
@PropertySources({
	@PropertySource(value={"classpath:application.properties"}),
	@PropertySource(value={"classpath:custom.properties"})
})
public class AssetsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetsManagerApplication.class, args);
	}

}
