package com.AdminMisterJerry.adminPanel.Model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "images")
public class ImageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] imageData;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;
    
    public ImageEntity() {
        this.createdAt = new Date();
    }
    
    public ImageEntity(String fileName, String contentType, byte[] imageData) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.imageData = imageData;
        this.fileSize = (long) imageData.length;
        this.createdAt = new Date();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public byte[] getImageData() {
        return imageData;
    }
    
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
        if (imageData != null) {
            this.fileSize = (long) imageData.length;
        }
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}