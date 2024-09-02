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
import java.util.List;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("firstName")
    private String firstName;
    @org.springframework.data.mongodb.core.mapping.Field("lastName")
    private String lastName;
    @org.springframework.data.mongodb.core.mapping.Field("email")
    private String email;
    @org.springframework.data.mongodb.core.mapping.Field("mobile")
    private long mobile;
    @org.springframework.data.mongodb.core.mapping.Field("password")
    private String password;
    @org.springframework.data.mongodb.core.mapping.Field("address")
    private AddressModel address;
    @org.springframework.data.mongodb.core.mapping.Field("orders")
    private List<ObjectId> orders; // List of Order IDs
    @org.springframework.data.mongodb.core.mapping.Field("likedProducts")
    private List<ObjectId> likedProducts; // List of Product IDs
    @org.springframework.data.mongodb.core.mapping.Field("isDeleted")
    private boolean isDeleted = false;
    @CreatedDate
    @org.springframework.data.mongodb.core.mapping.Field("createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @org.springframework.data.mongodb.core.mapping.Field("updatedAt")
    private LocalDateTime updatedAt;

    public UserModel(String firstName, String lastName, String email, long mobile, String password,String building, String street, String city, String state, String postalCode, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        address = new AddressModel(building, street, city, state, postalCode, country);
        orders = List.of();
        likedProducts = List.of();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AddressModel {
    @org.springframework.data.mongodb.core.mapping.Field("building")
    private String building;
    @org.springframework.data.mongodb.core.mapping.Field("street")
    private String street;
    @org.springframework.data.mongodb.core.mapping.Field("city")
    private String city;
    @org.springframework.data.mongodb.core.mapping.Field("state")
    private String state;
    @org.springframework.data.mongodb.core.mapping.Field("postalCode")
    private String postalCode;
    @org.springframework.data.mongodb.core.mapping.Field("country")
    private String country;
}
