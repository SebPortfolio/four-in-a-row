package de.paulm.four_in_a_row.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProjection;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "LEFT JOIN FETCH u.customPermissions " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithAuthorities(@Param("email") String email);

    @Query("SELECT new de.paulm.four_in_a_row.domain.security.UserProjection(u, p.displayName) FROM User u LEFT JOIN PlayerProfile p ON u.id = p.userId WHERE u.id = :id")
    Optional<UserProjection> findProjectionById(Long id);

    @Query("SELECT new de.paulm.four_in_a_row.domain.security.UserProjection(u, p.displayName) FROM User u LEFT JOIN PlayerProfile p ON u.id = p.userId")
    List<UserProjection> findAllProjections();
}
