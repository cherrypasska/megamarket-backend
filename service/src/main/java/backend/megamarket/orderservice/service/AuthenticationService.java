package backend.megamarket.service.orderservice.service;

import backend.megamarket.service.orderservice.dto.JwtAuthenticationResponse;
import backend.megamarket.service.orderservice.dto.RefreshRequestDto;
import backend.megamarket.service.orderservice.dto.SignInRequestDto;
import backend.megamarket.service.orderservice.dto.SignUpRequestDto;

public interface AuthenticationService {

    JwtAuthenticationResponse signUp(SignUpRequestDto request);

    JwtAuthenticationResponse signIn(SignInRequestDto request);

    JwtAuthenticationResponse refresh(RefreshRequestDto request);
}
