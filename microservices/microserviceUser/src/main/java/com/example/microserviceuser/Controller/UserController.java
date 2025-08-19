package com.example.microserviceuser.Controller;

import com.example.microserviceuser.Entity.User;
import com.example.microserviceuser.Repository.UserRepository;
import com.example.microserviceuser.dto.PasswordChangeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Test endpoint to verify backend is running
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Backend is running successfully!");
    }

    // Endpoint accessible par les chefs de département et les admins
    @GetMapping("/all")
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Endpoint accessible par tous les utilisateurs authentifiés
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserProfile(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour mettre à jour le profil (accessible par tous les utilisateurs authentifiés)
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateProfile(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setPoste(user.getPoste());
            existingUser.setSecteur(user.getSecteur());
            if (user.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(user.getPhoneNumber());
            }
            if (user.getProfilePicture() != null) {
                existingUser.setProfilePicture(user.getProfilePicture());
            }
            userRepository.save(existingUser);
            return ResponseEntity.ok(existingUser);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour changer le mot de passe (accessible par tous les utilisateurs authentifiés)
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody PasswordChangeRequest request, Authentication authentication) {
        System.out.println("Change password request received for user: " + authentication.getName());
        System.out.println("Request data - Current password provided: " + (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty()));
        System.out.println("Request data - New password provided: " + (request.getNewPassword() != null && !request.getNewPassword().isEmpty()));

        Map<String, String> response = new HashMap<>();
        String userEmail = authentication.getName();
        User existingUser = userRepository.findByEmail(userEmail);

        if (existingUser == null) {
            System.out.println("User not found: " + userEmail);
            response.put("error", "Utilisateur non trouvé");
            return ResponseEntity.status(404).body(response);
        }

        // Vérification mot de passe actuel
        System.out.println("Verifying current password...");
        if (!passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword())) {
            System.out.println("Current password verification failed");
            response.put("error", "Mot de passe actuel incorrect");
            return ResponseEntity.badRequest().body(response);
        }

        // Vérification nouveau mot de passe
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            System.out.println("New password validation failed - too short or null");
            response.put("error", "Le nouveau mot de passe doit contenir au moins 6 caractères");
            return ResponseEntity.badRequest().body(response);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            System.out.println("Password confirmation mismatch");
            response.put("error", "Les mots de passe ne correspondent pas");
            return ResponseEntity.badRequest().body(response);
        }

        // Mise à jour mot de passe
        System.out.println("Updating password...");
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(existingUser);
        System.out.println("Password updated successfully");

        response.put("message", "Mot de passe changé avec succès");
        return ResponseEntity.ok(response);
    }

    // Endpoint pour mettre à jour la photo de profil
    @PutMapping("/profile-picture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> updateProfilePicture(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        try {
            System.out.println("=== Profile Picture Update Request ===");
            String email = request.get("email");
            String profilePicture = request.get("profilePicture");

            System.out.println("Email: " + email);
            System.out.println("ProfilePicture length: " + (profilePicture != null ? profilePicture.length() : "null"));
            System.out.println("ProfilePicture preview: " + (profilePicture != null ? profilePicture.substring(0, Math.min(50, profilePicture.length())) + "..." : "null"));

            if (email == null || email.trim().isEmpty()) {
                System.out.println("ERROR: Email is null or empty");
                response.put("error", "Email est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (profilePicture == null || profilePicture.trim().isEmpty()) {
                System.out.println("ERROR: ProfilePicture is null or empty");
                response.put("error", "Image de profil est requise");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation de base pour s'assurer que c'est une image base64 valide
            if (!profilePicture.startsWith("data:image/")) {
                System.out.println("ERROR: Invalid image format - doesn't start with data:image/");
                response.put("error", "Format d'image invalide");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérifier la taille de l'image (limite à 5MB en base64)
            if (profilePicture.length() > 7000000) { // ~5MB en base64
                System.out.println("ERROR: Image too large - " + profilePicture.length() + " characters");
                response.put("error", "Image trop volumineuse (max 5MB)");
                return ResponseEntity.badRequest().body(response);
            }

            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                System.out.println("ERROR: User not found for email: " + email);
                response.put("error", "Utilisateur non trouvé");
                return ResponseEntity.notFound().build();
            }

            System.out.println("User found: " + existingUser.getUsername() + " (ID: " + existingUser.getId() + ")");
            System.out.println("Current profile picture: " + (existingUser.getProfilePicture() != null ? "EXISTS (" + existingUser.getProfilePicture().length() + " chars)" : "NULL"));

            existingUser.setProfilePicture(profilePicture);
            User savedUser = userRepository.save(existingUser);

            System.out.println("User saved successfully. New profile picture: " + (savedUser.getProfilePicture() != null ? "EXISTS (" + savedUser.getProfilePicture().length() + " chars)" : "NULL"));

            response.put("message", "Photo de profil mise à jour avec succès");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la photo de profil: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Erreur interne du serveur");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Endpoint pour supprimer un utilisateur (chef de département et admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour obtenir les utilisateurs par rôle (chef de département et admin)
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('CHEF_DEPARTEMENT') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userRepository.findByRole(role);
        return ResponseEntity.ok(users);
    }

    // Endpoint pour trouver le chef de département par secteur
    @GetMapping("/chef-by-sector/{sector}")
    public ResponseEntity<String> getChefBySector(@PathVariable String sector) {
        System.out.println("Looking for chef in sector: '" + sector + "'");

        // Try exact match first
        User chef = userRepository.findByRoleAndSecteur("chef departement", sector);
        System.out.println("Exact match result: " + (chef != null ? chef.getEmail() : "null"));

        // If not found, try case-insensitive match
        if (chef == null) {
            List<User> allChefs = userRepository.findByRole("chef departement");
            System.out.println("Total chefs found: " + allChefs.size());

            for (User c : allChefs) {
                System.out.println("Chef: " + c.getEmail() + " - Secteur: '" + c.getSecteur() + "'");
            }

            chef = allChefs.stream()
                .filter(c -> c.getSecteur() != null && c.getSecteur().equalsIgnoreCase(sector))
                .findFirst()
                .orElse(null);

            System.out.println("Case-insensitive match result: " + (chef != null ? chef.getEmail() : "null"));
        }

        if (chef != null) {
            System.out.println("SUCCESS: Returning chef: " + chef.getEmail() + " for sector: " + sector);
            return ResponseEntity.ok(chef.getEmail());
        }

        // Return default chef if no specific chef found for the sector
        System.out.println("FALLBACK: No chef found for sector '" + sector + "', returning default chef@test.com");
        return ResponseEntity.ok("chef@test.com");
    }

    // Debug endpoint to list all chefs
    @GetMapping("/debug/chefs")
    public ResponseEntity<Map<String, Object>> listAllChefs() {
        List<User> allChefs = userRepository.findByRole("chef departement");
        Map<String, Object> response = new HashMap<>();

        response.put("totalChefs", allChefs.size());

        List<Map<String, String>> chefDetails = new ArrayList<>();
        for (User chef : allChefs) {
            Map<String, String> chefInfo = new HashMap<>();
            chefInfo.put("email", chef.getEmail());
            chefInfo.put("username", chef.getUsername());
            chefInfo.put("secteur", chef.getSecteur());
            chefDetails.add(chefInfo);
        }

        response.put("chefs", chefDetails);
        return ResponseEntity.ok(response);
    }

    // Admin-specific endpoints

    // Endpoint pour mettre à jour un utilisateur (admin seulement)
    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserByAdmin(@PathVariable Long id, @RequestBody User userUpdate) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setUsername(userUpdate.getUsername());
            existingUser.setPoste(userUpdate.getPoste());
            existingUser.setSecteur(userUpdate.getSecteur());
            existingUser.setRole(userUpdate.getRole());
            if (userUpdate.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(userUpdate.getPhoneNumber());
            }
            if (userUpdate.getProfilePicture() != null) {
                existingUser.setProfilePicture(userUpdate.getProfilePicture());
            }
            // Don't update password here - use separate endpoint for password changes
            userRepository.save(existingUser);
            return ResponseEntity.ok(existingUser);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour obtenir les statistiques des utilisateurs (admin seulement)
    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total users
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        // Users by role
        Map<String, Long> usersByRole = new HashMap<>();
        usersByRole.put("enseignant", (long) userRepository.findByRole("enseignant").size());
        usersByRole.put("chef departement", (long) userRepository.findByRole("chef departement").size());
        usersByRole.put("rapporteur", (long) userRepository.findByRole("rapporteur").size());
        usersByRole.put("admin", (long) userRepository.findByRole("admin").size());
        stats.put("usersByRole", usersByRole);

        // Users by sector
        Map<String, Long> usersBySector = new HashMap<>();

        List<String> sectors = List.of("informatique", "mathématique", "telecommunication", "EM", "gc");

        for (String sector : sectors) {
            long count = userRepository.findAll().stream()
                .filter(user -> sector.equalsIgnoreCase(user.getSecteur()))
                .count();
            usersBySector.put(sector, count);
        }
        stats.put("usersBySector", usersBySector);

        return ResponseEntity.ok(stats);
    }

    // Endpoint pour obtenir l'état du système (admin seulement)
    @GetMapping("/admin/system-health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        // Database connectivity
        try {
            long userCount = userRepository.count();
            health.put("database", "UP");
            health.put("userCount", userCount);
        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("databaseError", e.getMessage());
        }

        // Memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        Map<String, Object> memory = new HashMap<>();
        memory.put("total", totalMemory / (1024 * 1024)); // MB
        memory.put("used", usedMemory / (1024 * 1024)); // MB
        memory.put("free", freeMemory / (1024 * 1024)); // MB
        health.put("memory", memory);

        // System uptime (simplified)
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(health);
    }

    // Endpoint pour obtenir les activités récentes (admin seulement)
    @GetMapping("/admin/recent-activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();

        // Recent user registrations (last 30 days)
        // This is a simplified version - in a real app, you'd have audit tables
        List<User> recentUsers = userRepository.findAll().stream()
            .sorted((u1, u2) -> Long.compare(u2.getId(), u1.getId()))
            .limit(10)
            .collect(java.util.stream.Collectors.toList());

        activities.put("recentUsers", recentUsers.stream()
            .map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole());
                return userInfo;
            })
            .collect(java.util.stream.Collectors.toList()));

        activities.put("totalActivities", recentUsers.size());
        activities.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(activities);
    }
}
