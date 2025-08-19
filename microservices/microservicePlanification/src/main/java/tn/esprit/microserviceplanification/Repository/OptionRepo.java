package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microserviceplanification.Entity.Option;

@Repository
public interface OptionRepo extends JpaRepository<Option, Integer> {
    Option findByNom(String nom);
}
