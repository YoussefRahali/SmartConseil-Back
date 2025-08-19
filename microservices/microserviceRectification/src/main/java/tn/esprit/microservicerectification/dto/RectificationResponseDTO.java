package tn.esprit.microservicerectification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RectificationResponseDTO {
    private Long id;
    private String etudiantPrenom;
    private String etudiantNom;
    private String classe;
    private String option;
    private Double ancienneNote;
    private Double nouvelleNote;
    private String justification;
    private String status;
    private String enseignantUsername;
    private String chefDepartementUsername;
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
    private boolean smsVerified;
    private String motifRefus;
}
