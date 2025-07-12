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

    private String etudiantNom;
    private String classe;
    private String option;
    private Double ancienneNote;
    private Double nouvelleNote;
    private String justification;

    private String status; // EN_ATTENTE / ACCEPTEE / REFUSEE

    private String enseignantUsername; // depuis le token
    private String chefDepartementUsername;

    private LocalDateTime dateDemande;
}