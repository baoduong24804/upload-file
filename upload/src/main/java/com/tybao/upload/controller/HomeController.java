package com.tybao.upload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@CrossOrigin(origins = "*") // Cho phép tất cả các địa chỉ web
public class HomeController {
    @GetMapping("/")
    public String getMethodName() {
        return "index.html";
    }
    
}
