package com.finalprj.major_proj.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslationService {

    public static String translateToEnglish(String input) {
        try {
            // Auto-detect language and translate to English
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" +
                    URLEncoder.encode(input, "UTF-8");
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            // Parse the translated result
            return response.toString().split("\"")[1];

        } catch (Exception e) {
            e.printStackTrace();
            return input;  // Fallback to original input if translation fails
        }
    }
}
