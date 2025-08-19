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
<<<<<<< HEAD
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    @JsonIgnoreProperties({"conseils", "classes", "participants", "hibernateLazyInitializer", "handler"})
    private Option option;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "conseil_classes",
        joinColumns = @JoinColumn(name = "conseil_id"),
        inverseJoinColumns = @JoinColumn(name = "classe_id")
    )
    @JsonIgnoreProperties("conseils")
    private List<Classe> classes = new ArrayList<>();
=======
    private String classes;
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
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
<<<<<<< HEAD
    @JsonIgnoreProperties({"conseils", "participants", "hibernateLazyInitializer", "handler"})
=======
    @JsonIgnoreProperties("conseils")
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
    private Salle salle;

}
