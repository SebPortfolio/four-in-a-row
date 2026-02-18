package de.paulm.four_in_a_row.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.paulm.four_in_a_row.domain.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
