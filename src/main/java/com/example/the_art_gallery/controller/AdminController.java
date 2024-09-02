package com.example.the_art_gallery.controller;

import com.example.the_art_gallery.model.AdminModel;
import com.example.the_art_gallery.repository.AdminRepository;
import com.example.the_art_gallery.routes.AdminRoutes;
import com.example.the_art_gallery.service.AdminService;
import com.example.the_art_gallery.utils.Config;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.example.the_art_gallery.routes.AdminRoutes.ADMIN;

@RestController
@RequestMapping(ADMIN)
public class AdminController extends JWT {
    @Autowired
    private AdminService adminService;

    Logger logger = Logger.getLogger(AdminController.class.getName());


    // login
    @PostMapping(ADMIN_LOGIN)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!payload.containsKey("email") || !payload.containsKey("password")) {
                throw new Exception("Required fields are missing");
            }
            if (payload.get("email").toString().isEmpty()) {
                throw new Exception("Email is required");
            }
            if (payload.get("password").toString().isEmpty()) {
                throw new Exception("Password is required");
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
        return adminService.login(payload);
    }

    // get profile
    @GetMapping(ADMIN_PROFILE)
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            return adminService.getProfile(adminModel.getId());
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    // update profile
    @PutMapping(ADMIN_UPDATE_PROFILE)
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new Exception("Name is missing");
            }
            if (!payload.containsKey("email") || payload.get("email").toString().isEmpty()) {
                throw new Exception("Email is missing");
            }
            if (!payload.containsKey("phone") || payload.get("phone").toString().isEmpty()) {
                throw new Exception("Phone is missing");
            }
            if(payload.get("phone").toString().length()!=10) {
                throw new Exception("Phone number should be 10 digits");
            }
            return adminService.updateProfile(payload, adminModel.getId());
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    // change admin password
    @PutMapping(ADMIN_CHANGE_PASSWORD)
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("oldPassword") || payload.get("oldPassword").toString().isEmpty()) {
                throw new Exception("Old password is missing");
            }
            if (!payload.containsKey("newPassword") || payload.get("newPassword").toString().isEmpty()) {
                throw new Exception("New password is missing");
            }
            if (payload.get("newPassword").toString().length() < 5) {
                throw new Exception("Password should be at least 5 characters");
            }
            if(payload.get("newPassword").toString().equals(payload.get("oldPassword").toString())) {
                throw new Exception("New password should not be same as old password");
            }
            return adminService.changePassword(payload, adminModel.getId());
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }


    // add category
    @PostMapping(ADD_CATEGORY)
    public ResponseEntity<Map<String, Object>> addCategory(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new Exception("Category name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new Exception("Category description is missing");
            }

            return adminService.addCategory(payload);
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    // get all categories
    @GetMapping(GET_CATEGORY)
    public ResponseEntity<Map<String, Object>> getCategories(@RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            return adminService.getCategories();
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<Map<String, Object>> deleteCategory(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new Exception("Category id is missing");
            }
            return adminService.deleteCategory(payload.get("id").toString());
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    @PutMapping(UPDATE_CATEGORY)
    public ResponseEntity<Map<String, Object>> updateCategory(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new Exception("Category id is missing");
            }
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new Exception("Category name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new Exception("Category description is missing");
            }
            return adminService.updateCategory(payload);
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    @PostMapping(ADD_PAINTING)
    public ResponseEntity<Map<String, Object>> addPainting(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {

            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new Exception("Painting name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new Exception("Painting description is missing");
            }
            if (!payload.containsKey("categoryId") || payload.get("categoryId").toString().isEmpty()) {
                throw new Exception("Painting category is missing");
            }
            if (!payload.containsKey("price") || payload.get("price").toString().isEmpty()) {
                throw new Exception("Painting price is missing");
            }
            if (!payload.containsKey("image") || payload.get("image").toString().isEmpty()) {
                throw new Exception("Image is missing");
            }
            if (!payload.containsKey("quantity") || payload.get("quantity").toString().isEmpty()) {
                throw new Exception("Painting quantity is missing");
            }
            if (!payload.containsKey("maximumOrderQuantity") || payload.get("maximumOrderQuantity").toString().isEmpty()) {
                throw new Exception("Painting maximum order quantity is missing");
            }
            return adminService.addPainting(payload);
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    // get all paintings
    @GetMapping(GET_PAINTING)
    public ResponseEntity<Map<String, Object>> getPaintings(@RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            return adminService.getPaintings();
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    // delete painting
    @DeleteMapping(DELETE_PAINTING)
    public ResponseEntity<Map<String, Object>> deletePainting(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new Exception("Painting id is missing");
            }
            return adminService.deletePainting(payload.get("id").toString());
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }

    // update painting
    @PutMapping(UPDATE_PAINTING)
    public ResponseEntity<Map<String, Object>> updatePainting(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!headers.containsKey("authorization")) {
                map.put("status", 406);
                map.put("message", "Unauthorized access");
                return ResponseEntity.status(406).body(map);
            }
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new Exception("Painting id is missing");
            }
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new Exception("Painting name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new Exception("Painting description is missing");
            }
            if (!payload.containsKey("categoryId") || payload.get("categoryId").toString().isEmpty()) {
                throw new Exception("Painting category is missing");
            }
            if (!payload.containsKey("price") || payload.get("price").toString().isEmpty()) {
                throw new Exception("Painting price is missing");
            }
            if (!payload.containsKey("image") || payload.get("image").toString().isEmpty()) {
                throw new Exception("Image is missing");
            }
            if (!payload.containsKey("quantity") || payload.get("quantity").toString().isEmpty()) {
                throw new Exception("Painting quantity is missing");
            }
            if (!payload.containsKey("maximumOrderQuantity") || payload.get("maximumOrderQuantity").toString().isEmpty()) {
                throw new Exception("Painting maximum order quantity is missing");
            }
            return adminService.updatePainting(payload);
        } catch (TokenExpiredException e) {
            map.put("status", 406);
            map.put("message", e.getMessage());
            return ResponseEntity.status(406).body(map);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            map.put("status", 400);
            map.put("message", e.getMessage());
            map.put("data", new HashMap<>());
            return ResponseEntity.status(400).body(map);
        }
    }
}

class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}


class JWT extends AdminRoutes {
    @Autowired
    private AdminRepository adminRepository;
    Logger logger = Logger.getLogger(AdminController.class.getName());

    public AdminModel verifyJWTToken(String token) throws Exception {
        try {
            token = token.split(" ")[1]; // Remove "Bearer" prefix
            JWSVerifier verifier = new MACVerifier(Config.JWT_SIGN.getBytes());

            // Parse and verify token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Find the AdminModel by the JWT ID (which should be the admin ID)
            AdminModel adminModel = adminRepository.findById(signedJWT.getJWTClaimsSet().getJWTID()).orElseThrow(
                    () -> new TokenExpiredException("User not found")
            );

            // Additional verification steps
            if (!signedJWT.getJWTClaimsSet().getSubject().equals("ADMIN_ACCESS")) {
                throw new TokenExpiredException("Unauthorized access");
            }
            if (!signedJWT.getJWTClaimsSet().getIssuer().equals(adminModel.getEmail())) {
                throw new TokenExpiredException("Invalid token");
            }
            if (!signedJWT.getJWTClaimsSet().getJWTID().equals(adminModel.getId())) {
                throw new TokenExpiredException("Invalid token");
            }
            if (signedJWT.getJWTClaimsSet().getExpirationTime().before(Date.from(Instant.now()))) {
                throw new TokenExpiredException("Token expired");
            }
            if (!signedJWT.verify(verifier)) {
                throw new TokenExpiredException("Invalid token");
            }

            // Return the adminModel if everything is valid
            return adminModel;

        } catch (TokenExpiredException e) {
            throw e; // Rethrow the token expired exception to be handled later
        } catch (Exception e) {
            logger.warning(e.getMessage());
            throw new Exception("Token verification failed: " + e.getMessage());
        }
    }

}
