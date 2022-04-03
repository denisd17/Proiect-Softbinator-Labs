package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    @Query("SELECT u from User u where u.email = :email or u.username = :username ")
    Optional<User> findByEmailOrUsername(String email, String username);

    @Query("SELECT u.id from User u where u.id <> :uid and u.email = :email or u.username = :username ")
    List<Long> findDifferentByEmailOrUsername(String email, String username, Long uid);
}
