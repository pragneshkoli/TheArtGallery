package com.example.the_art_gallery.exeption;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}