package tn.esprit.microservicerapport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rapports")
public class Rapport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Column(nullable = false)
    private String option ;

    @Column(nullable = false)
    private String classe;

    @Column(nullable = false)
    private String rapporteurUsername; // Username of the rapporteur who created the report

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRapport statut; // BROUILLON, VALIDE

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    private LocalDateTime dateValidation;

    // Additional metadata
    private String secteur; // Department/sector
    private String anneeAcademique; // Academic year
    private String semestre; // Semester

    public enum StatutRapport {
        BROUILLON,
        VALIDE
    }
}
