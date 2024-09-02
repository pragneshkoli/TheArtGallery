package com.example.the_art_gallery;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.the_art_gallery.model.AdminModel;
import com.example.the_art_gallery.model.UserModel;
import com.example.the_art_gallery.repository.AdminRepository;
import com.example.the_art_gallery.repository.UserRepository;
import com.example.the_art_gallery.utils.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class TheArtGalleryApplication {
    Logger logger = Logger.getLogger(TheArtGalleryApplication.class.getName());
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public TheArtGalleryApplication(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(TheArtGalleryApplication.class, args);
        Logger logger = Logger.getLogger(TheArtGalleryApplication.class.getName());
        logger.info("Server started, listening on port " + Config.PORT);
    }

    @GetMapping()
    public Map<String, Object> root() {
        return new HashMap<>() {

            {
                put("message", "Chhim Tapak Dam Dam");
            }
        };
    }

    @PostConstruct // Executed after Spring context initialization
    public void createAdminIfNotExists() {
        try {
            if (adminRepository.findByEmail(Config.EMAIL).isEmpty()) {
                AdminModel adminUser = new AdminModel(
                        Config.ADMIN_NAME,
                        BCrypt.withDefaults().hashToString(12, Config.ADMIN_PASSWORD.toCharArray()),
                        Config.EMAIL,
                        Long.parseLong(Config.PHONE)
                );
                adminRepository.save(adminUser);
                logger.info("Admin created");
            }
        } catch (Exception e) {

            logger.log(Level.WARNING, e.toString());
        }
    }

    @PostConstruct // Executed after Spring context initialization
    public void createTestUserIfNotExists() {
        try {
            if (userRepository.findByEmail("pragneshkoli@gmail.com").isEmpty()) {
                UserModel user = new UserModel(
                        "Pragnesh",
                        "Koli",
                        "pragneshkoli@gmail.com",
                        6351621487L,
                        BCrypt.withDefaults().hashToString(12, "User@123".toCharArray()),
                        "B/34,Jay Suryanagar Society", "Nikol Gaam Road", "Ahmedabad", "Gujarat", "382350", "India"
                );
                userRepository.save(user);
                logger.info("Test user created");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }
}
