package com.techtitans.mifinca.domain.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sendgrid.*;

import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;

@Service
public class EmailService {

    private String apiKey;
    private String fromEmail;
    private String domain;

    public EmailService(
        @Value("${email.api_key}") String apiKey,
        @Value("${email.from_email}") String fromEmail,
        @Value("${general.frontend-domain}") String domain
    ){
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.domain = domain;
    }

    public void sendConfirmEmail(String email, String token){
        Email from = new Email(fromEmail);
        String subject = "Account confirmation";
        Email to = new Email(email);
        String confirmLink = domain + "/auth/confirmation/" + token;
        Content content = new Content("text/plain", "To confirm your account please click on this link: " + confirmLink);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (Exception ex) {
            throw new ApiException(ApiError.UNABLE_TO_SEND_EMAIL);
        }
    }
    
}
