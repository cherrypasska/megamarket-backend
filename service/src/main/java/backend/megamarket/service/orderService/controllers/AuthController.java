package backend.megamarket.service.orderService.controllers;

import backend.megamarket.service.orderService.dbs.dao.JwtAuthenticationResponse;
import backend.megamarket.service.orderService.dbs.dao.RefreshRequest;
import backend.megamarket.service.orderService.dbs.dao.SignInRequest;
import backend.megamarket.service.orderService.dbs.dao.SignUpRequest;
import backend.megamarket.service.orderService.servise.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/reg")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Обновление информации пользователя")
    @PostMapping("/refresh")
    public JwtAuthenticationResponse refresh(@RequestBody @Valid RefreshRequest request) {
        return authenticationService.refresh(request);
    }
}
