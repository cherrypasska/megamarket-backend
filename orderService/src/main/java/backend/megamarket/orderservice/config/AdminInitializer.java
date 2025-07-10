package backend.megamarket.orderservice.config;

import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.entity.enums.Role;
import backend.megamarket.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Инициализирует данные при запуске приложения.
 * Создаёт пользователя-админа, если его ещё нет.
 */
@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            String adminUsername = "admin";
            String adminEmail = "admin@megamarket.com";

            if (userRepository.existsByUsername(adminUsername)) {
                return;
            }

            UserEntity admin = UserEntity.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userRepository.save(admin);

            System.out.println("Администратор создан");
        };
    }
}
