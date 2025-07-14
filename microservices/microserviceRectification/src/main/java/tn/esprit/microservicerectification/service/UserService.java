package tn.esprit.microservicerectification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final RestTemplate restTemplate;

    @Value("${microservice.user.url:http://localhost:8088}")
    private String userServiceUrl;

    public UserService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Find department head username by option/sector
     */
    public String findChefDepartementByOption(String option) {
        try {
            String url = userServiceUrl + "/api/users/chef-by-sector/" + option;

            // In a real scenario, you would make an HTTP call to the user microservice
            // For now, we'll use a mock implementation based on common sectors
            return getChefDepartementMock(option);

        } catch (Exception e) {
            log.error("Error finding chef departement for option: {}", option, e);
            // Return a default chef if service is unavailable
            return "chef@test.com";
        }
    }

    /**
     * Get teacher's phone number
     */
    public String getTeacherPhoneNumber(String username) {
        try {
            // Mock implementation - in real scenario, call user microservice
            // String url = userServiceUrl + "/api/users/phone/" + username;

            // For demo purposes, return a mock phone number
            return "+216" + (20000000 + Math.abs(username.hashCode() % 80000000));

        } catch (Exception e) {
            log.error("Error getting phone number for user: {}", username, e);
            return "+21620000000"; // Default phone number
        }
    }

    /**
     * Mock implementation for finding chef departement by sector
     * In production, this would query the user microservice
     */
    private String getChefDepartementMock(String option) {
        Map<String, String> sectorToChef = new HashMap<>();
        sectorToChef.put("Informatique", "chef@test.com");
        sectorToChef.put("Mathématique", "chef@test.com");
        sectorToChef.put("Telecommunication", "chef@test.com");
        sectorToChef.put("ML", "chef@test.com");
        sectorToChef.put("GC", "chef@test.com");

        return sectorToChef.getOrDefault(option, "chef@test.com");
    }

    /**
     * Make authenticated HTTP call to user microservice
     */
    private ResponseEntity<String> makeAuthenticatedCall(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}