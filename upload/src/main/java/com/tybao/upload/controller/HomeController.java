package com.tybao.upload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@CrossOrigin(origins = {"https://congnghetoday.com", "https://congnghetoday.click", "https://anime404.click"}) 
public class HomeController {
    @GetMapping("/")
    public String getMethodName() {
        return "index.html";
    }
    
}
