package com.example.smartcontactmanager.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
public boolean sendEmail(String subject,String message1,String to) {
    boolean f=true;
    String from = "shafaat888@gmail.com";
    //provide Mailtrap's username
    final String username = "shafaat888@gmail.com";
    //provide Mailtrap's password
    final String password = "";
    //provide Mailtrap's host address
    String host = "smtp.gmail.com";
    //configure Mailtrap's SMTP server details
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "587");
    //create the Session object
    Authenticator authenticator = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    };
    Session session = Session.getInstance(props, authenticator);

    try {
        //create a MimeMessage object
        Message message = new MimeMessage(session);
        //set From email field
        message.setFrom(new InternetAddress(from));
        //set To email field
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        //set email subject field
        message.setSubject(subject);
        //set the content of the email message
        //message.setText(message1);
        message.setContent(message1,"text/html");
        //send the email message
        Transport.send(message);
        System.out.println("Email Message Sent Successfully");
    } catch (MessagingException e) {
        throw new RuntimeException(e);
    }
    return f;
  }
}
