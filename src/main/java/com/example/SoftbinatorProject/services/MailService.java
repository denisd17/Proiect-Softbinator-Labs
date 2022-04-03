package com.example.SoftbinatorProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class MailService {
    private final JavaMailSender emailSender;

    @Autowired
    public MailService(@Qualifier("getJavaMailSender") JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendRegistrationEmail(String email, String username, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();

        String content = "Bine ai venit, " +
                firstName +
                ".\n" +
                "Username-ul tau este: " +
                username;

        message.setFrom("noreply@charityapp.com");
        message.setTo(email);
        message.setSubject("Cont nou creat");
        message.setText(content);

        emailSender.send(message);
    }

    @Async
    public void sendReceiptEmail(String email, String receiptName, String receiptUrl) {
        SimpleMailMessage message = new SimpleMailMessage();

        String content = "Link catre factura: " + receiptUrl;

        message.setFrom("noreply@charityapp.com");
        message.setTo(email);
        message.setSubject(receiptName);
        message.setText(content);

        emailSender.send(message);
    }
}
