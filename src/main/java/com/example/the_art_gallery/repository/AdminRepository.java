package com.example.the_art_gallery.repository;

import com.example.the_art_gallery.model.AdminModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<AdminModel, String> {
    Optional<AdminModel> findByEmail(String email);
}
