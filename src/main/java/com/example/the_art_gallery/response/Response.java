package com.example.the_art_gallery.response;

import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Response {

    /**
     * Sends a success response with the specified status code and message.
     *
     * @param status  the HTTP status code to be returned in the response
     * @param message the success message to be included in the response
     * @return a ResponseEntity containing the response map with status and message
     */
    public ResponseEntity<Map<String, Object>> sendBadRequest(int status, String message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("count", 0);
        map.put("data", new LinkedHashMap<>());
        return ResponseEntity.status(status).body(map);
    }

    /**
     * Sends a success response with the specified status code, message, and data.
     *
     * @param status  the HTTP status code to be returned in the response
     * @param message the success message to be included in the response
     * @param data    the data to be included in the response
     * @return a ResponseEntity containing the response map with status, message, and data
     */
    public ResponseEntity<Map<String, Object>> sendSuccess(int status, String message, Map<String, Object> data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("count", 1);
        map.put("data", data);
        return ResponseEntity.status(status).body(map);
    }

    /**
     * Sends a success response with the specified status code, message, and data.
     *
     * @param status  the HTTP status code to be returned in the response
     * @param message the success message to be included in the response
     * @param data    the data to be included in the response
     * @return a ResponseEntity containing the response map with status, message, and data
     */
    public ResponseEntity<Map<String, Object>> sendSuccess(int status, String message, List<Map<String, Object>> data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("count", data.size());
        map.put("data", data);
        return ResponseEntity.status(status).body(map);
    }


    /**
     * Sends a success response with the specified status code, message, and data.
     *
     * @param status  the HTTP status code to be returned in the response
     * @param message the success message to be included in the response
     * @param token   the token to be included in the response
     * @param data    the data to be included in the response
     * @return a ResponseEntity containing the response map with status, message, token, and data
     */
    public ResponseEntity<Map<String, Object>> sendSuccess(int status, String message, String token, Map<String, Object> data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("token", token);
        map.put("count", 1);
        map.put("data", data);
        return ResponseEntity.status(status).body(map);
    }
}
