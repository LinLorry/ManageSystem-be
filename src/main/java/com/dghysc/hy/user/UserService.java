package com.dghysc.hy.user;

import com.dghysc.hy.until.MD5Tool;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean addUser(String username, String name, String password) {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return false;
        }
        user = new User();

        String hash = MD5Tool.encode(password);

        user.setUsername(username);
        user.setName(name);
        user.setPassword(hash);

        userRepository.save(user);

        return true;
    }

    public boolean checkPassword(User user, String password) {
        return user.getPassword().compareTo(MD5Tool.encode(password)) == 0;
    }

    public User getUser(String username) {
        return userRepository.findUserByUsername(username);
    }
}
