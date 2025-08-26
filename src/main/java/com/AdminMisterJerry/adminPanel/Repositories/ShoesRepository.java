package com.AdminMisterJerry.adminPanel.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AdminMisterJerry.adminPanel.Model.Shoes;

public interface ShoesRepository extends JpaRepository<Shoes, Integer>{
    
    // Metodo esistente per trovare per codice
    public Shoes findByCode(String code);
    
    // NUOVI METODI per i filtri
    
    // Filtra solo per stagione (ordinato per ID discendente)
    List<Shoes> findBySeasonOrderByIdDesc(String season);
    
    // Filtra solo per categoria (ordinato per ID discendente)  
    List<Shoes> findByCategoryOrderByIdDesc(String category);
    
    // Filtra per entrambi stagione e categoria (ordinato per ID discendente)
    List<Shoes> findBySeasonAndCategoryOrderByIdDesc(String season, String category);
    
    // METODI OPZIONALI per ricerche più flessibili (case-insensitive)
    
    // Ricerca parziale per stagione (contiene la stringa)
    List<Shoes> findBySeasonContainingIgnoreCaseOrderByIdDesc(String season);
    
    // Ricerca parziale per categoria (contiene la stringa)
    List<Shoes> findByCategoryContainingIgnoreCaseOrderByIdDesc(String category);
    
    // Ricerca parziale per entrambi
    List<Shoes> findBySeasonContainingIgnoreCaseAndCategoryContainingIgnoreCaseOrderByIdDesc(String season, String category);
}