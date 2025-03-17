package com.wtl.collab.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminTest(@RequestParam String testString){
        return ResponseEntity.ok(testString);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user")
    public ResponseEntity<String> userTest(@RequestParam String testString){
        return ResponseEntity.ok(testString);
    }

}
