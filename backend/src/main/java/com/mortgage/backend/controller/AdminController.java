package com.mortgage.mortgage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class AdminController {
    @PreAuthorize("hasRole('OFFICER')")
    @GetMapping("/officer")
    public ResponseEntity<String> helloAdmin(){
        return ResponseEntity.ok("Hello officer");
    }

    @PreAuthorize("hasRole('APPLICANT')")
    @GetMapping("/applicant")
    public ResponseEntity<String> helloUser(){
        return ResponseEntity.ok("Hello applicant");
    }
}
