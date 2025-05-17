package com.finalprj.major_proj;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class welcomecontrl {
    @RequestMapping("/welcome")
    public String greet(){
        return "Welcome to our major project";
    }

//    @GetMapping("/")
//    public String showHomePage() {
//        return "home"; // This will load templates/home.html
//    }

    @GetMapping("/welcome") // Change the URL mapping here
    public String showHomePage() {
        return "home"; // Or the desired view name
    }

    @GetMapping("/admin-login")
    public String showAdminLogin() {
        return "admin-login";
    }

    @GetMapping("/admin-signup")
    public String showAdminSignup() {
        return "admin-signup";
    }

    @GetMapping("/student-login")
    public String showStudentLogin() {
        return "student-login";
    }

    @GetMapping("/student-signup")
    public String showStudentSignup() {
        return "student-signup";
    }
}
