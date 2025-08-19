package tn.esprit.microserviceplanification.Repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.microserviceplanification.Entity.UserDTO;

@FeignClient(name = "microserviceUser", url = "http://localhost:8088")
public interface UtilisateurClient {

    @GetMapping("/auth/{id}")
    UserDTO getUtilisateurById(@PathVariable("id") Long id);
}