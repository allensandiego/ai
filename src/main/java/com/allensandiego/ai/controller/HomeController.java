package com.allensandiego.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    
    @GetMapping ("/")
    public String home() {
        return "index";
    }

    @GetMapping ("/dashboard")
    public String dashboard() {
        return "blank";
    }

}
