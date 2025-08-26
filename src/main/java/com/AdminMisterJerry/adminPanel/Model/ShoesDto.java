package com.AdminMisterJerry.adminPanel.Model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;

public class ShoesDto {
    
    @NotEmpty(message = "Campo obbligatorio")
    private String code;

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
}
