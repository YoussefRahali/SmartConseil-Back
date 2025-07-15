package com.example.microserviceuser.Controller;

import com.example.microserviceuser.Entity.User;
import com.example.microserviceuser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Endpoint accessible seulement par les chefs de département
    @GetMapping("/all")
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Endpoint accessible par les enseignants et chefs de département
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('CHEF DEPARTEMENT')")
    public ResponseEntity<User> getUserProfile(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour mettre à jour le profil (accessible par tous les utilisateurs authentifiés)
    @PutMapping("/profile")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('CHEF DEPARTEMENT')")
    public ResponseEntity<User> updateProfile(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setPoste(user.getPoste());
            existingUser.setSecteur(user.getSecteur());
            userRepository.save(existingUser);
            return ResponseEntity.ok(existingUser);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour supprimer un utilisateur (seulement chef de département)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour obtenir les utilisateurs par rôle (seulement chef de département)
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('CHEF DEPARTEMENT')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userRepository.findByRole(role);
        return ResponseEntity.ok(users);
    }

    // Endpoint pour trouver le chef de département par secteur
    @GetMapping("/chef-by-sector/{sector}")
    public ResponseEntity<String> getChefBySector(@PathVariable String sector) {
        User chef = userRepository.findByRoleAndSecteur("chef departement", sector);
        if (chef != null) {
            return ResponseEntity.ok(chef.getEmail());
        }
        // Return default chef if no specific chef found for the sector
        return ResponseEntity.ok("chef@test.com");
    }
}
