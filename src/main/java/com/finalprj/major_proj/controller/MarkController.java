package com.finalprj.major_proj.controller;

import com.finalprj.major_proj.dto.MarksFormDTO;
import com.finalprj.major_proj.dto.MarksForm;
import com.finalprj.major_proj.dto.SubjectDTO;
import com.finalprj.major_proj.dto.SubjectMark;
import com.finalprj.major_proj.entity.Result;
import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.repo.ResultRepository;
import com.finalprj.major_proj.repo.StudentRepository;
import com.finalprj.major_proj.service.EmailService;
import com.finalprj.major_proj.service.ResultService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MarkController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResultService resultService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ResultRepository resultRepository;

    @GetMapping("/chooseMarkType")
    public String chooseMarkType(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "chooseMarkType";
    }

    @GetMapping("/enterSemesterMarks")
    public String enterSemesterMarks(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);

        // Get semesters already submitted for this email
        List<Integer> submittedSemesters = resultRepository.findSemestersByEmail(email); // Make sure this method exists

        // All 8 semesters
        List<Integer> allSemesters = java.util.stream.IntStream.rangeClosed(1, 8).boxed().toList();

        // Remove already submitted ones
        List<Integer> availableSemesters = allSemesters.stream()
                .filter(s -> !submittedSemesters.contains(s))
                .toList();

        model.addAttribute("availableSemesters", availableSemesters);
        return "semester_marks_form";
    }

    @GetMapping("/enterInternalMarks")
    public String enterInternalMarks(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "internal_marks_form";
    }

    @PostMapping("/submitMarks")
    public String submitMarks(@ModelAttribute MarksFormDTO marksForm, Model model) {
        model.addAttribute("marksForm", marksForm);
        model.addAttribute("message", null);
        return "result";
    }

    @PostMapping("/sendResultEmail")
    public String sendResultEmail(@ModelAttribute MarksFormDTO marksForm, Model model) {
        String email = marksForm.getEmail();

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("message", "Invalid email address provided.");
            model.addAttribute("marksForm", marksForm);
            return "result";
        }

        email = email.trim();
        MarksForm convertedForm = convertDtoToForm(marksForm);

        String dbSaveMessage = resultService.saveResult(convertedForm);
        model.addAttribute("message", dbSaveMessage);

        if (!dbSaveMessage.toLowerCase().contains("already")) {
            try {
                int semester = marksForm.getSemester();

                // Compose email content
                StringBuilder sb = new StringBuilder();
                sb.append("Dear Student,\n\n");
                sb.append("Your marks for semester ").append(semester).append(" have been updated.\n\n");
                sb.append("Summary:\n");
                sb.append("Total Scored: ").append(marksForm.getTotalScored()).append("\n");
                sb.append("Total Max: ").append(marksForm.getTotalMax()).append("\n");
                sb.append("Percentage: ").append(marksForm.getPercentage()).append("%\n");
                sb.append("CGPA: ").append(marksForm.getCgpa()).append("\n\n");
                sb.append("Subjects:\n");

                for (SubjectDTO sub : marksForm.getSubjects()) {
                    sb.append(sub.getSubjectName()).append(": Scored ")
                            .append(sub.getScored()).append(" out of ").append(sub.getMax()).append("\n");
                }

                sb.append("\nRegards,\nYour Institution");

                // Send email
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Updated Marks for Semester " + semester);
                message.setText(sb.toString());

                mailSender.send(message);
                model.addAttribute("message", dbSaveMessage + " Result email sent successfully!");
            } catch (Exception e) {
                model.addAttribute("message", dbSaveMessage + " But error sending email: " + e.getMessage());
            }
        }

        model.addAttribute("marksForm", marksForm);
        return "result";
    }

    private MarksForm convertDtoToForm(MarksFormDTO dto) {
        MarksForm form = new MarksForm();
        form.setEmail(dto.getEmail());
        form.setSemester(dto.getSemester());
        form.setTotalScored((int) dto.getTotalScored());
        form.setTotalMax((int) dto.getTotalMax());
        form.setPercentage(dto.getPercentage());
        form.setCgpa(dto.getCgpa());

        List<SubjectMark> subjectMarks = dto.getSubjects().stream().map(sub -> {
            SubjectMark sm = new SubjectMark();
            sm.setSubjectName(sub.getSubjectName());
            sm.setScored(sub.getScored());
            sm.setMax(sub.getMax());
            return sm;
        }).collect(Collectors.toList());

        form.setSubjects(subjectMarks);
        return form;
    }

    @GetMapping("/view-marks")
    public String viewMarks(@RequestParam(required = false) String usn,
                            HttpSession session,
                            Model model) {

        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("email");

        // Admin Flow
        if ("admin".equals(role)) {
            if (usn == null || usn.trim().isEmpty()) {
                model.addAttribute("error", "Please enter a USN.");
                return "view-marks";
            }

            Optional<Student> studentOpt = studentRepository.findByUsn(usn.trim());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                email = student.getEmail();
                model.addAttribute("studentName", student.getName());
                model.addAttribute("usn", student.getUsn());
            } else {
                model.addAttribute("error", "No student found for USN: " + usn);
                return "view-marks";
            }
        }

        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Unable to retrieve marks. Email not found.");
            return "view-marks";
        }

        List<Result> results = resultRepository.findByEmail(email);

        if (results.isEmpty()) {
            model.addAttribute("error", "No marks found for this student.");
        } else {
            model.addAttribute("results", results);
            model.addAttribute("email", email);
        }

        return "view-marks";
    }
}
