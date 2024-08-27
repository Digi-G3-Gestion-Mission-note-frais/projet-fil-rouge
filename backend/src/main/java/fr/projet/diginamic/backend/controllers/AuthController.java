package fr.projet.diginamic.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.diginamic.backend.dtos.LoginDto;
import fr.projet.diginamic.backend.dtos.RegisterDto;
import fr.projet.diginamic.backend.dtos.user.UserDto;
import fr.projet.diginamic.backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Auth controller for users */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API for user management")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * Route for login with email and password
     * 
     * @param loginDto - the dto to login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String userDto = authService.login(loginDto);

        if (userDto == null) {
            return new ResponseEntity<>(new UsernameNotFoundException("Email/Password not valid").getMessage(),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userDto);
    }

    /**
     * Route for registering a new user
     * 
     * @param registerDto - the dto to register a new user
     */
    @Operation(summary = "Register a new user", description = "Register a new user in the system")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        String token = authService.register(registerDto);

        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(token);
    }

    /**
     * Route for get current user
     * 
     * @param token - the access token
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // remove "Bearer " from the token
        token = token.substring(7);
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDto currentUser = authService.getCurrentUser(token);

        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(currentUser);
    }

    /**
     * Route for refresh token
     * 
     * @param oldToken - the token to refresh
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String oldToken) {
        oldToken = oldToken.substring(7);
        if (oldToken == null || oldToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String newToken = authService.refreshToken(oldToken);

        if (newToken == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(newToken);
    }

    /** Route for check user authorization */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    public ResponseEntity<String> checkUser() {
        return ResponseEntity.ok("OK");
    }

    /** Route for check manager authorization */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/manager")
    public ResponseEntity<String> checkManager() {
        return ResponseEntity.ok("OK");
    }

    /** Route for check admin authorization */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin")
    public ResponseEntity<String> checkAdmin() {
        return ResponseEntity.ok("OK");
    }

}
