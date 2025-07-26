package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microserviceplanification.Entity.Salle;


@Repository
public interface SalleRepo extends JpaRepository<Salle, Integer> {





}
