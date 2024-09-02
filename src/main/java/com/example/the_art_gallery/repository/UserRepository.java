package com.example.the_art_gallery.repository;

import com.example.the_art_gallery.model.AdminModel;
import com.example.the_art_gallery.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByEmail(String email);
}
