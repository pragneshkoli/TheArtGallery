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
import java.util.List;
import java.util.Date;
import java.util.Objects;

@Document(collection = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("userId")
    private ObjectId userId; // User ID
    @org.springframework.data.mongodb.core.mapping.Field("items")
    private List<CartItemModel> items;
    @org.springframework.data.mongodb.core.mapping.Field("totalPrice")
    private double totalPrice;
    @CreatedDate
    @org.springframework.data.mongodb.core.mapping.Field("createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @org.springframework.data.mongodb.core.mapping.Field("updatedAt")
    private LocalDateTime updatedAt;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CartItemModel {
    @org.springframework.data.mongodb.core.mapping.Field("paintingId")
    private ObjectId paintingId; // Painting ID
    @org.springframework.data.mongodb.core.mapping.Field("quantity")
    private int quantity;
    @org.springframework.data.mongodb.core.mapping.Field("price")
    private double price;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CustomPaintingModel {
    @org.springframework.data.mongodb.core.mapping.Field("imageUrl")
    private String imageUrl; // URL of the uploaded image
    @org.springframework.data.mongodb.core.mapping.Field("description")
    private String description;
    @org.springframework.data.mongodb.core.mapping.Field("price")
    private double price;
}
