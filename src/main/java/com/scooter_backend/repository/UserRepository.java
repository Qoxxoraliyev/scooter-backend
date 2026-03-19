package com.scooter_backend.repository;

import com.scooter_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByPhone(String phone);

    Optional<User> findByPhone(String phone);
}
