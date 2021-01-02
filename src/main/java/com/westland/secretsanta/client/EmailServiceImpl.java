package com.westland.secretsanta.client;

import com.westland.secretsanta.api.config.MailConfigProperties;
import com.westland.secretsanta.api.model.SantaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class EmailServiceImpl {
    private JavaMailSender emailSender;
    private MailConfigProperties mailConfigProperties;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender, MailConfigProperties mailConfigProperties) {
        this.emailSender = emailSender;
        this.mailConfigProperties = mailConfigProperties;
    }

    public void sendSimpleMessage(
            String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfigProperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);

    }
}