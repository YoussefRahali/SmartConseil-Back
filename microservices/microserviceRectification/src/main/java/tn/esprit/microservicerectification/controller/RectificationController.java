package tn.esprit.microservicerectification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import tn.esprit.microservicerectification.dto.*;
import tn.esprit.microservicerectification.entity.Rectification;
import tn.esprit.microservicerectification.service.RectificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rectification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RectificationController {

    private final RectificationService service;

    /**
     * Get all rectifications (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
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
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
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
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
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
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
    @GetMapping("/processed-history")
    public List<RectificationResponseDTO> getChefHistory(Principal principal) {
        return service.getChefHistory(principal.getName());
    }
}