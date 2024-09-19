package com.example.the_art_gallery;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class testingFile {

    public static void main(String[] args) {
        log("This is a test message");
    }
  public static void log(String message) {
        try {
            File file = new File("logs.txt");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
