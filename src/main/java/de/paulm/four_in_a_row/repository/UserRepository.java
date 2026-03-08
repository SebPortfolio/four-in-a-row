package de.paulm.four_in_a_row.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.paulm.four_in_a_row.domain.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = { "banHistory" })
    @Query("SELECT DISTINCT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailEager(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "LEFT JOIN FETCH u.customPermissions " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithAuthorities(@Param("email") String email);
}
