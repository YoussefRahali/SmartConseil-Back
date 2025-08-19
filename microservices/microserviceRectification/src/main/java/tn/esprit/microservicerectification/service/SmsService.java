package tn.esprit.microservicerectification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Slf4j
public class SmsService {

    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 10;

    /**
     * Generate a random SMS verification code
     */
    public String generateSmsCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return code.toString();
    }

    /**
     * Get SMS code expiry time (10 minutes from now)
     */
    public LocalDateTime getCodeExpiry() {
        return LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);
    }

    /**
     * Send SMS code (mock implementation - in real scenario, integrate with SMS provider)
     */
    public boolean sendSmsCode(String phoneNumber, String code) {
        try {
            // Mock SMS sending - in production, integrate with SMS provider like Twilio, etc.
            log.info("Sending SMS code {} to phone number {}", code, phoneNumber);

            // Simulate SMS sending delay
            Thread.sleep(1000);

            log.info("SMS code sent successfully to {}", phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS code to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Validate SMS code
     */
    public boolean isCodeValid(String providedCode, String storedCode, LocalDateTime expiry) {
        if (providedCode == null || storedCode == null || expiry == null) {
            return false;
        }

        // Check if code has expired
        if (LocalDateTime.now().isAfter(expiry)) {
            log.warn("SMS code has expired");
            return false;
        }

        // Check if codes match
        return providedCode.equals(storedCode);
    }
}