package com.AdminMisterJerry.adminPanel.Model;

import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>{
    //Metodo per cercare gli utenti tramite username
    Optional<UserModel> findByUsername(String username);
}
