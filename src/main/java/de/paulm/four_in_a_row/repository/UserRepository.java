package de.paulm.four_in_a_row.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.paulm.four_in_a_row.domain.security.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = { "roles", "customPermissions" })
    @Query("SELECT DISTINCT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndPermissions(Long id);

    @EntityGraph(attributePaths = { "roles", "customPermissions" })
    @Query("SELECT DISTINCT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = { "roles", "customPermissions" })
    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN :ids")
    List<User> findAllByIdInWithRolesAndPermissions(Collection<Long> ids);

    @EntityGraph(attributePaths = { "roles", "customPermissions" })
    @Query("SELECT DISTINCT u FROM User u")
    List<User> findAllWithRolesAndPermissions();
}
