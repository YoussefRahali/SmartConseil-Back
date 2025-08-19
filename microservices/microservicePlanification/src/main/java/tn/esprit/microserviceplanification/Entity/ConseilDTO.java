package tn.esprit.microserviceplanification.Entity;

import lombok.*;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConseilDTO {
    private Integer id;
    private Date date;
    private String duree;
    private String description;
    private Integer optionId;
    private List<Integer> classeIds;
    private LocalTime heure;

    private Integer salleId;

    private Long presidentId;
    private Long raporteurId;
    private String deroulement;
}
