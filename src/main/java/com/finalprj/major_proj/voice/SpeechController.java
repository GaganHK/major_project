package com.finalprj.major_proj.voice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SpeechController {

    private static final Logger logger = LoggerFactory.getLogger(SpeechController.class);
    private final MyOpenAiService openAiService;

    public SpeechController(MyOpenAiService openAiService) {
        this.openAiService = openAiService;
    }

//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }

    @GetMapping("/speech")
    public String index() {
        return "index"; // Or the view you want to return
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        logger.info("🔹 File received: {}", file.getOriginalFilename());

        File convFile = new File(System.getProperty("java.io.tmpdir"), Objects.requireNonNull(file.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());

            String transcription = openAiService.transcribeAudio(convFile);
            logger.info("📝 Transcription: {}", transcription);

            String response = openAiService.ask(transcription);
            logger.info("💬 NLP Response: {}", response);

            model.addAttribute("transcription", transcription);
            model.addAttribute("response", response);

        } catch (IOException | RuntimeException e) {
            logger.error("❌ Error handling upload", e);
            model.addAttribute("transcription", "Error: " + e.getMessage());
            model.addAttribute("response", "N/A");
        }

        return "index";
    }

    @PostMapping("/api/speech-to-nlp")
    @ResponseBody
    public ResponseEntity<String> handleApiUpload(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("🔹 API File received: {}", file.getOriginalFilename());

            File convFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            }

            String transcription = openAiService.transcribeAudio(convFile);
            logger.info("📝 API Transcription: {}", transcription);

            String response = openAiService.ask(transcription);
            logger.info("💬 API NLP Response: {}", response);

            return ResponseEntity.ok(transcription);
        } catch (IOException | RuntimeException e) {
            logger.error("❌ API error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }
}
