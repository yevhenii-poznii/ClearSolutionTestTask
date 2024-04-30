package com.kiskee.users.repository;

import com.kiskee.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    List<User> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);
}
