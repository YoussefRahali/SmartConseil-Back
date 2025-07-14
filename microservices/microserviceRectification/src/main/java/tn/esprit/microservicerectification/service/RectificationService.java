package tn.esprit.microservicerectification.service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.microservicerectification.dto.*;
import tn.esprit.microservicerectification.entity.Rectification;
import tn.esprit.microservicerectification.repository.RectificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RectificationService {

    private final RectificationRepository repo;
    private final SmsService smsService;
    private final UserService userService; // To get department head info

    /**
     * Create a new rectification request
     */
    public Rectification create(RectificationRequestDTO dto, String username) {
        // Find department head based on option
        String chefDepartement = userService.findChefDepartementByOption(dto.getOption());

        Rectification r = new Rectification();
        r.setEtudiantPrenom(dto.getEtudiantPrenom());
        r.setEtudiantNom(dto.getEtudiantNom());
        r.setClasse(dto.getClasse());
        r.setOption(dto.getOption());
        r.setAncienneNote(dto.getAncienneNote());
        r.setNouvelleNote(dto.getNouvelleNote());
        r.setJustification(dto.getJustification());

//        r.setStatus("EN_ATTENTE_SMS");
//        r.setEnseignantUsername(username);
//        r.setChefDepartementUsername(chefDepartement);
//        r.setDateDemande(LocalDateTime.now());
//        r.setSmsVerified(false);
//
//        // Generate SMS code
//        String smsCode = smsService.generateSmsCode();
//        r.setSmsCode(smsCode);
//        r.setSmsCodeExpiry(smsService.getCodeExpiry());
//
//        Rectification saved = repo.save(r);
//
//        // Send SMS code (mock implementation)
//        // In real scenario, get teacher's phone number from user service
//        String teacherPhone = userService.getTeacherPhoneNumber(username);
//        smsService.sendSmsCode(teacherPhone, smsCode);

        //Rectification saved = repo.save(r);

        //log.info("Rectification request created without SMS verification (test mode) for teacher: {}", username);

        r.setStatus("EN_ATTENTE"); // ← sauter la vérification SMS
        r.setEnseignantUsername(username);
        r.setChefDepartementUsername(chefDepartement);
        r.setDateDemande(LocalDateTime.now());
        r.setSmsVerified(true);    // ← déjà validée
        r.setSmsCode(null);
        r.setSmsCodeExpiry(null);

        // Sauvegarder sans SMS
        Rectification saved = repo.save(r);



        log.info("Rectification request created with ID: {} for teacher: {}", saved.getId(), username);
        return saved;
    }

    /**
     * Verify SMS code
     */
    public boolean verifySmsCode(SmsVerificationDTO dto) {
        Rectification rectification = repo.findById(dto.getRectificationId())
                .orElseThrow(() -> new RuntimeException("Rectification not found"));

        if (smsService.isCodeValid(dto.getSmsCode(), rectification.getSmsCode(), rectification.getSmsCodeExpiry())) {
            rectification.setSmsVerified(true);
            rectification.setStatus("EN_ATTENTE");
            rectification.setSmsCode(null); // Clear the code for security
            rectification.setSmsCodeExpiry(null);
            repo.save(rectification);

            log.info("SMS verification successful for rectification ID: {}", dto.getRectificationId());
            return true;
        }

        log.warn("SMS verification failed for rectification ID: {}", dto.getRectificationId());
        return false;
    }

    /**
     * Get all rectifications
     */
    public List<RectificationResponseDTO> findAll() {
        return repo.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get rectifications by teacher username
     */
    public List<RectificationResponseDTO> findByEnseignantUsername(String username) {
        return repo.findByEnseignantUsername(username).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get rectifications by department head username
     */
    public List<RectificationResponseDTO> findByChefDepartementUsername(String username) {
        return repo.findByChefDepartementUsername(username).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update rectification status (for department head)
     */
    public Rectification updateStatus(Long id, StatusUpdateDTO dto, String chefUsername) {
        Rectification r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rectification not found"));

        // Verify that the chef is authorized to update this rectification
        if (!r.getChefDepartementUsername().equals(chefUsername)) {
            throw new RuntimeException("Unauthorized to update this rectification");
        }

        r.setStatus(dto.getStatus());
        r.setDateTraitement(LocalDateTime.now());

        if ("REFUSEE".equals(dto.getStatus()) && dto.getMotifRefus() != null) {
            r.setMotifRefus(dto.getMotifRefus());
        }

        Rectification updated = repo.save(r);
        log.info("Rectification ID: {} status updated to: {} by chef: {}", id, dto.getStatus(), chefUsername);

        return updated;
    }

    /**
     * Get rectification history for a teacher
     */
    public List<RectificationResponseDTO> getTeacherHistory(String username) {
        return repo.findByEnseignantUsernameOrderByDateDemandeDesc(username).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get rectification history for a department head
     */
    public List<RectificationResponseDTO> getChefHistory(String username) {
        return repo.findByChefDepartementUsernameOrderByDateDemandeDesc(username).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to response DTO
     */
    private RectificationResponseDTO convertToResponseDTO(Rectification rectification) {
        RectificationResponseDTO dto = new RectificationResponseDTO();
        dto.setId(rectification.getId());
        dto.setEtudiantPrenom(rectification.getEtudiantPrenom());
        dto.setEtudiantNom(rectification.getEtudiantNom());
        dto.setClasse(rectification.getClasse());
        dto.setOption(rectification.getOption());
        dto.setAncienneNote(rectification.getAncienneNote());
        dto.setNouvelleNote(rectification.getNouvelleNote());
        dto.setJustification(rectification.getJustification());
        dto.setStatus(rectification.getStatus());
        dto.setEnseignantUsername(rectification.getEnseignantUsername());
        dto.setChefDepartementUsername(rectification.getChefDepartementUsername());
        dto.setDateDemande(rectification.getDateDemande());
        dto.setDateTraitement(rectification.getDateTraitement());
        dto.setSmsVerified(rectification.isSmsVerified());
        dto.setMotifRefus(rectification.getMotifRefus());
        return dto;
    }
}