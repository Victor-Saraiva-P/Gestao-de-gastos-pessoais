package br.com.gestorfinanceiro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public Map<String, String> helloAllUsers() {
        Map<String, String> message = new HashMap<>();
        message.put("message", "Hello World!");
        return message;
    }

    @GetMapping("/admin")
    public Map<String, String> helloAdminUsers() {
        Map<String, String> message = new HashMap<>();
        message.put("message", "Hello Admin!");
        return message;
    }
}
