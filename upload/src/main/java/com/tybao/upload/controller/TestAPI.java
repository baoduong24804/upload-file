package com.tybao.upload.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TestAPI {
    @GetMapping("/")
    public String getMethodName() {
        return "Hello World!";
    }
    
}
