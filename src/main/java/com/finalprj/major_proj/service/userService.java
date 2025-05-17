package com.finalprj.major_proj.service;

import com.finalprj.major_proj.entity.Student;
import com.finalprj.major_proj.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userService {

    @Autowired
    private StudentRepository studentRepository;

    // Register User (Student or Admin)
    public void registerUser(Student student) {
        // We assume that the Student object has a "role" attribute set by the controller.
        // For example, if "role" is 'admin' or 'student', it will be saved accordingly.
        studentRepository.save(student);
    }

    // Student Login
    public boolean studentLogin(String email, String password) {
        Student student = studentRepository.findByEmail(email);
        return student != null && student.getPassword().equals(password);
    }

    // Admin Login (Assumes admin checks based on role)
    public boolean adminLogin(String email, String password) {
        Student student = studentRepository.findByEmail(email);
        return student != null && student.getPassword().equals(password) && "admin".equals(student.getRole());
    }

    // Register Admin (Can be the same as regular user, just set the role to 'admin')
    public boolean registerAdmin(Student student) {
        student.setRole("admin");
        studentRepository.save(student);
        return false;
    }

    // Register Student (Set the role to 'student' before saving)
    public boolean registerStudent(Student student) {
        student.setRole("student");
        studentRepository.save(student);
        return false;
    }
}
