package com.example.microserviceuser.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Column(unique = true, nullable = false) // Empêche les doublons d'email
    private String email;
    private String password;
    private String role;
    private String poste;
    private String secteur;
    private String phoneNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String profilePicture; // Base64 encoded image data
}
