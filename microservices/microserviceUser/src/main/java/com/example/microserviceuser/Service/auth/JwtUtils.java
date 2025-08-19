package com.example.microserviceuser.Service.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtils {
    // Définir une clé secrète statique et unique
    private static final String SECRET_KEY = "votre_clé_secrète_uniquevzrbgrbaefbaetntenadsvjsdkvblrBKVSPVOUBZDVP OUBFVPOUV SBDVBSDVÖSDVBSDVUOBSDVBF IUFVBJEFVI"; // Utilisez une chaîne secrète robuste
    private static final Key KEY = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    private static final long EXPIRATION_TIME = 86400000; // 24 heures (en millisecondes)

    // Génération du token avec rôle
//    public static String generateToken(String username, String role) {
//        return Jwts.builder()
//                //.setSubject(username)  // Ajoute l'username dans le JWT
//                .claim("role", role)   // Ajoute le rôle dans le JWT
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Expiration dans 24 heures
//                .signWith(KEY)  // Signature avec la clé secrète
//                .compact();  // Génère le token compact
//    }

    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)  // Ajoute l'email dans le JWT comme subject
                .claim("role", role)   // Ajoute le rôle dans le JWT
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Expiration dans 24 heures
                .signWith(KEY)  // Signature avec la clé secrète
                .compact();  // Génère le token compact
    }

    // Génération du token (méthode de compatibilité)
    public static String generateToken(String username) {
        return generateToken(username, "USER");
    }

    // Extraction de l'username à partir du token
    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)  // Utilisation de la même clé pour vérifier la signature
                .build()
                .parseClaimsJws(token)  // Parse le token
                .getBody()
                .getSubject();  // Récupère l'username
    }

    // Extraction du rôle à partir du token
    public static String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)  // Utilisation de la même clé pour vérifier la signature
                .build()
                .parseClaimsJws(token)  // Parse le token
                .getBody()
                .get("role", String.class);  // Récupère le rôle
    }

    // Validation du token
    public static boolean validateToken(String jwtToken, UserDetails userDetails) {
        String username = extractUsername(jwtToken);  // Récupère l'username du token
        return username.equals(userDetails.getUsername());  // Compare avec l'username du UserDetails
    }
}
