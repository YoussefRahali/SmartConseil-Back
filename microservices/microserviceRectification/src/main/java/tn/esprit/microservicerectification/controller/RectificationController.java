package tn.esprit.microservicerectification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.microservicerectification.dto.*;
import tn.esprit.microservicerectification.entity.Rectification;
import tn.esprit.microservicerectification.service.RectificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rectification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RectificationController {

    private final RectificationService service;

    /**
     * Get all rectifications (admin and chef departement)
     */
    @GetMapping
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT') or hasRole('ADMIN')")
    public List<RectificationResponseDTO> getAll() {
        return service.findAll();
    }

    /**
     * Create a new rectification request (teachers only)
     */
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @PostMapping
    public ResponseEntity<Rectification> create(@RequestBody RectificationRequestDTO dto, Principal principal) {
        Rectification rectification = service.create(dto, principal.getName());
        return ResponseEntity.ok(rectification);
    }

    /**
     * Verify SMS code
     */
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @PostMapping("/verify-sms")
    public ResponseEntity<Map<String, Object>> verifySms(@RequestBody SmsVerificationDTO dto) {
        boolean verified = service.verifySmsCode(dto);
        Map<String, Object> response = Map.of(
                "verified", verified,
                "message", verified ? "SMS verification successful" : "SMS verification failed"
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Update rectification status (department heads only)
     */
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Rectification> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDTO dto,
            Principal principal) {
        Rectification updated = service.updateStatus(id, dto, principal.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * Get teacher's own rectifications
     */
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @GetMapping("/my-requests")
    public List<RectificationResponseDTO> getMyRequests(Principal principal) {
        return service.findByEnseignantUsername(principal.getName());
    }

    /**
     * Get rectifications for department head to process
     */
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT')")
    @GetMapping("/pending")
    public List<RectificationResponseDTO> getPendingRequests(Principal principal) {
        return service.findByChefDepartementUsername(principal.getName());
    }

    /**
     * Get teacher's rectification history
     */
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @GetMapping("/history")
    public List<RectificationResponseDTO> getTeacherHistory(Principal principal) {
        return service.getTeacherHistory(principal.getName());
    }

    /**
     * Get department head's processed rectifications history
     */
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT')")
    @GetMapping("/processed-history")
    public List<RectificationResponseDTO> getChefHistory(Principal principal) {
        return service.getChefHistory(principal.getName());
    }

    /**
     * Test endpoint to verify chef assignment for different options
     */
    @GetMapping("/test-chef-mapping/{option}")
    public ResponseEntity<String> testChefMapping(@PathVariable String option) {
        String result = service.testChefMapping(option);
        return ResponseEntity.ok(result);
    }

    /**
     * Test endpoint to debug authentication
     */
    @GetMapping("/test-auth")
    public ResponseEntity<Map<String, Object>> testAuth(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", principal != null);
        response.put("username", principal != null ? principal.getName() : "null");

        // Get current authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        response.put("hasAuthentication", auth != null);
        response.put("authName", auth != null ? auth.getName() : "null");
        response.put("authorities", auth != null ? auth.getAuthorities().toString() : "null");

        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint for enseignant role
     */
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @GetMapping("/test-enseignant")
    public ResponseEntity<String> testEnseignant(Principal principal) {
        return ResponseEntity.ok("Success! Enseignant role verified for: " + principal.getName());
    }

    /**
     * Debug endpoint to see all rectifications with chef assignments
     */
    @GetMapping("/debug-all")
    public ResponseEntity<List<Map<String, Object>>> debugAllRectifications() {
        List<RectificationResponseDTO> allRectifications = service.findAll();
        List<Map<String, Object>> debugInfo = new ArrayList<>();

        for (RectificationResponseDTO r : allRectifications) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", r.getId());
            info.put("etudiant", r.getEtudiantPrenom() + " " + r.getEtudiantNom());
            info.put("option", r.getOption());
            info.put("enseignantUsername", r.getEnseignantUsername());
            info.put("chefDepartementUsername", r.getChefDepartementUsername());
            info.put("status", r.getStatus());
            info.put("dateDemande", r.getDateDemande());
            debugInfo.add(info);
        }

        return ResponseEntity.ok(debugInfo);
    }

    /**
     * Get rectification statistics (admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/statistics")
    public ResponseEntity<Map<String, Object>> getRectificationStatistics() {
        Map<String, Object> stats = service.getRectificationStatistics();
        return ResponseEntity.ok(stats);
    }
}