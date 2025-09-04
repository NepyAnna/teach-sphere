package com.sheoanna.teach_sphere.email;

import static org.junit.jupiter.api.Assertions.*;

import com.sheoanna.teach_sphere.email.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendRegistrationEmail_shouldCallTemplateEngineAndMailSender() throws Exception {
        String to = "test@example.com";
        String username = "user123";
        String htmlContent = "<html>Welcome!</html>";

        when(templateEngine.process(eq("registration-email"), any(Context.class))).thenReturn(htmlContent);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendRegistrationEmail(to, username);

        verify(templateEngine).process(eq("registration-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }
}
