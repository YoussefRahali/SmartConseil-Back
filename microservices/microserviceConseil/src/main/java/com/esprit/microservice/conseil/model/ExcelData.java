package com.esprit.microservice.conseil.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String no;
    private String option;
    private String idEt;
    private String nom;
    private String prenom;
    private String emailEt;
    private String adresseMailEsp;
}