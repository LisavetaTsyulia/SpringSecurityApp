package net.proselyte.springsecurityapp.service;

import net.proselyte.springsecurityapp.model.User;

import java.util.List;

public interface UserService {

    void save(User user);

    User findByUsername(String username);

    User findById(Long id);

    List<User> getAllUsers();

    void delete(Long id);

    void block(Long id);
}
