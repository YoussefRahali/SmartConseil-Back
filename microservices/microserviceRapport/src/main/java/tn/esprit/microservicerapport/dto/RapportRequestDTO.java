package tn.esprit.microservicerapport.dto;

import lombok.Data;

@Data
public class RapportRequestDTO {
    private String titre;
    private String contenu;
    private String option;
    private String classe;
    private String secteur;
    private String anneeAcademique;
    private String semestre;
}
