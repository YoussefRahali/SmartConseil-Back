package tn.esprit.microservicerapport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservicerapport.entity.Rapport;
import tn.esprit.microservicerapport.entity.Rapport.StatutRapport;

import java.util.List;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {
    
    // Find reports by rapporteur username
    List<Rapport> findByRapporteurUsername(String rapporteurUsername);
    
    // Find reports by rapporteur username ordered by creation date
    List<Rapport> findByRapporteurUsernameOrderByDateCreationDesc(String rapporteurUsername);
    
    // Find reports by status
    List<Rapport> findByStatut(StatutRapport statut);
    
    // Find reports by class
    List<Rapport> findByClasse(String classe);
    
    // Find reports by rapporteur and status
    List<Rapport> findByRapporteurUsernameAndStatut(String rapporteurUsername, StatutRapport statut);
    
    // Find reports by sector
    List<Rapport> findBySecteur(String secteur);
    
    // Find reports by academic year
    List<Rapport> findByAnneeAcademique(String anneeAcademique);
}
