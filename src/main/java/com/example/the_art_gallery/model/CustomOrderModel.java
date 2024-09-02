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

@Document(collection = "custom_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomOrderModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("userId")
    private ObjectId userId; // User ID
    @org.springframework.data.mongodb.core.mapping.Field("imageUrl")
    private String imageUrl;
    @org.springframework.data.mongodb.core.mapping.Field("description")
    private String description;
    @org.springframework.data.mongodb.core.mapping.Field("price")
    private double price;
    @org.springframework.data.mongodb.core.mapping.Field("status")
    private String status; // "pending", "in progress", "completed", "canceled"
    @org.springframework.data.mongodb.core.mapping.Field("orderDate")
    private Date orderDate;
    @org.springframework.data.mongodb.core.mapping.Field("deliveryDate")
    private Date deliveryDate;
    @org.springframework.data.mongodb.core.mapping.Field("isDeleted")
    private boolean isDeleted = false;
    @CreatedDate
    @org.springframework.data.mongodb.core.mapping.Field("createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @org.springframework.data.mongodb.core.mapping.Field("updatedAt")
    private LocalDateTime updatedAt;
}
