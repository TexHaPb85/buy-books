package com.ua.buybooks.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ua.buybooks.entity.FailureLog;

public interface FailureLogRepository extends JpaRepository<FailureLog, Long> {
}
