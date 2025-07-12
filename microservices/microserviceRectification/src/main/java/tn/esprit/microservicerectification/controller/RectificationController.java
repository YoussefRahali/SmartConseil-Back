package tn.esprit.microservicerectification.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import tn.esprit.microservicerectification.dto.RectificationRequestDTO;
import tn.esprit.microservicerectification.entity.Rectification;
import tn.esprit.microservicerectification.service.RectificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/rectification")
@RequiredArgsConstructor
@CrossOrigin
public class RectificationController {

    private final RectificationService service;

    @GetMapping
    public List<Rectification> getAll() {
        return service.findAll();
    }

    @PreAuthorize("hasRole('enseignant')")
    @PostMapping
    public Rectification create(@RequestBody RectificationRequestDTO dto, Principal principal) {
        return service.create(dto, principal.getName());
    }

    @PreAuthorize("hasRole('chefDepartement')")
    @PutMapping("/{id}/status")
    public Rectification updateStatus(@PathVariable Long id, @RequestParam String status, Principal principal) {
        return service.updateStatus(id, status, principal.getName());
    }

}