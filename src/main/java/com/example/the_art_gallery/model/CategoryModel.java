package com.example.the_art_gallery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("name")
    private String name;
    @org.springframework.data.mongodb.core.mapping.Field("description")
    private String description;
    @org.springframework.data.mongodb.core.mapping.Field("isDeleted")
    private boolean isDeleted = false;
    @CreatedDate
    @org.springframework.data.mongodb.core.mapping.Field("createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @org.springframework.data.mongodb.core.mapping.Field("updatedAt")
    private LocalDateTime updatedAt;

    public CategoryModel(String name, String description) {
        this.name = name;
        this.description = description;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
