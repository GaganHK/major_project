package com.finalprj.major_proj.controller;

import ch.qos.logback.core.model.Model;
import com.finalprj.major_proj.config.DatabaseConnection;
import com.finalprj.major_proj.service.QueryHistoryService;
import com.finalprj.major_proj.util.TranslationService;

import jakarta.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private QueryHistoryService queryHistoryService;

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, HttpSession session) {
        JSONObject jsonResponse = new JSONObject();

        String role = (String) session.getAttribute("role");
        String loggedInStudentName = (String) session.getAttribute("studentName");

        // 🌍 Step 1: Translate input to English
        String translatedQuery = TranslationService.translateToEnglish(query);
        System.out.println("📝 Original (Kannada) query: " + query);
        System.out.println("🌐 Translated query: " + translatedQuery);

        // 🧠 Extract name & build SQL query
        String extractedName = extractName(translatedQuery);
        String sqlQuery = convertToSQL(translatedQuery);

        System.out.println("🔍 Extracted name: " + extractedName);
        System.out.println("🧾 SQL Query: " + sqlQuery);

        if (sqlQuery == null || extractedName == null || extractedName.isEmpty()) {
            return buildErrorResponse("Invalid query format or missing name.");
        }

        queryHistoryService.saveQuery(query, false); // Save original (non-English) query

        // 🔐 Access control for students
        if ("student".equalsIgnoreCase(role)) {
            if (loggedInStudentName == null || !extractedName.trim().equalsIgnoreCase(loggedInStudentName)) {
                return buildErrorResponse("Access denied. You can only view your own data.");
            }
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 👥 If multiple students with same name, warn admin
            if ("admin".equalsIgnoreCase(role)) {
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM student WHERE LOWER(name) = LOWER(?)")) {
                    checkStmt.setString(1, extractedName);
                    ResultSet countRs = checkStmt.executeQuery();
                    if (countRs.next() && countRs.getInt(1) > 1) {
                        jsonResponse.put("result", "multiple");
                        jsonResponse.put("message", "There are multiple students with this name. Please search using USN.");
                        return jsonResponse.toString();
                    }
                }
            }

            // 🔍 Execute final query
            try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
                pstmt.setString(1, extractedName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String result = rs.getString(1);
                        jsonResponse.put("result", (result == null || result.isEmpty()) ? "No data found." : result);
                    } else {
                        jsonResponse.put("result", "No data found.");
                    }
                }
            }

        } catch (SQLException | JSONException e) {
            return buildErrorResponse("Database error: " + e.getMessage());
        }

        return jsonResponse.toString();
    }

    @GetMapping("/studentHome")
    public String studentHome() {
        return "redirect:/chooseMarkType";
    }

    @GetMapping("/chooseMarkType")
    public String showChooseMarkTypePage() {
        return "chooseMarkType";
    }

    @GetMapping("/searchWithUSN")
    public String searchWithUSN(@RequestParam String name, @RequestParam String usn, HttpSession session) {
        JSONObject jsonResponse = new JSONObject();

        String role = (String) session.getAttribute("role");
        String loggedInStudentName = (String) session.getAttribute("studentName");

        String translatedQuery = TranslationService.translateToEnglish(name);

        String extractedName = extractName(translatedQuery);
        String fieldToSearch = getFieldFromQuery(translatedQuery);

        if (extractedName == null || fieldToSearch == null) {
            return buildErrorResponse("Invalid query format.");
        }

        queryHistoryService.saveQuery(name + " with USN " + usn, false);

        if ("student".equalsIgnoreCase(role)) {
            if (loggedInStudentName == null || !extractedName.trim().equalsIgnoreCase(loggedInStudentName)) {
                return buildErrorResponse("Access denied. You can only view your own data.");
            }
        }

        String sqlQuery = "SELECT " + fieldToSearch + " FROM student WHERE LOWER(name) = LOWER(?) AND LOWER(usn) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            pstmt.setString(1, extractedName);
            pstmt.setString(2, usn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String result = rs.getString(1);
                    jsonResponse.put("result", result);
                } else {
                    jsonResponse.put("result", "No data found for provided USN.");
                }
            }

        } catch (SQLException | JSONException e) {
            return buildErrorResponse("Database error: " + e.getMessage());
        }

        return jsonResponse.toString();
    }

    private String buildErrorResponse(String message) {
        JSONObject errorObj = new JSONObject();
        try {
            errorObj.put("error", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorObj.toString();
    }

    private String convertToSQL(String input) {
        input = input.toLowerCase().trim();

        // Student table
        if (input.contains("gender")) return "SELECT gender FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("father")) return "SELECT fathername FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("age")) return "SELECT age FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("usn")) return "SELECT usn FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("branch")) return "SELECT branch FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("semester")) return "SELECT semester FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("email")) return "SELECT email FROM student WHERE LOWER(name) = LOWER(?)";
        if (input.contains("mobile")) return "SELECT mobile FROM student WHERE LOWER(name) = LOWER(?)";

        // Result table
        if (input.contains("total marks") || input.contains("total scored"))
            return "SELECT total_scored FROM result WHERE LOWER(email) = (SELECT email FROM student WHERE LOWER(name) = LOWER(?))";
        if (input.contains("maximum marks") || input.contains("total max"))
            return "SELECT total_max FROM result WHERE LOWER(email) = (SELECT email FROM student WHERE LOWER(name) = LOWER(?))";
        if (input.contains("percentage"))
            return "SELECT percentage FROM result WHERE LOWER(email) = (SELECT email FROM student WHERE LOWER(name) = LOWER(?))";
        if (input.contains("cgpa"))
            return "SELECT cgpa FROM result WHERE LOWER(email) = (SELECT email FROM student WHERE LOWER(name) = LOWER(?))";

        return null;
    }

    private String extractName(String input) {
        input = input.toLowerCase().trim();
        String[] words = input.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            if (words[i].endsWith("'s")) {
                String name = words[i].substring(0, words[i].length() - 2).replaceAll("[^a-z]", "").trim();
                if (!name.isEmpty()) return name;
            }
        }

        if (input.contains(" of ")) {
            return input.substring(input.lastIndexOf(" of ") + 4).replaceAll("[^a-z\\s]", "").trim();
        }

        String[] commonWords = {
                "what", "is", "the", "tell", "me", "please", "show", "give",
                "email", "mobile", "number", "age", "usn", "branch", "semester",
                "gender", "father", "name", "of", "'s", "marks", "cgpa", "percentage", "total", "maximum"
        };

        StringBuilder nameBuilder = new StringBuilder();
        for (String word : words) {
            String clean = word.replaceAll("[^a-z]", "");
            boolean isCommon = false;
            for (String common : commonWords) {
                if (clean.equals(common)) {
                    isCommon = true;
                    break;
                }
            }
            if (!isCommon && !clean.isEmpty()) {
                nameBuilder.append(clean).append(" ");
            }
        }

        String name = nameBuilder.toString().trim();
        return name.isEmpty() ? null : name;
    }

    private String getFieldFromQuery(String input) {
        input = input.toLowerCase();

        if (input.contains("gender")) return "gender";
        if (input.contains("father")) return "fathername";
        if (input.contains("age")) return "age";
        if (input.contains("usn")) return "usn";
        if (input.contains("branch")) return "branch";
        if (input.contains("semester")) return "semester";
        if (input.contains("email")) return "email";
        if (input.contains("mobile")) return "mobile";

        if (input.contains("total marks") || input.contains("total scored"))
            return "total_scored";
        if (input.contains("maximum marks") || input.contains("total max"))
            return "total_max";
        if (input.contains("percentage"))
            return "percentage";
        if (input.contains("cgpa"))
            return "cgpa";

        return null;
    }

}
