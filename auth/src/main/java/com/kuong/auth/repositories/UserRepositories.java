package com.kuong.auth.repositories;

import com.kuong.auth.entities.User;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;


public interface UserRepositories extends CrudRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
