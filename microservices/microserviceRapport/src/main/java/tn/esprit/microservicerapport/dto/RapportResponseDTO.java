package tn.esprit.microservicerapport.dto;

import lombok.Data;
import tn.esprit.microservicerapport.entity.Rapport.StatutRapport;

import java.time.LocalDateTime;

@Data
public class RapportResponseDTO {
    private Long id;
    private String titre;
    private String contenu;
    private String option;
    private String classe;
    private String rapporteurUsername;
    private StatutRapport statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime dateValidation;
    private String secteur;
    private String anneeAcademique;
    private String semestre;
}
