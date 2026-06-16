package utils;

import model.Course;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class untuk membaca dataset dari file CSV.
 * Dibuat oleh System Integrator untuk mendukung loading data awal.
 */
public class CSVReader {

    /**
     * Membaca daftar mata kuliah dari file CSV.
     * Format: code,name,sks,semester,type,kuota,departemen
     */
    public static List<Course> readCourses(String filename) {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue;
                try {
                    String code     = parts[0].trim();
                    String name     = parts[1].trim();
                    int    sks      = Integer.parseInt(parts[2].trim());
                    int    semester = Integer.parseInt(parts[3].trim());
                    String type     = parts[4].trim();
                    int    kuota    = Integer.parseInt(parts[5].trim());
                    String dept     = parts[6].trim();
                    courses.add(new Course(code, name, sks, semester, type, kuota, dept));
                } catch (NumberFormatException e) {
                    System.out.println("[CSVReader] Baris tidak valid dilewati: " + line);
                }
            }
            System.out.println("[CSVReader] " + courses.size() + " mata kuliah berhasil dimuat.");
        } catch (IOException e) {
            System.out.println("[CSVReader] Error membaca file: " + e.getMessage());
        }
        return courses;
    }
}