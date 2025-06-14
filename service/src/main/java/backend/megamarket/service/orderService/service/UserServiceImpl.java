package backend.megamarket.service.orderservice.service;

import backend.megamarket.service.orderservice.repository.UserRepository;
import backend.megamarket.service.orderservice.entity.UserEntity;
import backend.megamarket.service.orderservice.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    @Override
    public UserEntity save(UserEntity user) {
        return repository.save(user);
    }

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Override
    public UserEntity create(UserEntity user) {
        if (repository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        return save(user);
    }

    /**
     * Обновление данных пользователя
     */
    @Override
    public void updateUser(UserEntity user, UserEntity newUser) {
        //Логика для проверки, что пользователь изменяет СВОи логин и пароль.
        if (repository.existsByEmailAndPassword(user.getEmail(), user.getPassword())) {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setUsername(newUser.getUsername());
            save(user);
        } else throw new RuntimeException("Неверный логин или пароль");
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    @Override
    public UserEntity getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    @Override
    public UserEntity getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    /**
     * Выдача прав администратора текущему пользователю
     * <p>
     * Нужен для демонстрации
     */
    @Deprecated
    @Override
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }

    /**
     * Функции для роли ADMIN
     * <p>
     * Просмотреть всю таблицу users
     */
    @Override
    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Просмотреть user по id
     */
    @Override
    public UserEntity getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * изменить user по id
     */
    @Override
    public UserEntity updateUserById(Long id, UserEntity updatedUser) {
        UserEntity existing = getUserById(id);

        if (!existing.getEmail().equals(updatedUser.getEmail())
                && repository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email уже используется");
        }

        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());

        return save(existing);
    }

    /**
     * удалить user по id
     */
    @Override
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Пользователь не найден");
        }
        repository.deleteById(id);
    }
}