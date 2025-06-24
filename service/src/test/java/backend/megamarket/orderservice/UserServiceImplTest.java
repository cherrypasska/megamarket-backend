package backend.megamarket.service;

import backend.megamarket.service.orderservice.entity.UserEntity;
import backend.megamarket.service.orderservice.entity.enums.Role;
import backend.megamarket.service.orderservice.repository.UserRepository;
import backend.megamarket.service.orderservice.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity createUser(Long id, String username, String email) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        return user;
    }

    @Test
    void save_ShouldReturnSavedUser() {
        // Arrange
        UserEntity user = createUser(1L, "testUser", "test@example.com");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserEntity result = userService.save(user);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void create_ShouldCreateNewUser() {
        // Arrange
        UserEntity newUser = createUser(null, "newUser", "new@example.com");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        // Act
        UserEntity result = userService.create(newUser);

        // Assert
        assertNotNull(result);
        assertEquals(newUser, result);
        verify(userRepository).save(newUser);
    }

    @Test
    void create_ShouldThrowWhenUsernameExists() {
        // Arrange
        UserEntity existingUser = createUser(1L, "existing", "existing@example.com");
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.create(existingUser));
        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowWhenEmailExists() {
        // Arrange
        UserEntity existingUser = createUser(1L, "newUser", "existing@example.com");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.create(existingUser));
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateWhenCredentialsMatch() {
        // Arrange
        UserEntity original = createUser(1L, "original", "original@example.com");
        UserEntity updated = createUser(1L, "updated", "updated@example.com");
        updated.setPassword("newPassword");

        when(userRepository.existsByEmailAndPassword(
                "original@example.com", "password")).thenReturn(true);
        when(userRepository.save(any())).thenReturn(original);

        // Act
        userService.updateUser(original, updated);

        // Assert
        verify(userRepository).save(original);
        assertEquals("updated", original.getUsername());
        assertEquals("updated@example.com", original.getEmail());
        assertEquals("newPassword", original.getPassword());
    }

    @Test
    void updateUser_ShouldThrowWhenCredentialsDontMatch() {
        // Arrange
        UserEntity original = createUser(1L, "original", "original@example.com");
        UserEntity updated = createUser(1L, "updated", "updated@example.com");

        when(userRepository.existsByEmailAndPassword(
                "original@example.com", "password")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUser(original, updated));
        assertEquals("Неверный логин или пароль", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getByUsername_ShouldReturnUser() {
        // Arrange
        UserEntity user = createUser(1L, "testUser", "test@example.com");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        UserEntity result = userService.getByUsername("testUser");

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getByUsername_ShouldThrowWhenNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getByUsername("unknown"));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void userDetailsService_ShouldLoadByUsername() {
        // Arrange
        UserEntity user = createUser(1L, "testUser", "test@example.com");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("currentUser");

        UserEntity user = createUser(1L, "currentUser", "current@example.com");
        when(userRepository.findByUsername("currentUser")).thenReturn(Optional.of(user));

        // Act
        UserEntity result = userService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals("currentUser", result.getUsername());
    }

    @Test
    void getAdmin_ShouldGrantAdminRole() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("adminCandidate");

        UserEntity user = createUser(1L, "adminCandidate", "admin@example.com");
        when(userRepository.findByUsername("adminCandidate")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.getAdmin();

        // Assert
        assertEquals(Role.ROLE_ADMIN, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<UserEntity> users = List.of(
                createUser(1L, "user1", "user1@example.com"),
                createUser(2L, "user2", "user2@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserEntity> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        UserEntity user = createUser(1L, "testUser", "test@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserEntity result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getUserById_ShouldReturnNullWhenNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        UserEntity result = userService.getUserById(99L);

        // Assert
        assertNull(result);
    }

    @Test
    void updateUserById_ShouldUpdateUser() {
        // Arrange
        UserEntity existing = createUser(1L, "oldName", "old@example.com");
        UserEntity updated = createUser(1L, "newName", "new@example.com");
        updated.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(existing);

        // Act
        UserEntity result = userService.updateUserById(1L, updated);

        // Assert
        assertNotNull(result);
        assertEquals("newName", existing.getUsername());
        assertEquals("new@example.com", existing.getEmail());
        assertEquals("newPassword", existing.getPassword());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUserById_ShouldThrowWhenEmailExists() {
        // Arrange
        UserEntity existing = createUser(1L, "user", "user@example.com");
        UserEntity updated = createUser(1L, "user", "taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserById(1L, updated));
        assertEquals("Email уже используется", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteWhenExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowWhenNotFound() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(99L));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }
}