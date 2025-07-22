package tn.esprit.microservicerapport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservicerapport.dto.RapportRequestDTO;
import tn.esprit.microservicerapport.dto.RapportResponseDTO;
import tn.esprit.microservicerapport.dto.RapportUpdateDTO;
import tn.esprit.microservicerapport.entity.Rapport.StatutRapport;
import tn.esprit.microservicerapport.service.RapportService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rapport")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RapportController {
    
    private final RapportService rapportService;
    
    /**
     * Create a new report (rapporteurs only)
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @PostMapping
    public ResponseEntity<RapportResponseDTO> createRapport(
            @RequestBody RapportRequestDTO dto, 
            Principal principal) {
        RapportResponseDTO rapport = rapportService.createRapport(dto, principal.getName());
        return ResponseEntity.ok(rapport);
    }
    
    /**
     * Update an existing report (rapporteurs only)
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @PutMapping("/{id}")
    public ResponseEntity<RapportResponseDTO> updateRapport(
            @PathVariable Long id,
            @RequestBody RapportUpdateDTO dto,
            Principal principal) {
        RapportResponseDTO rapport = rapportService.updateRapport(id, dto, principal.getName());
        return ResponseEntity.ok(rapport);
    }
    
    /**
     * Validate a report (change status from BROUILLON to VALIDE)
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @PutMapping("/{id}/validate")
    public ResponseEntity<RapportResponseDTO> validateRapport(
            @PathVariable Long id,
            Principal principal) {
        RapportResponseDTO rapport = rapportService.validateRapport(id, principal.getName());
        return ResponseEntity.ok(rapport);
    }
    
    /**
     * Get all reports for the current rapporteur
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @GetMapping("/my-reports")
    public ResponseEntity<List<RapportResponseDTO>> getMyReports(Principal principal) {
        List<RapportResponseDTO> reports = rapportService.getRapportsByRapporteur(principal.getName());
        return ResponseEntity.ok(reports);
    }
    
    /**
     * Get draft reports for the current rapporteur
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @GetMapping("/my-drafts")
    public ResponseEntity<List<RapportResponseDTO>> getMyDrafts(Principal principal) {
        List<RapportResponseDTO> drafts = rapportService.getRapportsByRapporteurAndStatus(
                principal.getName(), StatutRapport.BROUILLON);
        return ResponseEntity.ok(drafts);
    }
    
    /**
     * Get validated reports for the current rapporteur
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @GetMapping("/my-validated")
    public ResponseEntity<List<RapportResponseDTO>> getMyValidatedReports(Principal principal) {
        List<RapportResponseDTO> validated = rapportService.getRapportsByRapporteurAndStatus(
                principal.getName(), StatutRapport.VALIDE);
        return ResponseEntity.ok(validated);
    }
    
    /**
     * Get a specific report by ID
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @GetMapping("/{id}")
    public ResponseEntity<RapportResponseDTO> getRapportById(
            @PathVariable Long id,
            Principal principal) {
        RapportResponseDTO rapport = rapportService.getRapportById(id, principal.getName());
        return ResponseEntity.ok(rapport);
    }
    
    /**
     * Delete a report (only drafts can be deleted)
     */
    @PreAuthorize("hasRole('RAPPORTEUR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRapport(
            @PathVariable Long id,
            Principal principal) {
        rapportService.deleteRapport(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Report deleted successfully"));
    }
    
    /**
     * Get all reports (admin function)
     */
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<RapportResponseDTO>> getAllReports() {
        List<RapportResponseDTO> reports = rapportService.getAllRapports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Get report statistics (admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/statistics")
    public ResponseEntity<Map<String, Object>> getReportStatistics() {
        Map<String, Object> stats = rapportService.getReportStatistics();
        return ResponseEntity.ok(stats);
    }
}
