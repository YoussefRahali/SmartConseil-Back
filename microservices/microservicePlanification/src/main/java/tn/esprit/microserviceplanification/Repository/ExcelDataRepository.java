package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microserviceplanification.Entity.ExcelData;

@Repository
public interface ExcelDataRepository extends JpaRepository<ExcelData, Long> {
}