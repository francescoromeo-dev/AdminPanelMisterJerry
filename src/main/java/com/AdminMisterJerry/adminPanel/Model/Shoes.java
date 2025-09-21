package com.AdminMisterJerry.adminPanel.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity
@Table(name="shoes")
public class Shoes {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    private String code;

    // Invece di salvare nomi file, ora salviamo gli ID delle immagini nel database
    @Column(columnDefinition = "TEXT")
    private String imageIds; // IDs delle immagini salvate nel database, separati da virgola
    
    private String season; // Primavera/estate o autunno/inverno
    private String category;
    private Date createdAt;
    
    // Manteniamo per backward compatibility, ma sarà deprecato
    @Deprecated
    private String imageFileName;

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getImageIds() {
        return imageIds;
    }
    
    public void setImageIds(String imageIds) {
        this.imageIds = imageIds;
    }
    
    public String getSeason() {
        return season;
    }
    
    public void setSeason(String season) {
        this.season = season;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    // Deprecato - mantenuto per backward compatibility
    @Deprecated
    public String getImageFileName() {
        return imageFileName;
    }
    
    @Deprecated
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    // Metodo per ottenere gli ID delle immagini come lista di Long
    public List<Long> getImageIdsList() {
        if (imageIds == null || imageIds.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> ids = new ArrayList<>();
        String[] idStrings = imageIds.split(",");
        
        for (String idString : idStrings) {
            try {
                Long id = Long.parseLong(idString.trim());
                ids.add(id);
            } catch (NumberFormatException e) {
                // Ignora ID non validi
                System.err.println("ID immagine non valido: " + idString);
            }
        }
        
        return ids;
    }

    // Metodo per impostare gli ID delle immagini da una lista di Long
    public void setImageIdsList(List<Long> imageIdsList) {
        if (imageIdsList == null || imageIdsList.isEmpty()) {
            this.imageIds = "";
        } else {
            List<String> idStrings = new ArrayList<>();
            for (Long id : imageIdsList) {
                if (id != null) {
                    idStrings.add(id.toString());
                }
            }
            this.imageIds = String.join(",", idStrings);
        }
    }

    // Metodo per verificare se ha immagini
    public boolean hasImages() {
        return !getImageIdsList().isEmpty();
    }
    
    // Metodo per ottenere l'ID della prima immagine (immagine principale)
    public Long getPrimaryImageId() {
        List<Long> imageIds = getImageIdsList();
        return imageIds.isEmpty() ? null : imageIds.get(0);
    }
    
    // Metodo per ottenere il numero di immagini
    public int getImagesCount() {
        return getImageIdsList().size();
    }
    
    // Metodo helper per aggiungere un ID immagine
    public void addImageId(Long imageId) {
        if (imageId == null) return;
        
        List<Long> currentIds = getImageIdsList();
        currentIds.add(imageId);
        setImageIdsList(currentIds);
    }
    
    // Metodo helper per rimuovere un ID immagine
    public void removeImageId(Long imageId) {
        if (imageId == null) return;
        
        List<Long> currentIds = getImageIdsList();
        currentIds.removeIf(id -> id.equals(imageId));
        setImageIdsList(currentIds);
    }
    
    // Metodi di compatibilità per backward compatibility con il codice esistente
    @Deprecated
    public List<String> getImageFileNamesList() {
        // Per compatibilità, restituisce gli ID come stringhe
        List<Long> ids = getImageIdsList();
        List<String> stringIds = new ArrayList<>();
        for (Long id : ids) {
            stringIds.add(id.toString());
        }
        return stringIds;
    }
    
    @Deprecated
    public void setImageFileNamesList(List<String> imageFileNamesList) {
        // Per compatibilità, prova a convertire le stringhe in ID
        if (imageFileNamesList == null || imageFileNamesList.isEmpty()) {
            this.imageIds = "";
            return;
        }
        
        List<Long> ids = new ArrayList<>();
        for (String idString : imageFileNamesList) {
            try {
                Long id = Long.parseLong(idString);
                ids.add(id);
            } catch (NumberFormatException e) {
                // Se non è un numero, potrebbe essere un nome file legacy - ignora o gestisci
                System.err.println("Tentativo di conversione legacy fallito per: " + idString);
            }
        }
        setImageIdsList(ids);
    }
    
    @Deprecated
    public String getPrimaryImageFileName() {
        Long primaryId = getPrimaryImageId();
        return primaryId != null ? primaryId.toString() : null;
    }
}