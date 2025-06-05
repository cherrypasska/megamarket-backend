/*package backend.megamarket.service.controllers;

import backend.megamarket.service.db.DAO.userRepository;
import backend.megamarket.service.db.model.User;
import backend.megamarket.service.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final userRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/reg")
    public String register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already in use";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserRole("USER");

        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return "Invalid credentials";
        }

        UserDetails userDetails = userRepository.findByUsername(request.getUsername())
                .map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getUsername())
                        .password(u.getUserPassword())
                        .authorities(u.getUserRole())
                        .build())
                .orElseThrow();

        return jwtUtil.generateToken(userDetails);
    }

    @Getter
    @Setter
    static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Getter
    @Setter
    static class LoginRequest {
        private String username;
        private String password;
    }
}*/
