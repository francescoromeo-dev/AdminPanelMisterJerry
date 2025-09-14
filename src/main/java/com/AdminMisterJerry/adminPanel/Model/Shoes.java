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

    @Column(columnDefinition = "TEXT")
    private String colors;
    
    private String season; //Primavera/estate o autunno/inverno
    private String category;
    private Date createdAt;
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
    public String getColors(){
        return colors;
    }
    public void setColors(String colors){
        this.colors = colors; 
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
    public String getImageFileName(){
        return imageFileName;
    }
    public void setImageFileName(String imageFileName){
        this.imageFileName = imageFileName;
    }

    //Metodo per ottenere i colori come lista
    public List<String> getColorsList(){
        if(colors == null || colors.trim().isEmpty()){
            return new ArrayList<>();
        }
        return Arrays.asList(colors.split(","));
    }

    //Metodo per impostare i colori da una lista
    public void setColorsList(List<String> colorsList){
        if(colorsList == null || colorsList.isEmpty()){
            this.colors = "";
        } else {
            this.colors = String.join(",", colorsList);
        }
    }

    //Metodo per verificare se un colore è presente
    public boolean hasColor(String color){
        return getColorsList().contains(color);
    }
}
