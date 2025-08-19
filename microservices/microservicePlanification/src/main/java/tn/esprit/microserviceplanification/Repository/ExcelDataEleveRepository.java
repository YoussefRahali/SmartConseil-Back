package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microserviceplanification.Entity.ExcelDataEleve;

import java.util.List;

public interface ExcelDataEleveRepository extends JpaRepository<ExcelDataEleve, Long> {
    List<ExcelDataEleve> findByConseilId(Integer conseilId);
}