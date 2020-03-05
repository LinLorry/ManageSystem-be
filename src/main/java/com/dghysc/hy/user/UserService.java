package com.dghysc.hy.user;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * User Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class UserService {

    @Value("${manage.secret.password}")
    private String salt;

    private final UserRepository userRepository;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Add User Service
     * @param user the new user have raw password.
     * @return new user.
     */
    User add(User user) {
        String hash = encoder.encode(salt + user.getPassword().trim() + salt);
        user.setPassword(hash);

        return userRepository.save(user);
    }

    /**
     * Check User Exists By Username
     * @param username the username
     * @return if user exists return true else return false.
     */
    boolean checkByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Update User
     * @param user the user will be update.
     */
    User update(User user) {
        return userRepository.save(user);
    }

    /**
     * Check User Password
     * @param user be checked user.
     * @param password be checked password.
     * @return if password correct return true else return false.
     */
    boolean checkPassword(User user, String password) {
        return encoder.matches(salt + password + salt, user.getPassword());
    }

    /**
     * Update User Password
     * @param user the user with new raw password.
     */
    void updatePassword(User user) {
        String hash = encoder.encode(salt + user.getPassword().trim() + salt);
        user.setPassword(hash);

        userRepository.save(user);
    }

    /**
     * Load User By Id
     * @param id the user id.
     * @return the user.
     * @throws NoSuchElementException if the user is not exits throw this exception.
     */
    public User loadById(Long id) throws NoSuchElementException {
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Load User By Username.
     * @param username the username.
     * @return the user.
     * @throws NoSuchElementException if the user is not exits throw this exception.
     */
    User loadByUsername(String username) throws NoSuchElementException {
        return userRepository.findByUsername(username)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Get User Authentication Service
     * @param id the user id.
     * @return the user authentication.
     * @throws NullPointerException {@code id} is {@literal null}.
     * @throws DisabledException user is disable.
     */
    @Transactional(readOnly = true)
    public UsernamePasswordAuthenticationToken getAuthentication(@NotNull long id) {
        User user = userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        if (user.isEnabled()) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } else {
            throw new DisabledException("User: " + user.getUsername() + " is disabled");
        }
    }

    /**
     * Enable User Service
     * @param id the user id.
     * @throws NullPointerException {@code id} is {@literal null}.
     */
    public void enable(@NotNull Long id) {
        User user = userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        user.setDisable(false);

        userRepository.save(user);
    }

    /**
     * Disable User Service
     * @param id the user id.
     * @throws NullPointerException {@code id} is {@literal null}.
     */
    public void disable(@NotNull Long id) {
        User user = userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        user.setDisable(true);

        userRepository.save(user);
    }
}
