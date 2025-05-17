package com.finalprj.major_proj.controller;

import com.finalprj.major_proj.dto.MarksFormDTO;
import com.finalprj.major_proj.dto.MarksForm;
import com.finalprj.major_proj.dto.SubjectDTO;
import com.finalprj.major_proj.dto.SubjectMark;
import com.finalprj.major_proj.entity.Result;
import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.service.EmailService;
import com.finalprj.major_proj.service.ResultService;
import com.finalprj.major_proj.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;


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

    @GetMapping("/chooseMarkType")
    public String chooseMarkType(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "chooseMarkType";
    }

    @GetMapping("/enterSemesterMarks")
    public String enterSemesterMarks(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "semester_marks_form";
    }

    @GetMapping("/enterInternalMarks")
    public String enterInternalMarks(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "internal_marks_form";
    }

    @PostMapping("/submitMarks")
    public String submitMarks(@ModelAttribute MarksFormDTO marksForm, Model model) {
        model.addAttribute("marksForm", marksForm);  // For binding in result.html
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
                    sb.append(sub.getSubjectName()).append(": Scored ").append(sub.getScored())
                            .append(" out of ").append(sub.getMax()).append("\n");
                }
                sb.append("\nRegards,\nYour Institution");

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

    // ✅ Unified marks view for both Admin and Student
//    @GetMapping("/view-marks")
//    public String viewMarks(@RequestParam(value = "usn", required = false) String usn,
//                            HttpSession session,
//                            Model model) {
//
//        String email = null;
//
//        if (usn != null && !usn.isEmpty()) {
//            Optional<Student> studentOpt = studentRepository.findByUsn(usn);
//            if (studentOpt.isPresent()) {
//                email = studentOpt.get().getEmail();
//                model.addAttribute("usn", usn);
//            } else {
//                model.addAttribute("error", "Student with USN " + usn + " not found.");
//                return "view-marks";
//            }
//        } else {
//            email = (String) session.getAttribute("email");
//            if (email == null || email.isEmpty()) {
//                model.addAttribute("error", "No USN or student email found. Please log in.");
//                return "view-marks";
//            }
//        }
//
//        List<Result> results = resultService.getResultsByEmail(email);
//        model.addAttribute("email", email);
//        model.addAttribute("results", results);
//        return "view-marks";
//   }

    @GetMapping("/view-marks")
    public String viewMarks(@RequestParam(value = "usn", required = false) String usn,
                            HttpSession session,
                            Model model) {

        String role = (String) session.getAttribute("role"); // "student" or null (admin)
        String email = null;
        String studentName = null;

        // If ADMIN and no USN entered yet, show the search form
        if (role == null && (usn == null || usn.trim().isEmpty())) {
            return "view-marks"; // form will be shown in the HTML template
        }

        // ADMIN entered USN
        if (role == null && usn != null && !usn.trim().isEmpty()) {
            Optional<Student> studentOpt = studentRepository.findByUsn(usn.trim());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                email = student.getEmail();
                studentName = student.getName();
                model.addAttribute("usn", student.getUsn());
            } else {
                model.addAttribute("error", "Student with USN " + usn + " not found.");
                return "view-marks";
            }
        }

        // STUDENT is logged in
        if ("student".equals(role)) {
            email = (String) session.getAttribute("email");
            if (email == null || email.isEmpty()) {
                model.addAttribute("error", "No email in session. Please log in.");
                return "view-marks";
            }

            Optional<Student> studentOpt = Optional.ofNullable(studentRepository.findByEmail(email));
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                studentName = student.getName();
                model.addAttribute("usn", student.getUsn());
            }
        }

        if (email == null) {
            // For admin, just return form without error on first visit
            if (role == null && (usn == null || usn.isEmpty())) {
                return "view-marks"; // Admin initial visit
            } else {
                model.addAttribute("error", "Student not found.");
                return "view-marks";
            }
        }


        List<Result> results = resultService.getResultsByEmail(email);
        model.addAttribute("studentName", studentName);
        model.addAttribute("email", email);
        model.addAttribute("results", results);
        return "view-marks";
    }


}


