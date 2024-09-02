package com.example.the_art_gallery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "paintings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaintingModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("name")
    private String name;
    @org.springframework.data.mongodb.core.mapping.Field("description")
    private String description;
    @org.springframework.data.mongodb.core.mapping.Field("categoryId")
    private ObjectId categoryId;
    @org.springframework.data.mongodb.core.mapping.Field("price")
    private double price;
    @org.springframework.data.mongodb.core.mapping.Field("quantity")
    private int quantity;
    @org.springframework.data.mongodb.core.mapping.Field("maximumOrderQuantity")
    private int maximumOrderQuantity;
    @org.springframework.data.mongodb.core.mapping.Field("image")
    private String image;
    @org.springframework.data.mongodb.core.mapping.Field("isDeleted")
    private boolean isDeleted = false;
    @CreatedDate
    @org.springframework.data.mongodb.core.mapping.Field("createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @org.springframework.data.mongodb.core.mapping.Field("updatedAt")
    private LocalDateTime updatedAt;

    public PaintingModel(String name, String description, double price, ObjectId categoryId, String image,int quantity,int maximumOrderQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.image = image;
        this.quantity = quantity;
        this.maximumOrderQuantity = maximumOrderQuantity;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
