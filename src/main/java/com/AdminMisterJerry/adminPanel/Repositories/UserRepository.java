package com.AdminMisterJerry.adminPanel.Repositories;

import org.springframework.stereotype.Repository;

import com.AdminMisterJerry.adminPanel.Model.UserModel;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>{
    //Metodo per cercare gli utenti tramite username
    Optional<UserModel> findByUsername(String username);
}
