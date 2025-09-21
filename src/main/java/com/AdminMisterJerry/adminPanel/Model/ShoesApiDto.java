package com.AdminMisterJerry.adminPanel.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class ShoesApiDto {

    @JsonProperty("id")
    private int id;
    @JsonProperty("codice")
    private String code;
    @JsonProperty("categoria")
    private String category;
    @JsonProperty("stagione")
    private String season;
    @JsonProperty("immagine_principale")
    private String primaryImageUrl;
    @JsonProperty("immagini")
    private List<String> imageUrls;
    @JsonProperty("numero_immagini")
    private int imagesCount;
    @JsonProperty("data_creazione")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    public ShoesApiDto() {
    }

    public ShoesApiDto(int id, String code, String category, String season, String primaryImageUrl,
            List<String> imageUrls, int imagesCount, Date createdAt) {

        this.id = id;
        this.code = code;
        this.category = category;
        this.season = season;
        this.primaryImageUrl = primaryImageUrl;
        this.imageUrls = imageUrls;
        this.imagesCount = imagesCount;
        this.createdAt = createdAt;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(int imagesCount) {
        this.imagesCount = imagesCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
