package tn.esprit.microserviceplanification.Entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClasseDTO {
    private Integer id;
    private String nom;
    private String description;
    private Integer optionId;
    private String optionNom;
}
