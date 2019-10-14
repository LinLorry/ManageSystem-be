package com.dghysc.hy.user;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Value("${Manage.salt}")
    private String salt;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    boolean addUser(String username, String name, String password) {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return false;
        }
        String hash = encoder.encode(salt + password + salt);
        user = new User();

        user.setUsername(username);
        user.setName(name);
        user.setPassword(hash);

        userRepository.save(user);

        return true;
    }

    boolean checkPassword(User user, String password) {
        return encoder.matches(salt + password + salt, user.getPassword());
    }

    User getUser(String username) {
        return userRepository.findUserByUsername(username);
    }
}
