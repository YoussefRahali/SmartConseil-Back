package tn.esprit.microserviceplanification.Entity;

import lombok.*;
import java.time.LocalTime;
import java.util.Date;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConseilDTO {
    private Integer id;
    private Date date;
    private String duree;
    private String description;
<<<<<<< HEAD
    private Integer optionId;
    private List<Integer> classeIds;
=======
    private String classes;
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
    private LocalTime heure;

    private Integer salleId;

    private Long presidentId;
    private Long raporteurId;
    private String deroulement;
}
