package com.finalprj.major_proj.controller;

import jakarta.servlet.http.HttpSession;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class OCRController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SearchController searchController;  // ✅ Injected properly

    @PostMapping("/uploadImageQuery")
    public String uploadImageAndExtractQuery(@RequestParam("file") MultipartFile file,
                                             HttpServletRequest request) {
        JSONObject response = new JSONObject();

        try {
            File uploadDir = new File("C:/ocr-uploads/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String uniqueName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File savedFile = new File(uploadDir, uniqueName);
            file.transferTo(savedFile);

            // OCR and clean
            String ocrText = extractTextFromImage(savedFile);
            String cleanedQuery = cleanOcrText(ocrText);
            System.out.println("Cleaned OCR Query: " + cleanedQuery);

            // Use session-enabled search
            HttpSession session = request.getSession();
            return searchController.search(cleanedQuery,session);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error processing image: " + e.getMessage());
            return response.toString();
        }
    }

    private String extractTextFromImage(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        tesseract.setLanguage("eng");
        return tesseract.doOCR(imageFile);
    }

    private String cleanOcrText(String text) throws SQLException {
        text = text.replaceAll("[^a-zA-Z0-9 ?]", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();

        List<String> words = Arrays.asList(text.split(" "));
        String field = detectField(text);

        String extractedName = null;
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals("of") && i + 1 < words.size()) {
                extractedName = words.get(i + 1);
                break;
            }
        }

        if (extractedName == null) extractedName = "unknown";
        String correctedName = getClosestDatabaseName(extractedName);

        return "what is the " + field + " of " + correctedName + "?";
    }

    private String detectField(String text) {
        if (text.contains("father")) return "father's name";
        if (text.contains("gender")) return "gender";
        if (text.contains("age")) return "age";
        if (text.contains("address")) return "address";
        if (text.contains("pincode")) return "pincode";
        return "father's name";
    }

    private String getClosestDatabaseName(String inputName) throws SQLException {
        List<String> databaseNames = getAllNamesFromDatabase();
        LevenshteinDistance ld = new LevenshteinDistance();

        String closestName = inputName;
        int minDistance = Integer.MAX_VALUE;

        for (String dbName : databaseNames) {
            int distance = ld.apply(inputName.toLowerCase(), dbName.toLowerCase());
            if (distance < minDistance && distance <= 2) {
                minDistance = distance;
                closestName = dbName;
            }
        }

        if (minDistance == Integer.MAX_VALUE) {
            return "No close match found for name. Please check the name in the image.";
        }

        return closestName;
    }

    private List<String> getAllNamesFromDatabase() throws SQLException {
        List<String> names = new ArrayList<>();
        String query = "SELECT DISTINCT name FROM student";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }

        return names;
    }
}
