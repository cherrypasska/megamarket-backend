package backend.megamarket.service.orderservice.services;

import backend.megamarket.service.orderservice.dtos.JwtAuthenticationResponse;
import backend.megamarket.service.orderservice.dtos.RefreshRequestDto;
import backend.megamarket.service.orderservice.dtos.SignInRequestDto;
import backend.megamarket.service.orderservice.dtos.SignUpRequestDto;

public interface AuthenticationService {

    JwtAuthenticationResponse signUp(SignUpRequestDto request);

    JwtAuthenticationResponse signIn(SignInRequestDto request);

    JwtAuthenticationResponse refresh(RefreshRequestDto request);
}
