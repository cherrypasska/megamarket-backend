package backend.megamarket.orderservice.service;

import backend.megamarket.orderservice.dto.JwtAuthenticationResponse;
import backend.megamarket.orderservice.dto.RefreshRequestDto;
import backend.megamarket.orderservice.dto.SignInRequestDto;
import backend.megamarket.orderservice.dto.SignUpRequestDto;

public interface AuthenticationService {

    JwtAuthenticationResponse signUp(SignUpRequestDto request);

    JwtAuthenticationResponse signIn(SignInRequestDto request);

    JwtAuthenticationResponse refresh(RefreshRequestDto request);
}
