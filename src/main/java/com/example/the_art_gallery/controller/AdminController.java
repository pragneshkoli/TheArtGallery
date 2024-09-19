package com.example.the_art_gallery.controller;

import com.example.the_art_gallery.exeption.CustomException;
import com.example.the_art_gallery.exeption.TokenExpiredException;
import com.example.the_art_gallery.logs.Logs;
import com.example.the_art_gallery.model.AdminModel;
import com.example.the_art_gallery.repository.AdminRepository;
import com.example.the_art_gallery.response.Response;
import com.example.the_art_gallery.routes.AdminRoutes;
import com.example.the_art_gallery.service.AdminService;
import com.example.the_art_gallery.utils.Config;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import static com.example.the_art_gallery.routes.AdminRoutes.ADMIN;

@RestController
@RequestMapping(ADMIN)
public class AdminController extends JWT {
    @Autowired
    private AdminService adminService;

    Logger logger = Logger.getLogger(AdminController.class.getName());
    static Logs logs = new Logs(AdminController.class.getName());
    Response response = new Response();

    private void validateAuthorizationHeader(Map<String, Object> headers) throws TokenExpiredException {
        if (!headers.containsKey("authorization")) {
            throw new TokenExpiredException("Unauthorized access");
        }
    }


    /**
     * Admin login
     *
     * @param payload email, password
     * @return ResponseEntity
     */
    @PostMapping(ADMIN_LOGIN)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        try {
            if (payload.get("email").toString().isEmpty()) {
                throw new CustomException("Email is required");
            }
            if (payload.get("password").toString().isEmpty()) {
                throw new CustomException("Password is required");
            }
            return adminService.login(payload);
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "login");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Get Admin profile
     *
     * @param headers authorization
     * @return ResponseEntity
     */
    @GetMapping(ADMIN_PROFILE)
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            return adminService.getProfile(adminModel.getId());
        } catch (TokenExpiredException e) {
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "getProfile");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Update Admin profile
     *
     * @param payload name, email, phone
     * @param headers authorization
     * @return ResponseEntity
     */
    @PutMapping(ADMIN_UPDATE_PROFILE)
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new CustomException("Name is missing");
            }
            if (!payload.containsKey("email") || payload.get("email").toString().isEmpty()) {
                throw new CustomException("Email is missing");
            }
            if (!payload.containsKey("phone") || payload.get("phone").toString().isEmpty()) {
                throw new CustomException("Phone is missing");
            }
            if (payload.get("phone").toString().length() != 10) {
                throw new CustomException("Phone number should be 10 digits");
            }
            return adminService.updateProfile(payload, adminModel.getId());
        } catch (TokenExpiredException e) {
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "updateProfile");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Change Admin password
     *
     * @param payload oldPassword, newPassword
     * @param headers authorization
     * @return ResponseEntity
     */
    @PutMapping(ADMIN_CHANGE_PASSWORD)
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("oldPassword") || payload.get("oldPassword").toString().isEmpty()) {
                throw new CustomException("Old password is missing");
            }
            if (!payload.containsKey("newPassword") || payload.get("newPassword").toString().isEmpty()) {
                throw new CustomException("New password is missing");
            }
            if (payload.get("newPassword").toString().length() < 5) {
                throw new CustomException("Password should be at least 5 characters");
            }
            if (payload.get("newPassword").toString().equals(payload.get("oldPassword").toString())) {
                throw new CustomException("New password should not be same as old password");
            }
            return adminService.changePassword(payload, adminModel.getId());
        } catch (TokenExpiredException e) {
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "changePassword");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }


    /**
     * Add category
     *
     * @param payload name, description
     * @param headers authorization
     * @return ResponseEntity
     */
    @PostMapping(ADD_CATEGORY)
    public ResponseEntity<Map<String, Object>> addCategory(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new CustomException("Category name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new CustomException("Category description is missing");
            }

            return adminService.addCategory(payload);
        } catch (TokenExpiredException e) {
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "addCategory");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Get all categories
     *
     * @param headers authorization
     * @return ResponseEntity
     */
    @GetMapping(GET_CATEGORY)
    public ResponseEntity<Map<String, Object>> getCategories(@RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            return adminService.getCategories();
        } catch (TokenExpiredException e) {
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "getCategories");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Delete category
     *
     * @param payload id
     * @param headers authorization
     * @return ResponseEntity
     */
    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<Map<String, Object>> deleteCategory(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new CustomException("Category id is missing");
            }
            return adminService.deleteCategory(payload.get("id").toString());
        } catch (TokenExpiredException e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "deleteCategory");
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "deleteCategory");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }
    /**
     * Update category
     *
     * @param payload id, name, description
     * @param headers authorization
     * @return ResponseEntity
     */
    @PutMapping(UPDATE_CATEGORY)
    public ResponseEntity<Map<String, Object>> updateCategory(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new CustomException("Category id is missing");
            }
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new CustomException("Category name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new CustomException("Category description is missing");
            }
            return adminService.updateCategory(payload);
        } catch (TokenExpiredException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "updateCategory");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }
    /**
     * Add painting
     *
     * @param payload name, description, categoryId, price, image, quantity, maximumOrderQuantity
     * @param headers authorization
     * @return ResponseEntity
     */
    @PostMapping(ADD_PAINTING)
    public ResponseEntity<Map<String, Object>> addPainting(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {

            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new CustomException("Painting name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new CustomException("Painting description is missing");
            }
            if (!payload.containsKey("categoryId") || payload.get("categoryId").toString().isEmpty()) {
                throw new CustomException("Painting category is missing");
            }
            if (!payload.containsKey("price") || payload.get("price").toString().isEmpty()) {
                throw new CustomException("Painting price is missing");
            }
            if (!payload.containsKey("image") || payload.get("image").toString().isEmpty()) {
                throw new CustomException("Image is missing");
            }
            if (!payload.containsKey("quantity") || payload.get("quantity").toString().isEmpty()) {
                throw new CustomException("Painting quantity is missing");
            }
            if (!payload.containsKey("maximumOrderQuantity") || payload.get("maximumOrderQuantity").toString().isEmpty()) {
                throw new CustomException("Painting maximum order quantity is missing");
            }
            return adminService.addPainting(payload);
        } catch (TokenExpiredException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "addPainting");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Get all paintings
     *
     * @param headers authorization
     * @return ResponseEntity
     */
    @GetMapping(GET_PAINTING)
    public ResponseEntity<Map<String, Object>> getPaintings(@RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            return adminService.getPaintings();
        } catch (TokenExpiredException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "getPaintings");

        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Delete painting
     *
     * @param payload id
     * @param headers authorization
     * @return ResponseEntity
     */
    @DeleteMapping(DELETE_PAINTING)
    public ResponseEntity<Map<String, Object>> deletePainting(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new CustomException("Painting id is missing");
            }
            return adminService.deletePainting(payload.get("id").toString());
        } catch (TokenExpiredException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "deletePainting");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }

    /**
     * Update painting
     *
     * @param payload id, name, description, categoryId, price, image, quantity, maximumOrderQuantity
     * @param headers authorization
     * @return ResponseEntity
     */
    @PutMapping(UPDATE_PAINTING)
    public ResponseEntity<Map<String, Object>> updatePainting(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, Object> headers) {
        try {
            validateAuthorizationHeader(headers);
            AdminModel adminModel = verifyJWTToken(headers.get("authorization").toString());
            if (!payload.containsKey("id") || payload.get("id").toString().isEmpty()) {
                throw new CustomException("Painting id is missing");
            }
            if (!payload.containsKey("name") || payload.get("name").toString().isEmpty()) {
                throw new CustomException("Painting name is missing");
            }
            if (!payload.containsKey("description") || payload.get("description").toString().isEmpty()) {
                throw new CustomException("Painting description is missing");
            }
            if (!payload.containsKey("categoryId") || payload.get("categoryId").toString().isEmpty()) {
                throw new CustomException("Painting category is missing");
            }
            if (!payload.containsKey("price") || payload.get("price").toString().isEmpty()) {
                throw new CustomException("Painting price is missing");
            }
            if (!payload.containsKey("image") || payload.get("image").toString().isEmpty()) {
                throw new CustomException("Image is missing");
            }
            if (!payload.containsKey("quantity") || payload.get("quantity").toString().isEmpty()) {
                throw new CustomException("Painting quantity is missing");
            }
            if (!payload.containsKey("maximumOrderQuantity") || payload.get("maximumOrderQuantity").toString().isEmpty()) {
                throw new CustomException("Painting maximum order quantity is missing");
            }
            return adminService.updatePainting(payload);
        } catch (TokenExpiredException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(406, e.getMessage());
        } catch (CustomException e) {
            logger.warning(e.getMessage());
            return response.sendBadRequest(400, e.getMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "updatePainting");
        }
        return response.sendBadRequest(400, "Something went wrong");
    }
}


class JWT extends AdminRoutes {
    @Autowired
    private AdminRepository adminRepository;
    Logger logger = Logger.getLogger(AdminController.class.getName());
    static Logs logs = new Logs(JWT.class.getName());

    /**
     * Verify JWT token
     *
     * @param token JWT token
     * @return AdminModel
     * @throws Exception if token verification fails or token is invalid or expired
     * @exception TokenExpiredException if token is expired or invalid or unauthorized access
     */
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
            logger.warning(e.getMessage());
            throw e; // Rethrow the token expired exception to be handled later
        } catch (Exception e) {
            logger.warning(e.getMessage());
            logs.log(e.getMessage(), "verifyJWTToken");
            throw new Exception("Token verification failed: " + e.getMessage());
        }
    }

}
