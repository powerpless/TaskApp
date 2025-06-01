package org.example.taskapp.Repository;

import org.example.taskapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsUserByUsername(String username);

    Boolean existsUserByEmail(String email);
    Optional<User> findUserByUsername(String username);
}
