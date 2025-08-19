package tn.esprit.microservicerectification.dto;

import lombok.Data;

@Data
public class StatusUpdateDTO {
    private String status; // ACCEPTEE / REFUSEE
    private String motifRefus; // Required if status is REFUSEE
}
