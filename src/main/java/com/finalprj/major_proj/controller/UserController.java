package com.finalprj.major_proj.controller;

import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.repo.StudentRepository;
import com.finalprj.major_proj.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired(required = false) // In case you're not always injecting EmailService
    private EmailService emailService;

    // Show the register.html page (custom path /hello)
    @GetMapping("/hello")
    public String register(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    // Handle register.html form submission
    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute("student") Student student, Model model) {
        if (studentRepository.existsByUsn(student.getUsn())) {
            model.addAttribute("errorMessage", "USN already exists! Can't register again");
            return "register";
        }

        studentRepository.save(student);

        // Send email if service available
        if (emailService != null) {
            String subject = "Registration Successful";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "Thank you for registering. Your registration has been successfully completed.\n\n" +
                    "Here are your details:\n" +
                    "Name: " + student.getName() + "\n" +
                    "USN: " + student.getUsn() + "\n" +
                    "Email: " + student.getEmail() + "\n\n" +
                    "Regards,\nFinal Project Team";

            emailService.sendRegistrationEmail(student.getEmail(), subject, body);
        }

        model.addAttribute("successMessage", "Student registered successfully!");
        model.addAttribute("student", new Student()); // Reset form
        return "register";
    }

    // Show update marks page
    @GetMapping("/update-marks")
    public String updateMarksPage(Model model) {
        return "update_marks";  // Matches Thymeleaf file name
    }

    // Fetch students by branch and semester
    @RequestMapping(value = "/fetchStudents", method = {RequestMethod.GET, RequestMethod.POST})
    public String fetchStudents(@RequestParam String branch,
                                @RequestParam String semester,
                                Model model) {
        int sem;
        try {
            sem = Integer.parseInt(semester);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid semester value");
            return "update_marks";
        }

        List<Student> students = studentRepository.findByBranchAndSemester(branch, sem);
        model.addAttribute("students", students);
        model.addAttribute("selectedBranch", branch);
        model.addAttribute("selectedSemester", sem);
        return "update_marks";
    }

    // Search student by query like "What is the email of Inchara?"
    @GetMapping("/searchByQuery")
    public String searchByQuery(@RequestParam String query,
                                @RequestParam String branch,
                                @RequestParam Integer semester,
                                Model model) {

        model.addAttribute("selectedBranch", branch);
        model.addAttribute("selectedSemester", semester);

        String lower = query.toLowerCase().trim();
        String name = extractNameFromQuery(lower);
        if (name == null || name.isEmpty()) {
            model.addAttribute("error", "Could not extract name from query.");
            return "update_marks";
        }

        List<Student> students = studentRepository.searchByNameAndContext(name, branch, semester);

        if (!students.isEmpty()) {
            Student student = students.get(0);
            model.addAttribute("emailResult", student.getEmail());
        } else {
            model.addAttribute("result", "No student found");
        }

        model.addAttribute("students", students);
        return "update_marks";
    }

    // Helper method to extract student name from query
    private String extractNameFromQuery(String query) {
        query = query.toLowerCase().trim();
        if (query.contains(" of ")) {
            return query.substring(query.lastIndexOf(" of ") + 4).trim().replaceAll("[^a-zA-Z\\s]", "");
        }
        return null;
    }
}
