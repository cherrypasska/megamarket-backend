package backend.megamarket.service.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса на изменение данных пользователя.
 * <p>
 * Содержит текущие и новые значения полей пользователя, таких как имя, email и пароль.
 */
@Data
@Schema(description = "Запрос на изменение данных пользователя")
public class RefreshRequestDto {

    /**
     * Текущее имя пользователя и имя пользователя для обновления имени.
     */
    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(max = 50, message = "Имя пользователя должно содержать до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username, newUsername;

    /**
     * Текущий адрес электронной почты пользователя и адрес электронной почты для обновления.
     */
    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email, newEmail;

    /**
     * Текущий пароль пользователя и пароль для обновления пароля.
     */
    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password, newPassword;
}
