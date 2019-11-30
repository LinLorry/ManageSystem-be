package com.dghysc.hy.user;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.NoSuchElementException;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Value("${manage.secret.password}")
    private String salt;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    User add(User user) {
        String hash = encoder.encode(salt + user.getPassword().trim() + salt);
        user.setPassword(hash);

        return userRepository.save(user);
    }

    boolean checkByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    User update(User user) {
        return userRepository.save(user);
    }

    void updatePassword(User user) {
        String hash = encoder.encode(salt + user.getPassword().trim() + salt);
        user.setPassword(hash);

        userRepository.save(user);
    }

    boolean checkPassword(User user, String password) {
        return encoder.matches(salt + password + salt, user.getPassword());
    }

    User loadById(BigInteger id) throws NoSuchElementException {
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exits."));
    }
}
