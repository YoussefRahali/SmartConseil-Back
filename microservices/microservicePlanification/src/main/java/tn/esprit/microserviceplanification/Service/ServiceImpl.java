package tn.esprit.microserviceplanification.Service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microserviceplanification.Entity.*;
<<<<<<< HEAD
import tn.esprit.microserviceplanification.Repository.*;
=======
import tn.esprit.microserviceplanification.Repository.ConseilRepo;
import tn.esprit.microserviceplanification.Repository.ConseilUtilisateurRepo;
import tn.esprit.microserviceplanification.Repository.SalleRepo;
import tn.esprit.microserviceplanification.Repository.UtilisateurClient;
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b

import java.util.List;
import java.util.Optional;
import java.util.Random;
<<<<<<< HEAD
import java.util.stream.Collectors;
=======
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b

@AllArgsConstructor
@Service
public class ServiceImpl implements  IService {

    ConseilRepo conseilRepo;
    SalleRepo salleRepo;
    ConseilUtilisateurRepo conseilUtilisateurRepo;
<<<<<<< HEAD
    OptionRepo optionRepo;
    ClasseRepo classeRepo;
=======
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b


    @Autowired
    private UtilisateurClient utilisateurClient;
    @Override
    public Salle addSalle(Salle salle) {
        return salleRepo.save(salle);
    }

    @Override
    public List<Salle> getSallle() {
        return salleRepo.findAll();
    }

    @Override
    public Conseil createConseil(ConseilDTO request) {
        // 1️⃣ Vérifier que l’utilisateur existe via Feign
        Long PesidentId = utilisateurClient.getUtilisateurById(request.getPresidentId()).getId();
        Long RaporteurId = utilisateurClient.getUtilisateurById(request.getRaporteurId()).getId();

        // 2️⃣ Créer et sauvegarder le conseil
        Conseil conseil = new Conseil();
        conseil.setDate(request.getDate());
        conseil.setDescription(request.getDescription());
        conseil.setDuree(request.getDuree());
<<<<<<< HEAD
        // Récupérer l'option
        Option option = optionRepo.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option non trouvée avec id: " + request.getOptionId()));
        conseil.setOption(option);

        // Récupérer les classes
        List<Classe> classes = classeRepo.findAllById(request.getClasseIds());
        if (classes.size() != request.getClasseIds().size()) {
            throw new RuntimeException("Une ou plusieurs classes non trouvées");
        }
        conseil.setClasses(classes);
=======
        conseil.setClasses(request.getClasses());
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
        conseil.setSalle(salleRepo.findById(request.getSalleId()).get());
        conseil.setHeure(request.getHeure());
        conseil.setPresidentId(PesidentId);
        conseil.setRaporteurId(RaporteurId);
        conseil.setDeroulement(request.getDeroulement());


conseil.setToken(generateRandomToken(8));

        return conseilRepo.save(conseil);
    }



    public String generateRandomToken(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }

        return token.toString();
    }

    @Override
    public List<Conseil> getConseils() {
        return conseilRepo.findAll();
    }

    @Override
    public void assignerUtilisateursAuConseil(Integer conseilId, List<ConseilUtilisateur> nouveauxParticipants) {
        Conseil conseil = conseilRepo.findById(conseilId)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé"));


        conseil.getParticipants().clear();
        for (ConseilUtilisateur participant : nouveauxParticipants) {
            participant.setConseil(conseil);
            conseil.getParticipants().add(participant);
        }

         conseilRepo.save(conseil);
    }


    @Override
    public Salle updateSalle(Salle salle) {
        Optional<Salle> existingSalleOpt = salleRepo.findById(salle.getId());

        if (existingSalleOpt.isPresent()) {
            Salle existingType = existingSalleOpt.get();


            existingType.setCapacite(salle.getCapacite());
            existingType.setNomSalle(salle.getNomSalle());
            existingType.setEtage(salle.getEtage());
            existingType.setEquipement(salle.getEquipement());

            existingType.setDescription(salle.getDescription());
            return salleRepo.save(existingType);
        }

        return null;

    }

    @Override
    public Conseil updateConseil(Conseil conseil) {
        Optional<Conseil> existingConseilOpt = conseilRepo.findById(conseil.getId());

        if (existingConseilOpt.isPresent()) {
            Conseil existingType = existingConseilOpt.get();


            existingType.setDate(conseil.getDate());
            existingType.setDuree(conseil.getDuree());
            existingType.setClasses(conseil.getClasses());
            existingType.setSalle(salleRepo.findById(conseil.getSalle().getId()).get());
            existingType.setHeure(conseil.getHeure());
            existingType.setPresidentId(conseil.getPresidentId());
            existingType.setRaporteurId(conseil.getRaporteurId());

            existingType.setDescription(conseil.getDescription());
            return conseilRepo.save(existingType);
        }

        return null;
    }





    @Override
    public void deleteConseil(Integer conseilId) {
        conseilRepo.deleteById(conseilId);
    }

    @Override
    public void updateEtatConseil(Integer conseilId, Boolean etat) {
        Conseil conseil = conseilRepo.findById(conseilId)
                .orElseThrow(() -> new RuntimeException("Conseil introuvable avec id : " + conseilId));
        conseil.setEtat(etat);
        conseilRepo.save(conseil);
    }

    @Override
    public boolean updateMessage(Integer conseilId, Long utilisateurId, String newMessage, String justifictaion) {
        Optional<ConseilUtilisateur> optionalCU = conseilUtilisateurRepo.findByConseilIdAndUtilisateurId(conseilId, utilisateurId);
        if (optionalCU.isPresent()) {
            ConseilUtilisateur cu = optionalCU.get();
            cu.setMessage(newMessage);
            cu.setJustifictaion(justifictaion);
            conseilUtilisateurRepo.save(cu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ConseilUtilisateur> getConseilUtilisateurs(Long utilisateurId) {
        return conseilUtilisateurRepo.findByUtilisateurId(utilisateurId);
    }

    @Override
    public void setDeroulementSeance(Integer conseilId, String deroulement) {
        Conseil conseil=conseilRepo.findById(conseilId).get();
        conseil.setDeroulement(deroulement);
        conseilRepo.save(conseil);

    }

    @Override
    public List<Conseil> getConseil() {
        return conseilRepo.findAll();
    }
<<<<<<< HEAD

    @Override
    public List<Option> getAllOptions() {
        return optionRepo.findAll();
    }

    @Override
    public List<Classe> getClassesByOption(Integer optionId) {
        return classeRepo.findByOptionId(optionId);
    }

    @Override
    public Option addOption(Option option) {
        return optionRepo.save(option);
    }

    @Override
    public Classe addClasse(Classe classe) {
        return classeRepo.save(classe);
    }

    // Méthodes DTO pour éviter les problèmes de sérialisation
    @Override
    public List<OptionDTO> getAllOptionsDTO() {
        List<Option> options = optionRepo.findAll();
        return options.stream()
                .map(this::convertToOptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClasseDTO> getClassesByOptionDTO(Integer optionId) {
        List<Classe> classes = classeRepo.findByOptionId(optionId);
        return classes.stream()
                .map(this::convertToClasseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OptionDTO addOptionDTO(OptionDTO optionDTO) {
        Option option = new Option();
        option.setNom(optionDTO.getNom());
        option.setDescription(optionDTO.getDescription());

        Option savedOption = optionRepo.save(option);
        return convertToOptionDTO(savedOption);
    }

    @Override
    public ClasseDTO addClasseDTO(ClasseDTO classeDTO) {
        Classe classe = new Classe();
        classe.setNom(classeDTO.getNom());
        classe.setDescription(classeDTO.getDescription());

        Option option = optionRepo.findById(classeDTO.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option non trouvée avec id: " + classeDTO.getOptionId()));
        classe.setOption(option);

        Classe savedClasse = classeRepo.save(classe);
        return convertToClasseDTO(savedClasse);
    }

    // Méthodes de conversion
    private OptionDTO convertToOptionDTO(Option option) {
        OptionDTO dto = new OptionDTO();
        dto.setId(option.getId());
        dto.setNom(option.getNom());
        dto.setDescription(option.getDescription());
        return dto;
    }

    private ClasseDTO convertToClasseDTO(Classe classe) {
        ClasseDTO dto = new ClasseDTO();
        dto.setId(classe.getId());
        dto.setNom(classe.getNom());
        dto.setDescription(classe.getDescription());
        dto.setOptionId(classe.getOption().getId());
        dto.setOptionNom(classe.getOption().getNom());
        return dto;
    }
=======
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
}
