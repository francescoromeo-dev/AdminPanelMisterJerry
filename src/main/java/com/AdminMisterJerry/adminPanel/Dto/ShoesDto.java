package com.AdminMisterJerry.adminPanel.Dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;

public class ShoesDto {
    
    @NotEmpty(message = "Campo obbligatorio")
    private String code;

    @NotEmpty(message="Campo obbligatorio")
    private String season;

    @NotEmpty(message = "Campo obbligatorio")
    private String category;

    private List<MultipartFile> imageFiles = new ArrayList<>(); // Cambiato per supportare multiple immagini

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
    
    public List<MultipartFile> getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(List<MultipartFile> imageFiles) {
        this.imageFiles = imageFiles != null ? imageFiles : new ArrayList<>();
    }

    //Metodo helper per la validazione
    public boolean hasImages(){
        if(imageFiles == null || imageFiles.isEmpty()) {
            return false;
        }
        // Controlla se almeno un file non è vuoto
        return imageFiles.stream().anyMatch(file -> file != null && !file.isEmpty());
    }
    
    //Metodo per ottenere solo le immagini non vuote
    public List<MultipartFile> getNonEmptyImageFiles(){
        if(imageFiles == null) {
            return new ArrayList<>();
        }
        return imageFiles.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();
    }
}