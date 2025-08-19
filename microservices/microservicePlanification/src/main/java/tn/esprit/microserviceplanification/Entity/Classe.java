package tn.esprit.microserviceplanification.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "classes")
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "option_id", nullable = false)
    @JsonIgnoreProperties({"classes", "conseils", "hibernateLazyInitializer", "handler"})
    private Option option;

    @ManyToMany(mappedBy = "classes", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("classes")
    private List<Conseil> conseils = new ArrayList<>();
}
