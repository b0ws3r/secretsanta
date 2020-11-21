package com.westland.secretsanta.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailConfigProperties {

    private String mailSenderHost;
    private String username;
    private String password;
    private String transportProtocol;
    private boolean isSmtpAuth;
    private boolean isTlsEnabled;
    private boolean debugEnabled;

    public MailConfigProperties(){
        super();
    }

    public String getMailSenderHost() {
        return mailSenderHost;
    }

    public void setMailSenderHost(String mailSenderHost) {
        this.mailSenderHost = mailSenderHost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTransportProtocol() {
        return transportProtocol;
    }

    public void setTransportProtocol(String transportProtocol) {
        this.transportProtocol = transportProtocol;
    }

    public boolean isSmtpAuth() {
        return isSmtpAuth;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        isSmtpAuth = smtpAuth;
    }

    public boolean isTlsEnabled() {
        return isTlsEnabled;
    }

    public void setTlsEnabled(boolean tlsEnabled) {
        isTlsEnabled = tlsEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

}
