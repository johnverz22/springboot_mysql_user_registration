package com.johnverz.db_user_registration.repository;

import com.johnverz.db_user_registration.entity.Database;
import com.johnverz.db_user_registration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    List<Database> findByUser(User user);
    Optional<Database> findByNameAndUser(String name, User user);
    boolean existsByNameAndUser(String name, User user);
}