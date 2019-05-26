package ru.kubsu.fs.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kubsu.fs.entity.User;

import java.util.List;

public interface UserRepositiry extends CrudRepository<User, Long> {
    List<User> findAll();
    User findUserByEmail(String email);
    User findUserByUserId(Long id);
}
