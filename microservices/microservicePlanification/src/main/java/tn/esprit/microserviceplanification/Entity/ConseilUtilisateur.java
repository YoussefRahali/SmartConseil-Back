package tn.esprit.microserviceplanification.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConseilUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long utilisateurId;

    private String message;
    private String justifictaion;

    @ManyToOne
    @JoinColumn(name = "conseil_id")
    @JsonIgnoreProperties("participants") // 🔁 coupe la boucle JSON
    private Conseil conseil;
}
