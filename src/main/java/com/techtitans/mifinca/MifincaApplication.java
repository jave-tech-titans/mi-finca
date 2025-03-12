package com.techtitans.mifinca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.techtitans.mifinca.domain.services.AuthService;
import com.techtitans.mifinca.domain.services.CryptService;
import com.techtitans.mifinca.domain.services.EmailService;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MifincaApplication {

	public static void main(String[] args) {
		//load crypt key var and inject it
		Dotenv dotenv = Dotenv.load();
        String cryptKey = dotenv.get("CRYPT_KEY");
		CryptService.setKey(cryptKey);

		String jwtSecret = dotenv.get("JWT_SECRET");
		AuthService.setSecret(jwtSecret);

		String emailApi = dotenv.get("SENDGRID_API_KEY");
		EmailService.setApiAkey(emailApi);

		String fromEmail = dotenv.get("SENDGRID_EMAIL");
		EmailService.setFromEmail(fromEmail);

		SpringApplication.run(MifincaApplication.class, args);
	}
}
