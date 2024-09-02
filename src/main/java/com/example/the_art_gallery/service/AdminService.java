package com.example.the_art_gallery.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.the_art_gallery.model.AdminModel;
import com.example.the_art_gallery.model.CategoryModel;
import com.example.the_art_gallery.model.PaintingModel;
import com.example.the_art_gallery.repository.AdminRepository;
import com.example.the_art_gallery.repository.CategoryRepository;
import com.example.the_art_gallery.repository.PaintingRepository;
import com.example.the_art_gallery.utils.Config;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class AdminService extends JWT {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PaintingRepository paintingRepository;
    Logger logger = Logger.getLogger(AdminService.class.getName());

    // for login
    public ResponseEntity<Map<String, Object>> login(Map<String, Object> payload) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            String mail = payload.get("email").toString();
            String password = payload.get("password").toString();
            AdminModel adminModel = adminRepository.findByEmail(mail).orElseThrow(() -> new Exception("Admin not found"));
            if (!(BCrypt.verifyer().verify(password.toCharArray(), adminModel.getPassword()).verified)) {
                throw new Exception("Invalid password");
            }
            String jwt = generateJWTToken(adminModel);
            if (jwt.trim().isEmpty()) {
                throw new Exception("JWT token generation failed");
            }
            map.put("status", 200);
            map.put("message", "Admin login successfully");
            map.put("token", jwt);
            map.put("data", new LinkedHashMap<>() {{
                put("_id", adminModel.getId());
                put("email", adminModel.getEmail());
                put("name", adminModel.getName());
                put("phone", adminModel.getPhone());
                put("createdAt", adminModel.getCreatedAt());
                put("updatedAt", adminModel.getUpdatedAt());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }


    // get admin profile
    public ResponseEntity<Map<String, Object>> getProfile(String id) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            AdminModel adminModel = adminRepository.findById(id).orElseThrow(() -> new Exception("Admin not found"));
            map.put("status", 200);
            map.put("message", "Admin profile fetched successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", adminModel.getId());
                put("email", adminModel.getEmail());
                put("name", adminModel.getName());
                put("phone", adminModel.getPhone());
                put("createdAt", adminModel.getCreatedAt());
                put("updatedAt", adminModel.getUpdatedAt());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // update admin profile
    public ResponseEntity<Map<String, Object>> updateProfile(Map<String, Object> payload,String id) {
        Map<String, Object> map = new LinkedHashMap<>();
        boolean isDataUpdated = false;
        try {
            AdminModel adminModel = adminRepository.findById(id).orElseThrow(() -> new Exception("Admin not found"));
            if (!adminModel.getEmail().equals(payload.get("email").toString())) {
                adminModel.setEmail(payload.get("email").toString());
                isDataUpdated = true;
            }
            if (!adminModel.getName().equals(payload.get("name").toString())) {
                adminModel.setName(payload.get("name").toString());
                isDataUpdated = true;
            }
            // getMobile() returns long value
            if (adminModel.getPhone() != Long.parseLong(payload.get("phone").toString())) {
                adminModel.setPhone(Long.parseLong(payload.get("phone").toString()));
                isDataUpdated = true;
            }
            if (isDataUpdated) {
                adminModel.setUpdatedAt(LocalDateTime.now());
                adminRepository.save(adminModel);
            } else {
                throw new Exception("No data to update");
            }
            map.put("status", 200);
            map.put("message", "Admin profile updated successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", adminModel.getId());
                put("email", adminModel.getEmail());
                put("name", adminModel.getName());
                put("phone", adminModel.getPhone());
                put("createdAt", adminModel.getCreatedAt());
                put("updatedAt", adminModel.getUpdatedAt());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }


    // change admin password
    public ResponseEntity<Map<String, Object>> changePassword(Map<String, Object> payload, String id) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            AdminModel adminModel = adminRepository.findById(id).orElseThrow(() -> new Exception("Admin not found"));
            if (!(BCrypt.verifyer().verify(payload.get("oldPassword").toString().toCharArray(), adminModel.getPassword()).verified)) {
                throw new Exception("Invalid old password");
            }
            adminModel.setPassword(BCrypt.withDefaults().hashToString(12, payload.get("newPassword").toString().toCharArray()));
            adminModel.setUpdatedAt(LocalDateTime.now());
            adminRepository.save(adminModel);
            map.put("status", 200);
            map.put("message", "Password changed successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", adminModel.getId());
                put("email", adminModel.getEmail());
                put("name", adminModel.getName());
                put("phone", adminModel.getPhone());
                put("createdAt", adminModel.getCreatedAt());
                put("updatedAt", adminModel.getUpdatedAt());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // for add category
    public ResponseEntity<Map<String, Object>> addCategory(Map<String, Object> payload) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = new CategoryModel(
                    payload.get("name").toString(),
                    payload.get("description").toString()
            );
            categoryRepository.insert(categoryModel);
            map.put("status", 200);
            map.put("message", "Category added successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", categoryModel.getId());
                put("name", categoryModel.getName());
                put("isDeleted", categoryModel.isDeleted());
                put("description", categoryModel.getDescription());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // get all categories
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            List<CategoryModel> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                return ResponseEntity.status(201).body(new LinkedHashMap<>() {{
                    put("status", 201);
                    put("message", "No categories found");
                    put("data", new LinkedHashMap<>());
                }});
            }
            map.put("status", 200);
            map.put("message", "Categories fetched successfully");
            map.put("data", categories);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // delete category
    public ResponseEntity<Map<String, Object>> deleteCategory(String id) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = categoryRepository.findById(id).orElseThrow(() -> new Exception("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new Exception("Category already deleted");
            }
            categoryModel.setDeleted(true);
            categoryRepository.save(categoryModel);
            map.put("status", 200);
            map.put("message", "Category deleted successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", categoryModel.getId());
                put("name", categoryModel.getName());
                put("isDeleted", categoryModel.isDeleted());
                put("description", categoryModel.getDescription());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }


    // update category
    public ResponseEntity<Map<String, Object>> updateCategory(Map<String, Object> payload) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = categoryRepository.findById(payload.get("id").toString()).orElseThrow(() -> new Exception("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new Exception("Category is already deleted");
            }
            categoryModel.setName(payload.get("name").toString());
            categoryModel.setDescription(payload.get("description").toString());
            categoryModel.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(categoryModel);
            map.put("status", 200);
            map.put("message", "Category updated successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", categoryModel.getId());
                put("name", categoryModel.getName());
                put("isDeleted", categoryModel.isDeleted());
                put("description", categoryModel.getDescription());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // add painting
    public ResponseEntity<Map<String, Object>> addPainting(Map<String, Object> payload) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = categoryRepository.findById(payload.get("categoryId").toString()).orElseThrow(() -> new Exception("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new Exception("Category is deleted");
            }
            PaintingModel paintingModel = new PaintingModel(
                    payload.get("name").toString(),
                    payload.get("description").toString(),
                    Double.parseDouble(payload.get("price").toString()),
                    new ObjectId(payload.get("categoryId").toString()),
                    payload.get("image").toString(),
                    Integer.parseInt(payload.get("quantity").toString()),
                    Integer.parseInt(payload.get("maximumOrderQuantity").toString())
            );
            paintingRepository.insert(paintingModel);
            map.put("status", 200);
            map.put("message", "Painting added successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", paintingModel.getId());
                put("name", paintingModel.getName());
                put("description", paintingModel.getDescription());
                put("price", paintingModel.getPrice());
                put("categoryId", paintingModel.getCategoryId());
                put("image", paintingModel.getImage());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // get all paintings
    public ResponseEntity<Map<String, Object>> getPaintings() {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            // Get all paintings
            List<PaintingModel> paintings = paintingRepository.findAll();

            // List to store painting details along with category information
            List<Map<String, Object>> paintingsWithCategoryDetails = new ArrayList<>();

            // Loop through each painting to fetch its category details
            for (int i = 0; i < paintings.size(); i++) {
                PaintingModel painting = paintings.get(i);
                CategoryModel categoryModel = categoryRepository.findById(painting.getCategoryId().toString())
                        .orElseThrow(() -> new Exception("Category not found"));

                if (!categoryModel.isDeleted()) {
                    // Prepare a map containing painting and its corresponding category details
                    Map<String, Object> paintingDetails = new LinkedHashMap<>();
                    paintingDetails.put("_id", painting.getId());
                    paintingDetails.put("name", painting.getName());
                    paintingDetails.put("description", painting.getDescription());
                    paintingDetails.put("price", painting.getPrice());
                    paintingDetails.put("quantity", painting.getQuantity());
                    paintingDetails.put("maximumOrderQuantity", painting.getMaximumOrderQuantity());
                    paintingDetails.put("image", painting.getImage());
                    paintingDetails.put("createdAt", painting.getCreatedAt());
                    paintingDetails.put("updatedAt", painting.getUpdatedAt());
                    paintingDetails.put("category", categoryModel);

                    // Add this map to the list
                    paintingsWithCategoryDetails.add(paintingDetails);
                } else {
                    // Remove the painting if the category is deleted
                    paintings.remove(i);
                    i--; // Adjust the index after removal
                }
            }

            if (paintingsWithCategoryDetails.isEmpty()) {
                return ResponseEntity.status(201).body(new LinkedHashMap<>() {{
                    put("status", 201);
                    put("count", 0);
                    put("message", "No paintings found");
                    put("data", new LinkedHashMap<>());
                }});
            }

            // Prepare the response with painting and category details
            map.put("status", 200);
            map.put("count", paintingsWithCategoryDetails.size());
            map.put("message", "Paintings fetched successfully");
            map.put("data", paintingsWithCategoryDetails);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("count", 0);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // delete painting isDeleted= true
    public ResponseEntity<Map<String, Object>> deletePainting(String id) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            // update isDeleted =  true
            PaintingModel paintingModel = paintingRepository.findById(id).orElseThrow(() -> new Exception("Painting not found"));
            if (paintingModel.isDeleted()) {
                throw new Exception("Painting already deleted");
            }
            paintingModel.setDeleted(true);
            paintingRepository.save(paintingModel);
            map.put("status", 200);
            map.put("message", "Painting deleted successfully");
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }

    // update painting
    public ResponseEntity<Map<String, Object>> updatePainting(Map<String, Object> payload) {
        Map<String, Object> map = new LinkedHashMap<>();
        boolean isDataUpdated = false;
        try {
            // update painting
            PaintingModel paintingModel = paintingRepository.findById(payload.get("id").toString()).orElseThrow(() -> new Exception("Painting not found"));
            if (paintingModel.isDeleted()) {
                throw new Exception("Painting is already deleted");
            }
            CategoryModel categoryModel = categoryRepository.findById(payload.get
                    ("categoryId").toString()).orElseThrow(() -> new Exception("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new Exception("Category is deleted");
            }
            if (!paintingModel.getName().equals(payload.get("name").toString())) {
                paintingModel.setName(payload.get("name").toString());
                isDataUpdated = true;
            }
            if (!paintingModel.getDescription().equals(payload.get("description").toString())) {
                paintingModel.setDescription(payload.get("description").toString());
                isDataUpdated = true;
            }
            if (paintingModel.getPrice() != Double.parseDouble(payload.get("price").toString())) {
                paintingModel.setPrice(Double.parseDouble(payload.get("price").toString()));
                isDataUpdated = true;
            }
            if (!paintingModel.getCategoryId().toString().equals(payload.get("categoryId").toString())) {
                paintingModel.setCategoryId(new ObjectId(payload.get("categoryId").toString()));
                isDataUpdated = true;
            }
            if (!paintingModel.getImage().equals(payload.get("image").toString())) {
                paintingModel.setImage(payload.get("image").toString());
                isDataUpdated = true;
            }
            if (paintingModel.getQuantity() != Integer.parseInt(payload.get("quantity").toString())) {
                paintingModel.setQuantity(Integer.parseInt(payload.get("quantity").toString()));
                isDataUpdated = true;
            }
            if (paintingModel.getMaximumOrderQuantity() != Integer.parseInt(payload.get("maximumOrderQuantity").toString())) {
                paintingModel.setMaximumOrderQuantity(Integer.parseInt(payload.get("maximumOrderQuantity").toString()));
                isDataUpdated = true;
            }
            if (isDataUpdated) {
                paintingModel.setUpdatedAt(LocalDateTime.now());
                paintingRepository.save(paintingModel);
            } else {
                throw new Exception("No data to update");
            }

            Map<String, Object> categoryDetails = new LinkedHashMap<>();
            categoryDetails.put("_id", categoryModel.getId());
            categoryDetails.put("name", categoryModel.getName());
            categoryDetails.put("description", categoryModel.getDescription());
            categoryDetails.put("isDeleted", categoryModel.isDeleted());

            map.put("status", 200);
            map.put("message", "Painting updated successfully");
            map.put("data", new LinkedHashMap<>() {{
                put("_id", paintingModel.getId());
                put("name", paintingModel.getName());
                put("description", paintingModel.getDescription());
                put("price", paintingModel.getPrice());
                put("category", categoryDetails);
                put("image", paintingModel.getImage());
                put("quantity", paintingModel.getQuantity());
                put("maximumOrderQuantity", paintingModel.getMaximumOrderQuantity());
            }});
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return ResponseEntity.status(200).body(map);
    }
}

class JWT {
    Logger logger = Logger.getLogger(AdminService.class.getName());

    public String generateJWTToken(AdminModel adminModel) {
        try {
            // Convert hexadecimal string to bytes
            JWSSigner signer = new MACSigner(Config.JWT_SIGN);
//            JWSSigner signer = new MACSigner(dotenv.get("JWT_SECRETE").getBytes());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("ADMIN_ACCESS") // Set the subject
                    .issuer(adminModel.getEmail()) // Set the issuer
                    .issueTime(Date.from(Instant.now())) // Set the issue time
                    .expirationTime(Date.from(Instant.now().plusSeconds((3600 * (24 * 30))))) // Set the expiration time 30 days
                    .jwtID(adminModel.getId()) // Set a unique identifier for the token
                    .build();
            // Create the signed JWT
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            // Apply the signature
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            logger.warning(e.toString());
            return "";
        }
    }
}
