package com.example.the_art_gallery.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.the_art_gallery.exeption.CustomException;
import com.example.the_art_gallery.logs.Logs;
import com.example.the_art_gallery.model.AdminModel;
import com.example.the_art_gallery.model.CategoryModel;
import com.example.the_art_gallery.model.PaintingModel;
import com.example.the_art_gallery.repository.AdminRepository;
import com.example.the_art_gallery.repository.CategoryRepository;
import com.example.the_art_gallery.repository.PaintingRepository;
import com.example.the_art_gallery.response.Response;
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
    static Logs logs = new Logs(AdminService.class.getName());
    Response response = new Response();


    // for login
    public ResponseEntity<Map<String, Object>> login(Map<String, Object> payload) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            String mail = payload.get("email").toString();
            String password = payload.get("password").toString();
            AdminModel adminModel = adminRepository.findByEmail(mail).orElseThrow(() -> new CustomException("Admin not found"));
            if (!(BCrypt.verifyer().verify(password.toCharArray(), adminModel.getPassword()).verified)) {
                throw new CustomException("Invalid password");
            }
            String jwt = generateJWTToken(adminModel);
            if (jwt.trim().isEmpty()) {
                throw new CustomException("JWT token generation failed");
            }
            res.put("_id", adminModel.getId());
            res.put("email", adminModel.getEmail());
            res.put("name", adminModel.getName());
            res.put("phone", adminModel.getPhone());
            res.put("createdAt", adminModel.getCreatedAt());
            res.put("updatedAt", adminModel.getUpdatedAt());
            return response.sendSuccess(200, "Admin login successfully", jwt, res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "login");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }


    // get admin profile
    public ResponseEntity<Map<String, Object>> getProfile(String id) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            AdminModel adminModel = adminRepository.findById(id).orElseThrow(() -> new CustomException("Admin not found"));

            res.put("_id", adminModel.getId());
            res.put("email", adminModel.getEmail());
            res.put("name", adminModel.getName());
            res.put("phone", adminModel.getPhone());
            res.put("createdAt", adminModel.getCreatedAt());
            res.put("updatedAt", adminModel.getUpdatedAt());

            return response.sendSuccess(200, "Admin profile fetched successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "getProfile");
            return response.sendBadRequest(400, "Something went wrong");
        }

    }

    // update admin profile
    public ResponseEntity<Map<String, Object>> updateProfile(Map<String, Object> payload, String id) {
        Map<String, Object> res = new LinkedHashMap<>();
        boolean isDataUpdated = false;
        try {
            AdminModel adminModel = adminRepository.findById(id).orElseThrow(() -> new CustomException("Admin not found"));
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
                throw new CustomException("No data to update");
            }
            res.put("_id", adminModel.getId());
            res.put("email", adminModel.getEmail());
            res.put("name", adminModel.getName());
            res.put("phone", adminModel.getPhone());
            res.put("createdAt", adminModel.getCreatedAt());
            res.put("updatedAt", adminModel.getUpdatedAt());
            return response.sendSuccess(200, "Admin profile updated successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "updateProfile");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }


    // change admin password
    public ResponseEntity<Map<String, Object>> changePassword(Map<String, Object> payload, String id) {
        try {
            AdminModel adminModel = adminRepository.findById(id).orElseThrow(() -> new CustomException("Admin not found"));
            if (!(BCrypt.verifyer().verify(payload.get("oldPassword").toString().toCharArray(), adminModel.getPassword()).verified)) {
                throw new CustomException("Invalid old password");
            }
            adminModel.setPassword(BCrypt.withDefaults().hashToString(12, payload.get("newPassword").toString().toCharArray()));
            adminModel.setUpdatedAt(LocalDateTime.now());
            adminRepository.save(adminModel);
            return response.sendSuccess(200, "Password changed successfully", new LinkedHashMap<>());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "changePassword");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }

    // for add category
    public ResponseEntity<Map<String, Object>> addCategory(Map<String, Object> payload) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = new CategoryModel(
                    payload.get("name").toString(),
                    payload.get("description").toString()
            );
            categoryRepository.insert(categoryModel);
            res.put("_id", categoryModel.getId());
            res.put("name", categoryModel.getName());
            res.put("isDeleted", categoryModel.isDeleted());
            res.put("description", categoryModel.getDescription());
            return response.sendSuccess(200, "Category added successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "addCategory");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }

    // get all categories
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            List<CategoryModel> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                return response.sendSuccess(201, "No categories found", new LinkedHashMap<>());
            }
            for (CategoryModel category : categories) {
                if (!category.isDeleted()) {
                    Map<String, Object> categoryDetails = new LinkedHashMap<>();
                    categoryDetails.put("_id", category.getId());
                    categoryDetails.put("name", category.getName());
                    categoryDetails.put("isDeleted", category.isDeleted());
                    categoryDetails.put("description", category.getDescription());
                    res.add(categoryDetails);
                }
            }
            return response.sendSuccess(200, "Categories fetched successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "getCategories");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }

    // delete category
    public ResponseEntity<Map<String, Object>> deleteCategory(String id) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = categoryRepository.findById(id).orElseThrow(() -> new CustomException("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new CustomException("Category already deleted");
            }
            categoryModel.setDeleted(true);
            categoryRepository.save(categoryModel);
            res.put("_id", categoryModel.getId());
            res.put("name", categoryModel.getName());
            res.put("isDeleted", categoryModel.isDeleted());
            res.put("description", categoryModel.getDescription());
            return response.sendSuccess(200, "Category deleted successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "deleteCategory");
            return response.sendBadRequest(400, e.getMessage());
        }
    }


    // update category
    public ResponseEntity<Map<String, Object>> updateCategory(Map<String, Object> payload) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = categoryRepository.findById(payload.get("id").toString()).orElseThrow(() -> new CustomException("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new CustomException("Category is already deleted");
            }
            categoryModel.setName(payload.get("name").toString());
            categoryModel.setDescription(payload.get("description").toString());
            categoryModel.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(categoryModel);
            res.put("_id", categoryModel.getId());
            res.put("name", categoryModel.getName());
            res.put("isDeleted", categoryModel.isDeleted());
            res.put("description", categoryModel.getDescription());
            return response.sendSuccess(200, "Category updated successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "updateCategory");
            return response.sendBadRequest(400, e.getMessage());
        }
    }

    // add painting
    public ResponseEntity<Map<String, Object>> addPainting(Map<String, Object> payload) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            CategoryModel categoryModel = categoryRepository.findById(payload.get("categoryId").toString()).orElseThrow(() -> new CustomException("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new CustomException("Category is deleted");
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

            res.put("_id", paintingModel.getId());
            res.put("name", paintingModel.getName());
            res.put("description", paintingModel.getDescription());
            res.put("price", paintingModel.getPrice());
            res.put("categoryId", paintingModel.getCategoryId());
            res.put("image", paintingModel.getImage());
            return response.sendSuccess(200, "Painting added successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "addPainting");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }

    // get all paintings
    public ResponseEntity<Map<String, Object>> getPaintings() {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            // Get all paintings
            List<PaintingModel> paintings = paintingRepository.findAll();

            // List to store painting details along with category information
            List<Map<String, Object>> paintingsWithCategoryDetails = new ArrayList<>();

            // Loop through each painting to fetch its category details
            for (int i = 0; i < paintings.size(); i++) {
                PaintingModel painting = paintings.get(i);
                CategoryModel categoryModel = categoryRepository.findById(painting.getCategoryId().toString())
                        .orElseThrow(() -> new CustomException("Category not found"));

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
                return response.sendSuccess(201, "No paintings found", new LinkedHashMap<>());
            }

            // Prepare the response with painting and category details
            return response.sendSuccess(200, "Paintings fetched successfully", paintingsWithCategoryDetails);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "getPaintings");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }

    // delete painting isDeleted= true
    public ResponseEntity<Map<String, Object>> deletePainting(String id) {
        try {
            // update isDeleted =  true
            PaintingModel paintingModel = paintingRepository.findById(id).orElseThrow(() -> new CustomException("Painting not found"));
            if (paintingModel.isDeleted()) {
                throw new CustomException("Painting already deleted");
            }
            paintingModel.setDeleted(true);
            paintingRepository.save(paintingModel);
            return response.sendSuccess(200, "Painting deleted successfully", new LinkedHashMap<>());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "deletePainting");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }

    // update painting
    public ResponseEntity<Map<String, Object>> updatePainting(Map<String, Object> payload) {
        Map<String, Object> res = new LinkedHashMap<>();
        boolean isDataUpdated = false;
        try {
            // update painting
            PaintingModel paintingModel = paintingRepository.findById(payload.get("id").toString()).orElseThrow(() -> new CustomException("Painting not found"));
            if (paintingModel.isDeleted()) {
                throw new CustomException("Painting is already deleted");
            }
            CategoryModel categoryModel = categoryRepository.findById(payload.get
                    ("categoryId").toString()).orElseThrow(() -> new CustomException("Category not found"));
            if (categoryModel.isDeleted()) {
                throw new CustomException("Category is deleted");
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
                throw new CustomException("No data to update");
            }

            Map<String, Object> categoryDetails = new LinkedHashMap<>();
            categoryDetails.put("_id", categoryModel.getId());
            categoryDetails.put("name", categoryModel.getName());
            categoryDetails.put("description", categoryModel.getDescription());
            categoryDetails.put("isDeleted", categoryModel.isDeleted());


            res.put("_id", paintingModel.getId());
            res.put("name", paintingModel.getName());
            res.put("description", paintingModel.getDescription());
            res.put("price", paintingModel.getPrice());
            res.put("category", categoryDetails);
            res.put("image", paintingModel.getImage());
            res.put("quantity", paintingModel.getQuantity());
            res.put("maximumOrderQuantity", paintingModel.getMaximumOrderQuantity());
            return response.sendSuccess(200, "Painting updated successfully", res);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "updatePainting");
            return response.sendBadRequest(400, "Something went wrong");
        }
    }
}

class JWT {
    Logger logger = Logger.getLogger(JWT.class.getName());
    static Logs logs = new Logs(JWT.class.getName());

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
            logs.log(e.toString(), "generateJWTToken");

            return "";
        }
    }
}
