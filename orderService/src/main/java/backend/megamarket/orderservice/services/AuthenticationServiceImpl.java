package backend.megamarket.orderservice.services;

import backend.megamarket.orderservice.dto.JwtAuthenticationResponse;
import backend.megamarket.orderservice.dto.RefreshRequestDto;
import backend.megamarket.orderservice.dto.SignInRequestDto;
import backend.megamarket.orderservice.dto.SignUpRequestDto;

import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.mapper.UserMapper;
import backend.megamarket.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис аутентификации, реализующий регистрацию, вход и обновление данных пользователя.
 * <p>
 * Использует {@link UserService} для управления данными пользователей,
 * {@link JwtService} для генерации JWT токенов,
 * а также Spring Security {@link AuthenticationManager} для аутентификации.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository repository;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Override
    public JwtAuthenticationResponse signUp(SignUpRequestDto request) {

        var user = userMapper.signUpToEntity(request, passwordEncoder);

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Override
    public JwtAuthenticationResponse signIn(SignInRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    /**
     * Обновление данных пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Override
    public JwtAuthenticationResponse refresh(RefreshRequestDto request) {
        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        if (!(user instanceof UserEntity existingUser)) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Неверный логин или пароль");
        }

        var accessToken = jwtService.generateToken(existingUser);
        var refreshToken = jwtService.generateRefreshToken(existingUser);
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }
}