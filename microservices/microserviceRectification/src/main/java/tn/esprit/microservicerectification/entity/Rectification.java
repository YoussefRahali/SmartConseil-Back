package tn.esprit.microservicerectification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rectification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student information - separate first and last name as required
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

    private String status; // EN_ATTENTE / ACCEPTEE / REFUSEE

    private String enseignantUsername; // depuis le token
    private String chefDepartementUsername;

    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;

    // SMS verification fields
    private boolean smsVerified;
    private String smsCode;
    private LocalDateTime smsCodeExpiry;

    // Additional tracking fields
    private String motifRefus; // Reason for rejection if status is REFUSEE
}