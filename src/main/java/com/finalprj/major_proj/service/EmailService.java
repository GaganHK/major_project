package com.finalprj.major_proj.service;

import com.finalprj.major_proj.dto.MarksForm;
import com.finalprj.major_proj.dto.MarksFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    public void sendResultEmail(MarksForm marksForm) {
        // Construct and send the email with marksForm data
        // You can use JavaMailSender or any email API here
        System.out.println("Sending email to: " + marksForm.getEmail());
        // Include logic to format and send marksForm data
    }
}
