package tn.esprit.microserviceplanification.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDataEleve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String id_et;
    private String nom_et;
    private String pnom_et;
    private String classe_courante_et;
    private String adresse_mail_et;
    private String session_principale;
    private String remarque;
    private String Rachat_Moyenne;
    private String Rachat_UE;
    private  String Rachat_Module;
    private String Conseil_de_ecole;
    private String DossierMedical;


    private Integer conseilId;

}