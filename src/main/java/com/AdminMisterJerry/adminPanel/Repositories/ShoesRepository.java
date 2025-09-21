package com.AdminMisterJerry.adminPanel.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AdminMisterJerry.adminPanel.Model.Shoes;

public interface ShoesRepository extends JpaRepository<Shoes, Integer>{
    
    // Trova per codice (ora deve essere univoco)
    Optional<Shoes> findByCode(String code);
    
    // Filtra solo per stagione (ordinato per ID discendente)
    List<Shoes> findBySeasonOrderByIdDesc(String season);
    
    // Filtra solo per categoria (ordinato per ID discendente)  
    List<Shoes> findByCategoryOrderByIdDesc(String category);
    
    // Filtra per entrambi stagione e categoria (ordinato per ID discendente)
    List<Shoes> findBySeasonAndCategoryOrderByIdDesc(String season, String category);
    
    // Rimosse tutte le query relative ai colori dato che non sono più necessarie
    
    // METODI OPZIONALI per ricerche più flessibili (case-insensitive)
    
    // Ricerca parziale per stagione (contiene la stringa)
    List<Shoes> findBySeasonContainingIgnoreCaseOrderByIdDesc(String season);
    
    // Ricerca parziale per categoria (contiene la stringa)
    List<Shoes> findByCategoryContainingIgnoreCaseOrderByIdDesc(String category);
    
    // Ricerca parziale per entrambi stagione e categoria
    List<Shoes> findBySeasonContainingIgnoreCaseAndCategoryContainingIgnoreCaseOrderByIdDesc(String season, String category);
}