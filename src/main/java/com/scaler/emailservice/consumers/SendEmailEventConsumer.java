package com.scaler.emailservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.emailservice.dtos.SendEmailEventDto;
import com.scaler.emailservice.utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailEventConsumer {
    private final ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleEmailEvent(String message) throws JsonProcessingException {
        SendEmailEventDto event
                        = objectMapper.readValue(message, SendEmailEventDto.class);

        String to = event.getTo();
        String from = event.getFrom();
        String subject = event.getSubject();
        String body = event.getBody();

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "shubha.iim.mumbai@gmail.com",
                        "vpkhvmccyipxchyc");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session,to,subject,body);
    }
}
