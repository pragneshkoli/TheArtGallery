package com.example.the_art_gallery.repository;

import com.example.the_art_gallery.model.PaintingModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends MongoRepository<PaintingModel, String> {
}
