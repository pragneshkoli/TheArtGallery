package com.example.the_art_gallery.repository;

import com.example.the_art_gallery.model.CategoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<CategoryModel, String> {
}
