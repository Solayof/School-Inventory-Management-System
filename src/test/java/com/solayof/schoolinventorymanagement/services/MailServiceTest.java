package com.solayof.schoolinventorymanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) integrates Mockito with JUnit 5.
// It enables the use of Mockito annotations like @Mock and @InjectMocks.
@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    // @Mock creates a mock instance of JavaMailSender.
    // This allows us to control its behavior and verify interactions with it.
    @Mock
    private JavaMailSender javaMailSender;

    // @InjectMocks creates an instance of MailService and injects the mocked
    // JavaMailSender into it. This is the service we want to test.
    @InjectMocks
    private MailService mailService;

    // @BeforeEach method runs before each test method.
    // It's useful for setting up common test conditions, though in this simple case,
    // @Mock and @InjectMocks handle most of the setup.
    @BeforeEach
    void setUp() {
        // No specific setup needed here as Mockito annotations handle initialization.
    }

    @Test
    @DisplayName("Should send email successfully with correct details")
    void shouldSendEmailSuccessfully() throws MailException {
        // 1. Define test data
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body Content";

        // 2. Mock behavior (Optional for void methods like send, but good practice for clarity)
        // We don't need to 'when' anything here because JavaMailSender.send() is a void method
        // and we are primarily interested in verifying that it was called.
        // If it threw an exception, we'd handle it in a separate test.

        // 3. Call the method under test
        mailService.sendEmail(to, subject, body);

        // 4. Verify interactions and arguments
        // ArgumentCaptor is used to capture the argument passed to the mocked method.
        // This allows us to inspect the SimpleMailMessage object that was sent.
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Verify that javaMailSender.send() was called exactly once.
        // The captured argument is then used to assert its properties.
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        // Get the captured message
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        // Assert that the properties of the captured message are as expected
        // The 'from' address is hardcoded in the MailService, so we assert it.
        // Note: SimpleMailMessage.getFrom() returns an array of strings.
        assertNotNull(capturedMessage.getFrom());

        // Assert the 'to' recipient.
        assertNotNull(capturedMessage.getTo());
        assertEquals("noreply@schoolinventory.com", capturedMessage.getFrom());
        assertArrayEquals(new String[]{to}, capturedMessage.getTo());
        // Assert the subject.
        assertEquals(subject, capturedMessage.getSubject());
        
        // Assert the body/text content.
        assertEquals(body, capturedMessage.getText());
    }

    @Test
    @DisplayName("Should throw MailException when JavaMailSender fails")
    void shouldThrowMailExceptionWhenSenderFails() {
        // 1. Define test data
        String to = "error@example.com";
        String subject = "Error Subject";
        String body = "Error Body Content";

        // 2. Mock behavior: Simulate JavaMailSender throwing a MailException
        // when its send() method is called with any SimpleMailMessage.
        doThrow(new MailException("Simulated mail send failure") {})
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        // 3. Call the method under test and assert that it throws the expected exception
        // assertThrows verifies that the specified executable throws an exception of the given type.
        assertThrows(MailException.class, () -> mailService.sendEmail(to, subject, body));

        // 4. Verify that javaMailSender.send() was still attempted exactly once
        // even though it threw an exception.
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle null or empty 'to' address gracefully (though service might fail later)")
    void shouldHandleNullOrEmptyToAddress() throws MailException {
        // Test with a null 'to' address
        String toNull = null;
        String subject = "Subject";
        String body = "Body";

        mailService.sendEmail(toNull, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        // Assert that 'to' is null in the captured message, as passed.
        assertNotNull(capturedMessage.getTo());

        // Reset mock for the next part of the test
        reset(javaMailSender);

        // Test with an empty 'to' address
        String toEmpty = "";
        mailService.sendEmail(toEmpty, subject, body);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        capturedMessage = messageCaptor.getValue();
        // Assert that 'to' is an empty array in the captured message.
        assertArrayEquals(new String[]{toEmpty}, capturedMessage.getTo());
    }
}