package com.AdminMisterJerry.adminPanel.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;

public class ShoesDto {
    
    @NotEmpty(message = "Campo obbligatorio")
    private String code;

    @NotEmpty(message = "Seleziona almeno un colore")
    private List <String> colors = new ArrayList<>();

    @NotEmpty(message="Campo obbligatorio")
    private String season;

    @NotEmpty(message = "Campo obbligatorio")
    private String category;

    private MultipartFile imageFile;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getColors(){
        return colors;
    }

    public void setColors(List<String> colors){
        this.colors = colors != null ? colors : new ArrayList<>();
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
    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    //Metodo help per la validazione
    public boolean hasColors(){
        return colors != null && !colors.isEmpty();
    }
}
