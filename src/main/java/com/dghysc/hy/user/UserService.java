package com.dghysc.hy.user;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.SpecificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Map;
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
     * @param username the username.
     * @param password the password.
     * @param name the name.
     * @return the user.
     * @throws NullPointerException {@code username} or {@code password} is {@literal null}.
     * @throws DataIntegrityViolationException sql error.
     */
    User add(@NotNull String username, @NotNull String password, @Nullable String name) {
        User user = new User();

        Optional.of(username).ifPresent(user::setUsername);
        Optional.of(password).ifPresent(p ->
                user.setPassword(encoder.encode(salt + p + salt)));

        Optional.ofNullable(name).ifPresent(user::setName);

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
     * Update User Service
     * @param id the user id.
     * @param username the user username.
     * @param name the user name.
     * @return the user.
     * @throws NullPointerException {@code id} is {@literal null}.
     * @throws EntityNotFoundException if user not exist.
     * @throws DataIntegrityViolationException sql error.
     */
    User update(@NotNull Long id, @Nullable String username, @Nullable String name) {
        User user = userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(username).ifPresent(user::setUsername);
        Optional.ofNullable(name).ifPresent(user::setName);

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

    void updatePassword(@NotNull String password) {
        User user = userRepository.findById(Optional.of(SecurityUtil.getUserId()).get())
                .orElseThrow(EntityNotFoundException::new);

        String hash = encoder.encode(salt + Optional.of(password).get() + salt);
        user.setPassword(hash);

        userRepository.save(user);
    }

    /**
     * Update User Password Service
     * @param id the user id.
     * @param password the new password.
     * @throws NullPointerException {@code id} or {@code password} is {@literal null}.
     * @throws EntityNotFoundException if user not exist.
     */
    @PreAuthorize("hasRole('ADMIN')")
    void updatePassword(@NotNull Long id, @NotNull String password) {
        User user = userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        String hash = encoder.encode(salt + Optional.of(password).get() + salt);
        user.setPassword(hash);

        userRepository.save(user);
    }

    /**
     * Load Users Service
     * @param likeMap {
     *      "the user field": value will be equal by "%value%"
     * }
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return the users page.
     * @throws NullPointerException {@code likeMap} is {@literal null}
     */
    Page<User> load(@NotNull Map<String, Object> likeMap, int pageNumber, int pageSize) {

        SpecificationUtil specificationUtil = new SpecificationUtil();

        specificationUtil.addLikeMap(likeMap);

        return userRepository.findAll(specificationUtil.getSpecification(),
                PageRequest.of(pageNumber, pageSize));
    }

    /**
     * Load User By Id
     * @param id the user id.
     * @return the user.
     * @throws NullPointerException {@code id} is {@literal null}.
     * @throws EntityNotFoundException if the user is not exits throw this exception.
     */
    public User loadById(@NotNull Long id) {
        return userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Load User By Username.
     * @param username the username.
     * @return the user.
     * @throws NullPointerException {@code id} is {@literal null}.
     * @throws EntityNotFoundException if the user is not exits throw this exception.
     */
    User loadByUsername(String username) {
        return userRepository.findByUsername(Optional.of(username).get())
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public User loadWithRolesById(@NotNull Long id) {
        User user = userRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        user.getAuthorities().size();

        return user;
    }

    /**
     * Get User Authentication Service
     * @param id the user id.
     * @return the user authentication.
     * @throws NullPointerException {@code id} is {@literal null}.
     * @throws EntityNotFoundException if the user is not exits throw this exception.
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
