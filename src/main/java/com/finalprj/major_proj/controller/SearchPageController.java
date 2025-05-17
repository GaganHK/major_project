package com.finalprj.major_proj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchPageController {

    @GetMapping("/searchPage")
    public String showSearchPage() {
        return "search";
    }
}
