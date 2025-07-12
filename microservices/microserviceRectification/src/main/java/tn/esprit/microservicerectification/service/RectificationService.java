package tn.esprit.microservicerectification.service;

import tn.esprit.microservicerectification.dto.RectificationRequestDTO;
import tn.esprit.microservicerectification.dto.RectificationResponseDTO;
import tn.esprit.microservicerectification.entity.Rectification;
import tn.esprit.microservicerectification.repository.RectificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RectificationService {
    private final RectificationRepository repo;

    public Rectification create(RectificationRequestDTO dto, String username) {
        Rectification r = new Rectification();
        r.setEtudiantNom(dto.getEtudiantNom());
        r.setClasse(dto.getClasse());
        r.setOption(dto.getOption());
        r.setAncienneNote(dto.getAncienneNote());
        r.setNouvelleNote(dto.getNouvelleNote());
        r.setJustification(dto.getJustification());
        r.setStatus("EN_ATTENTE");
        r.setEnseignantUsername(username);
        r.setDateDemande(LocalDateTime.now());
        return repo.save(r);
    }

    public List<Rectification> findAll() {
        return repo.findAll();
    }

    public Rectification updateStatus(Long id, String status, String chef) {
        Rectification r = repo.findById(id).orElseThrow();
        r.setStatus(status);
        r.setChefDepartementUsername(chef);
        return repo.save(r);
    }
}
