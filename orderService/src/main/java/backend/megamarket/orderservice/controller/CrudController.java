package backend.megamarket.orderservice.controller;

import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Контроллер для управления пользователями (CRUD-операции).
 * Доступ разрешён только администраторам.
 */
@RestController
@RequestMapping("/crud/users")
@RequiredArgsConstructor
public class CrudController {

    private final UserService userService;

    /**
     * Получает список всех зарегистрированных пользователей.
     *
     * @return {@link ResponseEntity} содержащий список пользователей {@link UserEntity}
     */
    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Получает информацию о пользователе по его уникальному идентификатору.
     *
     * @param id ID пользователя
     * @return {@link ResponseEntity} содержащий пользователя {@link UserEntity}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param user сущность пользователя, которую необходимо создать
     * @return {@link ResponseEntity} содержащий созданного пользователя {@link UserEntity}
     */
    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        return ResponseEntity.ok(userService.create(user));
    }

    /**
     * Обновляет информацию о пользователе по его ID.
     *
     * @param id ID пользователя
     * @param updatedUser обновлённые данные пользователя
     * @return {@link ResponseEntity} содержащий обновлённого пользователя {@link UserEntity}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserEntity updatedUser) {
        return ResponseEntity.ok(userService.updateUserById(id, updatedUser));
    }

    /**
     * Удаляет пользователя по его ID.
     *
     * @param id ID пользователя
     * @return {@link ResponseEntity} без содержимого (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
