package server.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Represents a service which sends emails.
 * Based on the spring.mail.* properties in the <i>application.properties</i> file
 */
@Service
public class MailSenderService {

    /**
     * A <code>JavaMailSender</code> instance which is injected by the Spring framework.
     * Configured by the spring.mail.* properties in the <i>application.properties</i> file
     */
    @SuppressWarnings("unused")
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a plaintext email
     * @param to The email address of the intended recipient of the email
     * @param subject The subject of the email
     * @param content The content of the email
     */
    public void sendSimpleMail(String to, String subject, String content) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}