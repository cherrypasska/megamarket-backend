package backend.megamarket.orderservice;

import backend.megamarket.orderservice.dto.JwtAuthenticationResponse;
import backend.megamarket.orderservice.dto.RefreshRequestDto;
import backend.megamarket.orderservice.dto.SignInRequestDto;
import backend.megamarket.orderservice.dto.SignUpRequestDto;
import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.entity.enums.Role;
import backend.megamarket.orderservice.service.AuthenticationServiceImpl;
import backend.megamarket.orderservice.service.JwtService;
import backend.megamarket.orderservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Captor
    private ArgumentCaptor<UserEntity> oldUserCaptor;

    @Captor
    private ArgumentCaptor<UserEntity> newUserCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void signUp_shouldCreateUserAndReturnToken() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password");
        String encodedPassword = "encodedPassword";
        UserEntity createdUser = UserEntity.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password(encodedPassword)
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        when(jwtService.generateToken(any())).thenReturn("mockedToken");

        JwtAuthenticationResponse response = authenticationService.signUp(request);

        verify(userService).create(argThat(user ->
                user.getUsername().equals("newuser") &&
                        user.getEmail().equals("newuser@example.com") &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getRole() == Role.ROLE_USER
        ));

        assertEquals("mockedToken", response.getToken());
    }

    @Test
    void signIn_shouldAuthenticateAndReturnToken() {
        SignInRequestDto request = new SignInRequestDto();
        request.setUsername("testuser");
        request.setPassword("password");

        UserEntity userDetails = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(userService.userDetailsService()).thenReturn(username -> userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("signedInToken");

        JwtAuthenticationResponse response = authenticationService.signIn(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        assertEquals("signedInToken", response.getToken());
    }

    @Test
    void refresh_shouldUpdateUserAndReturnToken() {
        RefreshRequestDto request = new RefreshRequestDto();
        request.setPassword("oldPass");
        request.setNewPassword("newPass");
        request.setEmail("olduser@example.com");
        request.setNewEmail("newuser@example.com");
        request.setUsername("olduser");
        request.setNewUsername("newuser");

        when(passwordEncoder.encode("oldPass")).thenReturn("encodedOld");
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");
        when(jwtService.generateToken(any())).thenReturn("refreshedToken");

        JwtAuthenticationResponse response = authenticationService.refresh(request);

        verify(userService).updateUser(oldUserCaptor.capture(), newUserCaptor.capture());

        UserEntity capturedOld = oldUserCaptor.getValue();
        UserEntity capturedNew = newUserCaptor.getValue();

        assertEquals("olduser", capturedOld.getUsername());
        assertEquals("olduser@example.com", capturedOld.getEmail()); // ✔️ исправлено
        assertEquals("encodedOld", capturedOld.getPassword());

        assertEquals("newuser", capturedNew.getUsername());
        assertEquals("newuser@example.com", capturedNew.getEmail());
        assertEquals("encodedNew", capturedNew.getPassword());

    }
}
