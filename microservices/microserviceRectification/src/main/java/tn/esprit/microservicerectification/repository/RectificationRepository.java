package tn.esprit.microservicerectification.repository;

import tn.esprit.microservicerectification.entity.Rectification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RectificationRepository extends JpaRepository<Rectification, Long> {
    List<Rectification> findByEnseignantUsername(String username);
    List<Rectification> findByChefDepartementUsername(String username);
    List<Rectification> findByEnseignantUsernameOrderByDateDemandeDesc(String username);
    List<Rectification> findByChefDepartementUsernameOrderByDateDemandeDesc(String username);
    List<Rectification> findByStatus(String status);
}
