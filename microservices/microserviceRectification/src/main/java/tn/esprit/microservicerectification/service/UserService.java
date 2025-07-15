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
            log.info("Calling user service to find chef for sector: {}", option);

            String chefEmail = restTemplate.getForObject(url, String.class);
            log.info("Found chef for sector {}: {}", option, chefEmail);

            return chefEmail != null ? chefEmail : "chef@test.com";

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