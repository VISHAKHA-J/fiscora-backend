package com.fiscora.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Temporary: proves the JWT filter works end to end. Delete once real endpoints exist.
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities().toString());
    }
}