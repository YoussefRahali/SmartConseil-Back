package tn.esprit.microserviceplanification.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Conseil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String Duree;
    private String Description;
    private String classes;
    private LocalTime heure;

    private Boolean etat = false;

    @OneToMany(mappedBy = "conseil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConseilUtilisateur> participants = new ArrayList<>();

    private Long presidentId;
    private Long raporteurId;
    private String deroulement;
    private String token;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties("conseils")
    private Salle salle;

}
