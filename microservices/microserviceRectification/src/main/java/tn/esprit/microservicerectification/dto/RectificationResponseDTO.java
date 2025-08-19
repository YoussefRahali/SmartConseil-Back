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
<<<<<<< HEAD
=======
    private String module;
    private String typeNote;
    private String session;
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
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
