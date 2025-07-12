package tn.esprit.microservicerectification.repository;

import tn.esprit.microservicerectification.entity.Rectification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RectificationRepository extends JpaRepository<Rectification, Long> {
    List<Rectification> findByEnseignantUsername(String username);
}
