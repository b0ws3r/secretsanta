package com.westland.secretsanta.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Autowired
    @Bean
    public JavaMailSender getJavaMailSender(MailConfigProperties configProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configProperties.getMailSenderHost());
        mailSender.setPort(587);

        mailSender.setUsername(configProperties.getUsername());
        mailSender.setPassword(configProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", configProperties.getTransportProtocol());
        props.put("mail.smtp.auth", configProperties.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", configProperties.isTlsEnabled());
        props.put("mail.debug", configProperties.isDebugEnabled());

        return mailSender;
    }

}
