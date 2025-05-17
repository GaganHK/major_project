package com.finalprj.major_proj.controller;

import com.finalprj.major_proj.entity.Admin;
import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.repo.AdminRepository;
import com.finalprj.major_proj.repo.StudentRepository;
import jakarta.servlet.http.HttpSession;  // ✅ Required for session handling
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;  // Added for student access

    @GetMapping("/admin/login")
    public String adminLoginPage(Model model) {
        model.addAttribute("error", null);
        return "adminLogin";
    }




    @GetMapping("/admin/signup")
    public String adminSignUpPage() {
        return "adminSignup";
    }

    @PostMapping("/admin/signup")
    public String processAdminSignup(@ModelAttribute Admin admin, Model model) {
        long count = adminRepository.count();

        if (count >= 4) {
            model.addAttribute("error", "Maximum 4 admins can register. Registration is closed.");
            return "adminSignup";
        }

        // Check if email already exists
        if (adminRepository.findByEmail(admin.getEmail()) != null) {
            model.addAttribute("error", "An account with this email already exists.");
            return "adminSignup";
        }

        adminRepository.save(admin);
        return "redirect:/admin/login";
    }

    @PostMapping("/admin/login")
    public String processAdminLogin(@RequestParam String email,
                                    @RequestParam String password,
                                    Model model,
                                    HttpSession session) {  // ✅ Add HttpSession
        Admin admin = adminRepository.findByEmail(email);

        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("role", "admin");         // ✅ Set admin role
            session.setAttribute("adminEmail", email);     // ✅ Store admin email

            // ✅ Clear any previous student session data
            session.removeAttribute("loggedInStudentUsn");
            session.removeAttribute("studentName");

            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid email or password");
        return "adminLogin";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "adminDashboard";
    }

    @GetMapping("/admin/register")
    public String adminRegisterRedirect(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    @GetMapping("/admin/search")
    public String adminSearchRedirect() {
        return "search";
    }

    // ----------------------------
    // Edit Student Feature Below
    // ----------------------------

    @GetMapping("/admin/edit-student")
    public String showEditStudentSearchPage() {
        return "editStudentSearch";
    }

    @PostMapping("/admin/edit-student")
    public String processStudentSearch(@RequestParam String usn, Model model) {
        Student student = studentRepository.findById(usn).orElse(null);
        if (student != null) {
            model.addAttribute("student", student);
            return "editStudentForm";
        }
        model.addAttribute("error", "Student with USN not found");
        return "editStudentSearch";
    }

    @GetMapping("/update_marks")
    public String showUpdateMarksPage() {
        return "update_marks"; // returns update_marks.html from templates
    }

    @PostMapping("/admin/update-student")
    public String updateStudent(@ModelAttribute Student student, Model model) {
        studentRepository.save(student);
        model.addAttribute("message", "Student updated successfully.");
        return "adminDashboard";

    }

        }





