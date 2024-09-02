package com.example.the_art_gallery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("name")
    private String name;
    @org.springframework.data.mongodb.core.mapping.Field("password")
    private String password;
    @org.springframework.data.mongodb.core.mapping.Field("email")
    private String email;
    @org.springframework.data.mongodb.core.mapping.Field("phone")
    private long phone;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public AdminModel(String name, String password, String email, long phone) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

    }
}
