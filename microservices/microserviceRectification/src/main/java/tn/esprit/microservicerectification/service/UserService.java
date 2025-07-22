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
            // Map option to sector
            String sector = mapOptionToSector(option);
            String url = userServiceUrl + "/api/users/chef-by-sector/" + sector;
            log.info("Calling user service to find chef for option: {} (mapped to sector: {})", option, sector);

            String chefEmail = restTemplate.getForObject(url, String.class);
            log.info("Found chef for option {} (sector {}): {}", option, sector, chefEmail);

            return chefEmail != null ? chefEmail : "chef@test.com";

        } catch (Exception e) {
            log.error("Error finding chef departement for option: {}", option, e);
            // Return a default chef if service is unavailable
            return "chef@test.com";
        }
    }

    /**
     * Map frontend option to backend sector
     * Maps to exact sector names as they exist in the database
     */
    private String mapOptionToSector(String option) {
        log.info("Mapping option '{}' to sector", option);

        if (option == null || option.trim().isEmpty()) {
            log.warn("Option is null or empty, defaulting to informatique");
            return "informatique";
        }

        String trimmedOption = option.trim();

        // Handle Tronc-Commun options -> informatique (lowercase as in DB)
        if (trimmedOption.equals("1A") || trimmedOption.equals("2A") || trimmedOption.equals("2P") ||
            trimmedOption.equals("3A") || trimmedOption.equals("3B")) {
            log.info("Option '{}' mapped to informatique (Tronc-Commun)", trimmedOption);
            return "informatique";
        }

        // Handle Informatique options -> informatique (lowercase as in DB)
        if (trimmedOption.equals("Parcours IA") || trimmedOption.equals("Option DS") ||
            trimmedOption.equals("Option ERP-BI") || trimmedOption.equals("Option IFINI") ||
            trimmedOption.equals("Option SAE") || trimmedOption.equals("Option SE") ||
            trimmedOption.equals("Option Twin")) {
            log.info("Option '{}' mapped to informatique", trimmedOption);
            return "informatique";
        }

        // Handle Télécommunications options -> telecommunication (as in DB, no 's', no é)
        if (trimmedOption.equals("Option ARCTIC") || trimmedOption.equals("Option IOSYS") ||
            trimmedOption.equals("Option DATA") || trimmedOption.equals("Option GAMIX") ||
            trimmedOption.equals("Option SIM") || trimmedOption.equals("Option SLEAM") ||
            trimmedOption.equals("Option NIDS")) {
            log.info("Option '{}' mapped to telecommunication", trimmedOption);
            return "telecommunication";
        }

        // Check if option directly matches known database sector names
        if (trimmedOption.equalsIgnoreCase("informatique")) {
            log.info("Option '{}' matches informatique sector", trimmedOption);
            return "informatique";
        }
        if (trimmedOption.equalsIgnoreCase("telecommunication") ||
            trimmedOption.equalsIgnoreCase("télécommunications") ||
            trimmedOption.equalsIgnoreCase("telecommunications")) {
            log.info("Option '{}' matches telecommunication sector", trimmedOption);
            return "telecommunication";
        }

        // Default fallback
        log.warn("Option '{}' not recognized, defaulting to informatique", trimmedOption);
        return "informatique";
    }

    /**
     * Test method to verify option to chef mapping
     */
    public String testChefMapping(String option) {
        log.info("Testing chef mapping for option: {}", option);
        String sector = mapOptionToSector(option);
        log.info("Option '{}' mapped to sector '{}'", option, sector);

        try {
            String url = userServiceUrl + "/api/users/chef-by-sector/" + sector;
            log.info("Calling user service URL: {}", url);

            String chefEmail = restTemplate.getForObject(url, String.class);
            log.info("Chef found for sector '{}': {}", sector, chefEmail);

            return "Option: " + option + " -> Sector: " + sector + " -> Chef: " + chefEmail;
        } catch (Exception e) {
            log.error("Error in test mapping for option: {}", option, e);
            return "Error: " + e.getMessage();
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