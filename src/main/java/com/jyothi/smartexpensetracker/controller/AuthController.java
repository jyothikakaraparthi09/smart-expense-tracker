package com.jyothi.smartexpensetracker.controller;

import com.jyothi.smartexpensetracker.dto.LoginRequest;
import com.jyothi.smartexpensetracker.entity.User;
import com.jyothi.smartexpensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger log = Logger.getLogger(AuthController.class.getName());
;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request){

        log.info("User: "+ request.username());

        String token = userService.generateToken(request);
        log.info("Generated Token: "+token);
        return token;
    }

    @PostMapping("/register")
    public String register(@RequestBody LoginRequest request){
        return userService.registerUser(request);
    }
}

