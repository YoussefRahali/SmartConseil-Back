package tn.esprit.microserviceplanification.Service;

<<<<<<< HEAD
import tn.esprit.microserviceplanification.Entity.*;
=======
import tn.esprit.microserviceplanification.Entity.Conseil;
import tn.esprit.microserviceplanification.Entity.ConseilDTO;
import tn.esprit.microserviceplanification.Entity.ConseilUtilisateur;
import tn.esprit.microserviceplanification.Entity.Salle;
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b

import java.util.List;

public interface IService {

 public Salle addSalle(Salle salle);
 public List<Salle> getSallle();


 public List<Conseil> getConseil();
 public Conseil createConseil(ConseilDTO request);
public List<Conseil> getConseils();
 public void assignerUtilisateursAuConseil(Integer conseilId,  List<ConseilUtilisateur> conseilUtilisateurs);

 public Salle  updateSalle(Salle salle);
 public Conseil  updateConseil(Conseil conseil);

public void deleteConseil(Integer conseilId);

public void updateEtatConseil(Integer conseilId, Boolean etat);
 public boolean updateMessage(Integer conseilId, Long utilisateurId, String newMessage,String justifictaion) ;
 List<ConseilUtilisateur>getConseilUtilisateurs(Long utilisateurId);
 public void setDeroulementSeance(Integer conseilId, String deroulement);
<<<<<<< HEAD

 // Méthodes pour les options et classes
 public List<Option> getAllOptions();
 public List<Classe> getClassesByOption(Integer optionId);
 public Option addOption(Option option);
 public Classe addClasse(Classe classe);

 // Méthodes DTO pour éviter les problèmes de sérialisation
 public List<OptionDTO> getAllOptionsDTO();
 public List<ClasseDTO> getClassesByOptionDTO(Integer optionId);
 public OptionDTO addOptionDTO(OptionDTO optionDTO);
 public ClasseDTO addClasseDTO(ClasseDTO classeDTO);
=======
>>>>>>> 0139d5b706f6c8c817326e3af968b75daf29528b
}
