package server.email;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @see MailSenderService
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MailSenderServiceTest {

    @SuppressWarnings("unused")
    @MockBean
    private JavaMailSenderImpl javaMailSender;

    @SuppressWarnings("unused")
    @Autowired
    private MailSenderService mailSenderService;

    /**
     * Tests that the method sends an email via the Java Mail Sender instance
     * @see MailSenderService#sendSimpleMail
     */
    @Test
    public void sendSimpleMail() {
        mailSenderService.sendSimpleMail("recipient@email.com", "Test email", "This is a test!");

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}