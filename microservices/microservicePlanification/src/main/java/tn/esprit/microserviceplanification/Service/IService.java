package tn.esprit.microserviceplanification.Service;

import tn.esprit.microserviceplanification.Entity.Conseil;
import tn.esprit.microserviceplanification.Entity.ConseilDTO;
import tn.esprit.microserviceplanification.Entity.ConseilUtilisateur;
import tn.esprit.microserviceplanification.Entity.Salle;

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
}
