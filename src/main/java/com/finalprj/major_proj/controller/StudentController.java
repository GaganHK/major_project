package com.finalprj.major_proj.controller;

import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/student/signup")
    public String studentSignUpPage(Model model) {
        model.addAttribute("student", new Student()); // ✅ Required for th:object
        model.addAttribute("error", null);
        return "studentSignup";
    }

    @PostMapping("/student/signup")
    public String processStudentSignup(
            @RequestParam String usn,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        Student existing = studentRepository.findById(usn).orElse(null);
        if (existing == null) {
            model.addAttribute("error", "USN not found. Please contact admin.");
            return "studentSignup";
        }

        if (!existing.getEmail().equalsIgnoreCase(email)) {
            model.addAttribute("error", "Email does not match our records.");
            return "studentSignup";
        }

        if (existing.getPassword() != null) {
            model.addAttribute("error", "You have already signed up.");
            return "studentSignup";
        }

        existing.setPassword(password);
        existing.setRole("STUDENT");
        studentRepository.save(existing);

        return "redirect:/student/login";
    }

    @GetMapping("/student/login")
    public String studentLoginPage(Model model) {
        model.addAttribute("error", null);
        return "studentLogin";
    }

    @GetMapping("/search")
    public String showSearchPage() {
        return "search";
    }

    @PostMapping("/student/login")
    public String processStudentLogin(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Student student = studentRepository.findByEmail(email);
        if (student != null && student.getPassword().equals(password)) {
            session.setAttribute("loggedInStudentUsn", student.getUsn());
            session.setAttribute("studentName", student.getName().trim().toLowerCase());
            session.setAttribute("email", student.getEmail()); // ✅ FIX: added for view-marks to work
            session.setAttribute("role", "student");
            session.removeAttribute("loggedInAdminEmail");
            return "redirect:/search";
        }

        model.addAttribute("error", "Invalid credentials.");
        return "studentLogin";
    }

}
