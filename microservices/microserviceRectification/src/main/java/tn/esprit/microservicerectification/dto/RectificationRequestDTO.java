package tn.esprit.microservicerectification.dto;

import lombok.Data;

@Data
public class RectificationRequestDTO {
    private String etudiantPrenom;
    private String etudiantNom;
    private String classe;
    private String option;
    private String module;      // new
    private String typeNote;    // TP | CC | Examen | PI
    private String session;     // Principale | Rattrapage
    private Double ancienneNote;
    private Double nouvelleNote;
    private String justification;
}
