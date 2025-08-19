package com.esprit.microservice.conseil.repository;

import com.esprit.microservice.conseil.model.ExcelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelDataRepository extends JpaRepository<ExcelData, Long> {
}