package com.example.the_art_gallery.logs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Logs {
    private final String className;
    private static final Lock lock = new ReentrantLock();
    private static final String LOG_FILE_NAME = "logs.txt";

    public Logs(String className) {
        this.className = className;
    }

    public void log(String message, String functionName) {
        lock.lock();  // Ensure thread safety
        try {
            File file = new File(LOG_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter fileWriter = new FileWriter(file, true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                String currentTime = LocalDateTime.now().toString();
                bufferedWriter.write("\n" + currentTime + "\t" + className + "." + functionName + ":\n" + message);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("An error occurred while logging.");
            e.printStackTrace();
        } finally {
            lock.unlock();  // Ensure the lock is released
        }
    }
}
