package com.AdminMisterJerry.adminPanel.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.AdminMisterJerry.adminPanel.Model.Shoes;

public interface ShoesRepository extends JpaRepository<Shoes, Integer>{
    
    // Trova per codice (ora deve essere univoco)
    Optional<Shoes> findByCode(String code);
    
    // METODI ESISTENTI per i filtri base
    
    // Filtra solo per stagione (ordinato per ID discendente)
    List<Shoes> findBySeasonOrderByIdDesc(String season);
    
    // Filtra solo per categoria (ordinato per ID discendente)  
    List<Shoes> findByCategoryOrderByIdDesc(String category);
    
    // Filtra per entrambi stagione e categoria (ordinato per ID discendente)
    List<Shoes> findBySeasonAndCategoryOrderByIdDesc(String season, String category);
    
    // NUOVI METODI: Filtri che includono anche il colore usando query personalizzate
    
    // Filtra per colore (usando LIKE per cercare nelle stringhe di colori separate da virgole)
    @Query("SELECT s FROM Shoes s WHERE s.colors LIKE %:color% ORDER BY s.id DESC")
    List<Shoes> findByColorOrderByIdDesc(@Param("color") String color);
    
    // Filtra per stagione e colore
    @Query("SELECT s FROM Shoes s WHERE s.season = :season AND s.colors LIKE %:color% ORDER BY s.id DESC")
    List<Shoes> findBySeasonAndColorOrderByIdDesc(@Param("season") String season, @Param("color") String color);
    
    // Filtra per categoria e colore  
    @Query("SELECT s FROM Shoes s WHERE s.category = :category AND s.colors LIKE %:color% ORDER BY s.id DESC")
    List<Shoes> findByCategoryAndColorOrderByIdDesc(@Param("category") String category, @Param("color") String color);
    
    // Filtra per stagione, categoria e colore
    @Query("SELECT s FROM Shoes s WHERE s.season = :season AND s.category = :category AND s.colors LIKE %:color% ORDER BY s.id DESC")
    List<Shoes> findBySeasonAndCategoryAndColorOrderByIdDesc(@Param("season") String season, @Param("category") String category, @Param("color") String color);
    
    // METODI OPZIONALI per ricerche più flessibili (case-insensitive)
    
    // Ricerca parziale per stagione (contiene la stringa)
    List<Shoes> findBySeasonContainingIgnoreCaseOrderByIdDesc(String season);
    
    // Ricerca parziale per categoria (contiene la stringa)
    List<Shoes> findByCategoryContainingIgnoreCaseOrderByIdDesc(String category);
    
    // Ricerca parziale per colore (contiene la stringa) - usando query personalizzata
    @Query("SELECT s FROM Shoes s WHERE UPPER(s.colors) LIKE UPPER(CONCAT('%', :color, '%')) ORDER BY s.id DESC")
    List<Shoes> findByColorContainingIgnoreCaseOrderByIdDesc(@Param("color") String color);
    
    // Ricerca parziale per entrambi stagione e categoria
    List<Shoes> findBySeasonContainingIgnoreCaseAndCategoryContainingIgnoreCaseOrderByIdDesc(String season, String category);
}