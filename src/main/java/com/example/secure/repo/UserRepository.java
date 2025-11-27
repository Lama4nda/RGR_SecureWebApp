package com.example.secure.repo;

import com.example.secure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Метод для пошуку користувача за логіном
	// extends JpaRepository містить коди для пошуку, видаленні і тд.
    Optional<User> findByUsername(String username);
}
