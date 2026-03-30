package de.paulm.four_in_a_row.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.paulm.four_in_a_row.domain.security.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

}
