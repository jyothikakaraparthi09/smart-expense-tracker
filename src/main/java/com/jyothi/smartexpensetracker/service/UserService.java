package com.jyothi.smartexpensetracker.service;

import com.jyothi.smartexpensetracker.dto.LoginRequest;
import com.jyothi.smartexpensetracker.entity.User;
import com.jyothi.smartexpensetracker.repository.UserRepository;
import com.jyothi.smartexpensetracker.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepository repository;
    private final Logger log = Logger.getLogger(UserService.class.getName());

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public boolean validateUser(LoginRequest request)
    {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken( request.username(),request.password()));

        return authentication.isAuthenticated();
    }

    public String generateToken(LoginRequest request)
    {
        if(!validateUser(request))
        {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return jwtUtil.generateToken(request.username());
    }

    public String registerUser(LoginRequest request){

        log.info("Registering user");
        repository.save(new User(request.username(), passwordEncoder.encode(request.password())));

        return "User registered successfully";
    }
}
