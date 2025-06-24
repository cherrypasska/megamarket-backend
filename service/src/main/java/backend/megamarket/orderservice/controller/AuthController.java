package backend.megamarket.service.orderservice.controller;

import backend.megamarket.service.orderservice.dto.JwtAuthenticationResponse;
import backend.megamarket.service.orderservice.dto.RefreshRequestDto;
import backend.megamarket.service.orderservice.dto.SignInRequestDto;
import backend.megamarket.service.orderservice.dto.SignUpRequestDto;
import backend.megamarket.service.orderservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки операций аутентификации пользователей.
 * Предоставляет эндпоинты для регистрации, входа в систему и обновления токена.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Регистрирует нового пользователя на основе переданных данных.
     *
     * @param request DTO с информацией для регистрации пользователя
     * @return {@link JwtAuthenticationResponse} — объект, содержащий JWT access/refresh токены
     */
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/reg")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequestDto request) {
        return authenticationService.signUp(request);
    }

    /**
     * Аутентифицирует пользователя на основе логина и пароля.
     *
     * @param request DTO с учетными данными пользователя
     * @return {@link JwtAuthenticationResponse} — объект с новыми JWT access/refresh токенами
     */
    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequestDto request) {
        return authenticationService.signIn(request);
    }

    /**
     * Обновляет access-токен по действующему refresh-токену.
     *
     * @param request DTO с refresh-токеном
     * @return {@link JwtAuthenticationResponse} — новый access/refresh токен
     */
    @Operation(summary = "Обновление информации пользователя")
    @PostMapping("/refresh")
    public JwtAuthenticationResponse refresh(@RequestBody @Valid RefreshRequestDto request) {
        return authenticationService.refresh(request);
    }
}
