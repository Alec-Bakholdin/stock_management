package com.bakholdin.stock_management.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
@RequiredArgsConstructor
public class SendGridAdapter {
    private final SendGrid sendGrid;

    public void sendMessageFromServer(String to, String subject, String message){
        Content content = new Content("text/plain", message);
        sendEmail("noreply@bakholdin.com", to, subject, content);
    }

    public void sendEmail(String from, String to, String subject, Content content) {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        Request sendGridRequest = new Request();
        try {
            sendGridRequest.setMethod(Method.POST);
            sendGridRequest.setEndpoint("mail/send");
            sendGridRequest.setBody(mail.build());
            Response response = sendGrid.api(sendGridRequest);
            if(response.getStatusCode() == 202){
                log.info("Response {}: {}", response.getStatusCode(), response.getBody());
            } else {
                log.error("Response {}: {}", response.getStatusCode(), response.getBody());
            }
        }   catch (IOException e) {
            log.error("Error sending email", e);
        }
    }
}
