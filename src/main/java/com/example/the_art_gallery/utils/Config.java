package com.example.the_art_gallery.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    static Dotenv dotenv = Dotenv.load();
    public static String JWT_SIGN = dotenv.get("JWT_SECRETE");
    public static String PORT = dotenv.get("PORT");
    public static String EMAIL = dotenv.get("ADMIN_EMAIL");
    public static String ADMIN_NAME = dotenv.get("ADMIN_NAME");
    public static String ADMIN_PASSWORD = dotenv.get("ADMIN_PASSWORD");
    public static String PHONE = dotenv.get("PHONE");
    public  static  String ADMIN_ROUTE = dotenv.get("ADMIN_ROUTE");
    public  static  String USER_ROUTE = dotenv.get("USER_ROUTE");
}
