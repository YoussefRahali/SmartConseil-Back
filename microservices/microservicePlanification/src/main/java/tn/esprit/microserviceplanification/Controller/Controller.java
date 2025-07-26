package tn.esprit.microserviceplanification.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microserviceplanification.Entity.*;
import tn.esprit.microserviceplanification.Repository.UtilisateurClient;
import tn.esprit.microserviceplanification.Service.MailService;
import tn.esprit.microserviceplanification.Service.ServiceImpl;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/conseils")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.1.13:4200"})
public class Controller {
    @Autowired
    private ServiceImpl serviceIMPL;
    @Autowired
    private MailService mailService;
    @Autowired
    UtilisateurClient utilisateurClient;
    @PostMapping("/ajouterConseil")
    public ResponseEntity<Conseil> ajouterConseil(@RequestBody ConseilDTO request) {
        Conseil nouveau = serviceIMPL.createConseil(request);
        return new ResponseEntity<>(nouveau, HttpStatus.CREATED);
    }

    @PostMapping("/addSalle")
    public Salle addSalle(@RequestBody  Salle salle) {
        return serviceIMPL.addSalle(salle);
    }

    @GetMapping("/getSalle")
    public List<Salle> getSalle() {
        return serviceIMPL.getSallle();
    }
 @GetMapping("/getConseil")
    public List<Conseil> getConseil() {
        return serviceIMPL.getConseil();
    }

    @PutMapping("/assignerUtilisateurs/{conseilId}")
    public void assignerUtilisateursAuConseil(@PathVariable("conseilId") Integer conseilId, @RequestBody List<ConseilUtilisateur> conseilUtilisateurs) {
        serviceIMPL.assignerUtilisateursAuConseil(conseilId, conseilUtilisateurs);
    }

    @PutMapping("/updateSalle")
    public Salle updateSalle(@RequestBody Salle salle) {
        return serviceIMPL.updateSalle(salle);
    }
    @PutMapping("/updateConseil")
    public Conseil updateConseil(@RequestBody Conseil conseil) {
        return serviceIMPL.updateConseil(conseil);
    }
    @DeleteMapping("/deleteConseil/{id}")
    public void deleteConseil(@PathVariable("id") Integer id) {
        serviceIMPL.deleteConseil(id);
    }

    @PutMapping("/updateEtatConseil/{conseilId}")
    public void updateEtatConseil(@PathVariable("conseilId") Integer conseilId, @RequestBody Boolean etat) {
        serviceIMPL.updateEtatConseil(conseilId, etat);
    }

    @PutMapping("/updateMessage/{conseilId}/{utilisateurId}")
    public boolean updateMessage(
            @PathVariable Integer conseilId,
            @PathVariable Long utilisateurId,
            @RequestBody Map<String, String> requestBody) {

        String message = requestBody.get("message");
        String justification = requestBody.get("justification");

        return serviceIMPL.updateMessage(conseilId, utilisateurId, message, justification);
    }


    @GetMapping("/getConseilById/{id}")
    public List<ConseilUtilisateur> getConseilUtilisateurs(@PathVariable("id") Long utilisateurId) {
        return serviceIMPL.getConseilUtilisateurs(utilisateurId);
    }

    @PutMapping("/updateDeroulement/{conseilId}")
    public void setDeroulementSeance(@PathVariable("conseilId") Integer conseilId, @RequestBody String deroulement) {
        serviceIMPL.setDeroulementSeance(conseilId, deroulement);
    }
}
