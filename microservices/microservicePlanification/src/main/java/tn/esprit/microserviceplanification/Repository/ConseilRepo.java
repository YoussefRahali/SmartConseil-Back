package tn.esprit.microserviceplanification.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microserviceplanification.Entity.Conseil;
@Repository
public interface ConseilRepo extends JpaRepository<Conseil, Integer> {





}
