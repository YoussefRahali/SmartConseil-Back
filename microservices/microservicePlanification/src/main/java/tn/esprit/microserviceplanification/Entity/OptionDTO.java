package tn.esprit.microserviceplanification.Entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO {
    private Integer id;
    private String nom;
    private String description;
}
