package com.AdminMisterJerry.adminPanel.Repositories;

import com.AdminMisterJerry.adminPanel.Model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    
    // Trova un'immagine per nome file
    Optional<ImageEntity> findByFileName(String fileName);
    
    // Trova tutte le immagini ordinate per data di creazione (le più recenti prima)
    List<ImageEntity> findAllByOrderByCreatedAtDesc();
    
    // Trova immagini per content type
    List<ImageEntity> findByContentTypeContainingIgnoreCase(String contentType);
    
    // Trova immagini create dopo una certa data
    @Query("SELECT i FROM ImageEntity i WHERE i.createdAt >= :date ORDER BY i.createdAt DESC")
    List<ImageEntity> findImagesCreatedAfter(@Param("date") java.util.Date date);
    
    // Conta il numero totale di immagini
    long count();
    
    // Calcola la dimensione totale di tutte le immagini
    @Query("SELECT SUM(i.fileSize) FROM ImageEntity i")
    Long getTotalImagesSize();
    
    // Elimina immagini per nome file
    void deleteByFileName(String fileName);
    
    // Verifica se esiste un'immagine con un certo nome
    boolean existsByFileName(String fileName);
}
