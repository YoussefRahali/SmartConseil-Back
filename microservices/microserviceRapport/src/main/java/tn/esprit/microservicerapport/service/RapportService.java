package tn.esprit.microservicerapport.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.microservicerapport.dto.RapportRequestDTO;
import tn.esprit.microservicerapport.dto.RapportResponseDTO;
import tn.esprit.microservicerapport.dto.RapportUpdateDTO;
import tn.esprit.microservicerapport.entity.Rapport;
import tn.esprit.microservicerapport.entity.Rapport.StatutRapport;
import tn.esprit.microservicerapport.repository.RapportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RapportService {
    
    private final RapportRepository rapportRepository;
    
    /**
     * Create a new report
     */
    public RapportResponseDTO createRapport(RapportRequestDTO dto, String rapporteurUsername) {
        Rapport rapport = Rapport.builder()
                .titre(dto.getTitre())
                .contenu(dto.getContenu())
                .classe(dto.getClasse())
                .rapporteurUsername(rapporteurUsername)
                .statut(StatutRapport.BROUILLON)
                .dateCreation(LocalDateTime.now())
                .secteur(dto.getSecteur())
                .anneeAcademique(dto.getAnneeAcademique())
                .semestre(dto.getSemestre())
                .build();
        
        Rapport savedRapport = rapportRepository.save(rapport);
        log.info("Report created with ID: {} by rapporteur: {}", savedRapport.getId(), rapporteurUsername);
        
        return convertToResponseDTO(savedRapport);
    }
    
    /**
     * Update an existing report
     */
    public RapportResponseDTO updateRapport(Long id, RapportUpdateDTO dto, String rapporteurUsername) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        
        // Verify that the rapporteur is authorized to update this report
        if (!rapport.getRapporteurUsername().equals(rapporteurUsername)) {
            throw new RuntimeException("Unauthorized to update this report");
        }
        
        // Only allow updates if the report is still in draft status
        if (rapport.getStatut() == StatutRapport.VALIDE) {
            throw new RuntimeException("Cannot update a validated report");
        }
        
        // Update fields
        rapport.setTitre(dto.getTitre());
        rapport.setContenu(dto.getContenu());
        rapport.setClasse(dto.getClasse());
        rapport.setSecteur(dto.getSecteur());
        rapport.setAnneeAcademique(dto.getAnneeAcademique());
        rapport.setSemestre(dto.getSemestre());
        rapport.setDateModification(LocalDateTime.now());
        
        Rapport updatedRapport = rapportRepository.save(rapport);
        log.info("Report ID: {} updated by rapporteur: {}", id, rapporteurUsername);
        
        return convertToResponseDTO(updatedRapport);
    }
    
    /**
     * Validate a report (change status from BROUILLON to VALIDE)
     */
    public RapportResponseDTO validateRapport(Long id, String rapporteurUsername) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        
        // Verify that the rapporteur is authorized to validate this report
        if (!rapport.getRapporteurUsername().equals(rapporteurUsername)) {
            throw new RuntimeException("Unauthorized to validate this report");
        }
        
        // Only allow validation if the report is in draft status
        if (rapport.getStatut() == StatutRapport.VALIDE) {
            throw new RuntimeException("Report is already validated");
        }
        
        rapport.setStatut(StatutRapport.VALIDE);
        rapport.setDateValidation(LocalDateTime.now());
        
        Rapport validatedRapport = rapportRepository.save(rapport);
        log.info("Report ID: {} validated by rapporteur: {}", id, rapporteurUsername);
        
        return convertToResponseDTO(validatedRapport);
    }
    
    /**
     * Get all reports for a specific rapporteur
     */
    public List<RapportResponseDTO> getRapportsByRapporteur(String rapporteurUsername) {
        return rapportRepository.findByRapporteurUsernameOrderByDateCreationDesc(rapporteurUsername)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reports by status for a specific rapporteur
     */
    public List<RapportResponseDTO> getRapportsByRapporteurAndStatus(String rapporteurUsername, StatutRapport statut) {
        return rapportRepository.findByRapporteurUsernameAndStatut(rapporteurUsername, statut)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific report by ID
     */
    public RapportResponseDTO getRapportById(Long id, String rapporteurUsername) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        
        // Verify that the rapporteur is authorized to view this report
        if (!rapport.getRapporteurUsername().equals(rapporteurUsername)) {
            throw new RuntimeException("Unauthorized to view this report");
        }
        
        return convertToResponseDTO(rapport);
    }
    
    /**
     * Delete a report (only if it's in draft status)
     */
    public void deleteRapport(Long id, String rapporteurUsername) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        
        // Verify that the rapporteur is authorized to delete this report
        if (!rapport.getRapporteurUsername().equals(rapporteurUsername)) {
            throw new RuntimeException("Unauthorized to delete this report");
        }
        
        // Only allow deletion if the report is in draft status
        if (rapport.getStatut() == StatutRapport.VALIDE) {
            throw new RuntimeException("Cannot delete a validated report");
        }
        
        rapportRepository.delete(rapport);
        log.info("Report ID: {} deleted by rapporteur: {}", id, rapporteurUsername);
    }
    
    /**
     * Get all reports (admin function)
     */
    public List<RapportResponseDTO> getAllRapports() {
        return rapportRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert entity to response DTO
     */
    private RapportResponseDTO convertToResponseDTO(Rapport rapport) {
        RapportResponseDTO dto = new RapportResponseDTO();
        dto.setId(rapport.getId());
        dto.setTitre(rapport.getTitre());
        dto.setContenu(rapport.getContenu());
        dto.setClasse(rapport.getClasse());
        dto.setRapporteurUsername(rapport.getRapporteurUsername());
        dto.setStatut(rapport.getStatut());
        dto.setDateCreation(rapport.getDateCreation());
        dto.setDateModification(rapport.getDateModification());
        dto.setDateValidation(rapport.getDateValidation());
        dto.setSecteur(rapport.getSecteur());
        dto.setAnneeAcademique(rapport.getAnneeAcademique());
        dto.setSemestre(rapport.getSemestre());
        return dto;
    }
}
