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

@Document(collection = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("userId")
    private ObjectId userId; // User ID
    @org.springframework.data.mongodb.core.mapping.Field("paintingId")
    private ObjectId paintingId; // Painting ID
    @org.springframework.data.mongodb.core.mapping.Field("rating")
    private int rating; // 1-5
    @org.springframework.data.mongodb.core.mapping.Field("comment")
    private String comment;
    @CreatedDate
    @org.springframework.data.mongodb.core.mapping.Field("createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @org.springframework.data.mongodb.core.mapping.Field("updatedAt")
    private LocalDateTime updatedAt;
}
