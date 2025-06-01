package org.example.taskapp.Controllers;

import org.example.taskapp.Components.JwtCore;
import org.example.taskapp.DTO.LoginRequest;
import org.example.taskapp.DTO.LoginResponse;
import org.example.taskapp.DTO.RefreshTokenRequest;
import org.example.taskapp.DTO.RegisterRequest;
import org.example.taskapp.Entity.User;
import org.example.taskapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    @Autowired
    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        if(userRepository.existsUserByUsername(registerRequest.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this username is already exists, choose another one");
        }
        if (userRepository.existsUserByEmail(registerRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this email is already exists, choose another one");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return ResponseEntity.ok("Registration successful!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtCore.generateToken(authentication);
            String refreshToken = jwtCore.generateRefreshToken(loginRequest.getUsername());
            long accessExpiration = jwtCore.getExpirationTime();

            return ResponseEntity.ok(new LoginResponse(true, accessToken, accessExpiration,
                    refreshToken, loginRequest.getUsername(), "Login successful!"));
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, null, 0, null, null, "Invalid username or password"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        if (!jwtCore.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String username = jwtCore.getUsernameFromToken(refreshToken);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String newAccessToken = jwtCore.generateTokenFromUsername(username);
        long newExpiration = jwtCore.getExpirationTime();

        return ResponseEntity.ok(new LoginResponse(true, newAccessToken, newExpiration,
                refreshToken, username, "Token refreshed"));
    }
}
