package playground.logic.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import playground.logic.Entities.UserEntity;

@Service
public class EmailService {
  
    public JavaMailSender javaMailSender;
    
    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
    	this.javaMailSender = javaMailSender;
    }
 
    public void sendSimpleMessage(UserEntity userEntity) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo(userEntity.getEmail()); 
        message.setSubject("User verification code for animal shelter"); 
        message.setText("hello "+ userEntity.getUsername() +
        		",\nyour verify code is: " + userEntity.getCode());
        javaMailSender.send(message);
    }
}

