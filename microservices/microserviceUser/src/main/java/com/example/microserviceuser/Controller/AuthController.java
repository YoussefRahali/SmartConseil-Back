package com.example.microserviceuser.Controller;

import com.example.microserviceuser.Entity.User;
import com.example.microserviceuser.Entity.UserDTO;
import com.example.microserviceuser.Repository.UserRepository;
import com.example.microserviceuser.Service.auth.CustomUserDetailsService;
import com.example.microserviceuser.Service.auth.JwtUtils;
import com.example.microserviceuser.Service.auth.MailService;
import com.example.microserviceuser.Service.auth.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomUserDetailsService userService;

    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final PasswordResetTokenService tokenService;
    private final MailService mailService;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository,
                          AuthenticationManager authenticationManager, CustomUserDetailsService userService ,PasswordResetTokenService tokenService,MailService mailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.mailService = mailService;// Injection correcte
    }

    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()) != null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Email already exists");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Encode password and save user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @ResponseBody
    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, String>> createAdminUser() {
        try {
            // Check if admin already exists
            if (userRepository.findByEmail("admin@smartconseil.com") != null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Admin user already exists");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Create admin user
            User adminUser = new User();
            adminUser.setUsername("Admin");
            adminUser.setEmail("admin@smartconseil.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("chef departement");
            adminUser.setPoste("Chef de Département");
            adminUser.setSecteur("Administration");

            userRepository.save(adminUser);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Admin user created successfully");
            response.put("email", "admin@smartconseil.com");
            response.put("password", "admin123");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Admin creation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> authenticate(@RequestBody User user) {
        System.out.println("Tentative de connexion pour l'utilisateur : " + user.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (Exception e) {
            System.out.println("Échec de l'authentification : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Récupérer le rôle de l'utilisateur depuis la base de données
        String role = userService.getUserRoleByUsername(user.getEmail());
        String username = userService.getUserusername(user.getEmail());
        //String token = JwtUtils.generateToken(username, role);
        String token = JwtUtils.generateToken(user.getEmail(), role); // ✅ PAS username ici

        // ➕ Injecter dans le contexte Spring Security
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                token, // credentials
                null
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Récupérer l'ID de l'utilisateur depuis la base de données ou le service utilisateur
        Long userId = userService.getUserIdByUsername(user.getEmail());
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setToken(token);
        userDTO.setId(userId);
        userDTO.setRole(role);
        userDTO.setEmail(user.getEmail());
        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/me")
    public ResponseEntity<UserDetails> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails user = userService.loadUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/test")
    public String test() {
        return "message from backend successfully";
    }
    // 📌 Endpoint pour demander la réinitialisation du mot de passe
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody UserDTO request) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(request.getEmail()));
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email non trouvé");
        }

        User user = userOpt.get();
        String token = JwtUtils.generateToken(user.getEmail());
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        String subject = "Réinitialisation de votre mot de passe";
        String message = "<p>Bonjour,</p>" +
                "<p>Vous avez demandé une réinitialisation de votre mot de passe.</p>" +
                "<p>Cliquez sur le lien ci-dessous pour le réinitialiser :</p>" +
                "<p><a href=\"" + resetLink + "\">Réinitialiser mon mot de passe</a></p>" +
                "<p>Si vous n'avez pas fait cette demande, ignorez cet email.</p>";

        try {
            mailService.sendEmail(user.getEmail(), subject, message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

        return ResponseEntity.ok("Email de réinitialisation envoyé !");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody User user) {
        String email = JwtUtils.extractUsername(token);
        if (email == null) {
            return ResponseEntity.badRequest().body("Token invalide ou expiré");
        }

        Optional<User> existingUserOpt = Optional.ofNullable(userRepository.findByEmail(email));
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }

        User existingUser = existingUserOpt.get();
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(existingUser);

        return ResponseEntity.ok("Mot de passe réinitialisé avec succès !");
    }
    @PostMapping("/ajouterUser")
    public User ajouterUser(@RequestBody User user) {
        User nouvelUtilisateur = userRepository.save(user);
        return nouvelUtilisateur;
    }

    // Debug endpoint to create test users
    @PostMapping("/create-test-users")
    public ResponseEntity<Map<String, String>> createTestUsers() {
        try {
            Map<String, String> response = new HashMap<>();

            // Create enseignant user
            if (userRepository.findByEmail("enseignant@test.com") == null) {
                User enseignant = new User();
                enseignant.setUsername("Enseignant Test");
                enseignant.setEmail("enseignant@test.com");
                enseignant.setPassword(passwordEncoder.encode("password123"));
                enseignant.setRole("enseignant");
                enseignant.setPoste("Professeur");
                enseignant.setSecteur("Informatique");
                userRepository.save(enseignant);
                response.put("enseignant", "Created: enseignant@test.com / password123");
            }

            // Create chef departement user
            if (userRepository.findByEmail("chef@test.com") == null) {
                User chef = new User();
                chef.setUsername("Chef Test");
                chef.setEmail("chef@test.com");
                chef.setPassword(passwordEncoder.encode("password123"));
                chef.setRole("chef departement");
                chef.setPoste("Chef de Département");
                chef.setSecteur("Informatique");
                userRepository.save(chef);
                response.put("chef", "Created: chef@test.com / password123");
            }

            response.put("message", "Test users created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create test users: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Debug endpoint to check user
    @GetMapping("/check-user/{email}")
    public ResponseEntity<Map<String, Object>> checkUser(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("found", true);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("poste", user.getPoste());
            response.put("secteur", user.getSecteur());
            response.put("passwordEncoded", user.getPassword() != null && user.getPassword().startsWith("$2"));
        } else {
            response.put("found", false);
            response.put("message", "User not found");
        }

        return ResponseEntity.ok(response);
    }

}