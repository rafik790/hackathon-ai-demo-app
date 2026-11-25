package com.hackathon.demo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        System.out.println("I get fired);
        return Map.of(
                "application", "Hackathon Demo App",
                "message", "Hello! Application is running successfully.v1",
                "version", "1.0.0"
        )
    }
}
