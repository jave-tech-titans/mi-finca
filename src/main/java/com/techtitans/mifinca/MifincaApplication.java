package com.techtitans.mifinca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MifincaApplication {

	public static void main(String[] args) {
		if(System.getenv("CRYPT_KEY") == null){
			System.out.println("DIDNT HAD ENV KEYS, USING DEFAULT ONES");
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		}
		SpringApplication.run(MifincaApplication.class, args);
	}
}
