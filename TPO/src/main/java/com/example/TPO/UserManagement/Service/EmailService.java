package com.example.TPO.UserManagement.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    int currentYear = Year.now().getValue();
    public void sendOTPEmail(String toEmail, String otp) throws MessagingException {
        String subject = "Your OTP for Registration";
        String body = generateHTMLEmail(otp, "Registration");

        sendHTMLEmail(toEmail, subject, body);
    }

    public void sendOTPEmailResetpass(String toEmail, String otp) throws MessagingException {
        String subject = "Your OTP for Changing Password";
        String body = generateHTMLEmail(otp, "Password Reset");

        sendHTMLEmail(toEmail, subject, body);
    }

    private void sendHTMLEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // Enable HTML

        mailSender.send(message);
    }

    private String generateHTMLEmail(String otp, String purpose) {
        return "<div style=\"font-family: Arial, sans-serif; text-align: center; padding: 20px; background: url('https://i.ibb.co/mWCcyVy/Main-bg.png') no-repeat center center / cover; color: white;\">"
                + "<div style='max-width: 500px; margin: auto; padding: 20px; background: white; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>"
                + "<img src='https://engineering.saraswatikharghar.edu.in/wp-content/uploads/2023/05/thumbnail_Saraswati-Logo-square-final-1-1024x612.jpg.webp' alt='Logo' style='width: 120px; margin-bottom: 15px;'>"
                + "<h2 style='color: #1b263b;'>Hello,</h2>"
                + "<p style='color: #555;'>Your OTP for <strong>" + purpose + "</strong> is:</p>"
                + "<h1 style=\"color: #ffffff; font-size: 28px; background: #07333f; padding: 15px 30px; display: inline-block; border-radius: 5px;\">"
                + otp + "</h1>"
                + "<p style='color: #777;'>This OTP is valid for <strong>5 minutes</strong>.</p>"
                + "<p style='color: #aaa;'>If you didn't request this, please ignore this email.</p>"
                + "<hr style='border: 0; height: 1px; background: #ddd;'>"
                + "<p style='color: #888; font-size: 12px;'>© " + currentYear + " TPO SCOE. All rights reserved.</p>"
                + "</div>"
                + "</div>";
    }

}
