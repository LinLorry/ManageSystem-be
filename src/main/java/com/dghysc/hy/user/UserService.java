package com.dghysc.hy.user;

import com.dghysc.hy.until.MD5Tool;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Value("${Manage.salt}")
    private String salt;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    boolean addUser(String username, String name, String password) {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return false;
        }
        String hash = MD5Tool.encode(salt + password + salt);
        user = new User();

        user.setUsername(username);
        user.setName(name);
        user.setPassword(hash);

        userRepository.save(user);

        return true;
    }

    boolean checkPassword(User user, String password) {
        return user.getPassword().compareTo(
                MD5Tool.encode(salt + password + salt)) == 0;
    }

    User getUser(String username) {
        return userRepository.findUserByUsername(username);
    }
}
