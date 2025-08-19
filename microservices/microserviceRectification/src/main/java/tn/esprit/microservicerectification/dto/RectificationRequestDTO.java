package tn.esprit.microservicerectification.dto;

import lombok.Data;

@Data
public class RectificationRequestDTO {
    private String etudiantPrenom;
    private String etudiantNom;
    private String classe;
    private String option;
<<<<<<< HEAD
=======
    private String module;      // new
    private String typeNote;    // TP | CC | Examen | PI
    private String session;     // Principale | Rattrapage
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
    private Double ancienneNote;
    private Double nouvelleNote;
    private String justification;
}
