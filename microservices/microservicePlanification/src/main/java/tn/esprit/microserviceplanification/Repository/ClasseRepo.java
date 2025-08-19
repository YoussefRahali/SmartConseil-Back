package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microserviceplanification.Entity.Classe;

import java.util.List;

@Repository
public interface ClasseRepo extends JpaRepository<Classe, Integer> {
    List<Classe> findByOptionId(Integer optionId);
    Classe findByNom(String nom);
}
