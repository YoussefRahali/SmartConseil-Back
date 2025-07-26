package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microserviceplanification.Entity.ConseilUtilisateur;

import java.util.List;
import java.util.Optional;
@Repository
public interface ConseilUtilisateurRepo extends JpaRepository<ConseilUtilisateur,Long> {
    Optional<ConseilUtilisateur> findByConseilIdAndUtilisateurId(Integer conseilId, Long utilisateurId);
    List<ConseilUtilisateur> findByUtilisateurId(Long utilisateurId);

}
