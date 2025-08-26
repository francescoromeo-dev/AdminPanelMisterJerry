package com.AdminMisterJerry.adminPanel.Model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceLogin implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Cercando utente: " + username);

        Optional<UserModel> user = repository.findByUsername(username);

        if (user.isPresent()) {
            var userObj = user.get();
            System.out.println("Utente trovato: " + userObj.getUsername());
            System.out.println("Password dal DB: " + userObj.getPassword());

            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities("USER")
                    .build();
        } else {
            System.out.println("Utente non trovato!");
            throw new UsernameNotFoundException(username);
        }
    }

}
