package tn.esprit.microservicerectification.dto;

import lombok.Data;

@Data
public class RectificationRequestDTO {
    private String etudiantNom;
    private String classe;
    private String option;
    private Double ancienneNote;
    private Double nouvelleNote;
    private String justification;
}
