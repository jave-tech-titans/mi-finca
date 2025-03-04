package com.techtitans.mifinca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.techtitans.mifinca.domain.services.CryptService;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MifincaApplication {

	public static void main(String[] args) {
		//load crypt key var and inject it
		Dotenv dotenv = Dotenv.load();
        String cryptKey = dotenv.get("CRYPT_KEY");
		CryptService.setKey(cryptKey);
		SpringApplication.run(MifincaApplication.class, args);
	}
}
