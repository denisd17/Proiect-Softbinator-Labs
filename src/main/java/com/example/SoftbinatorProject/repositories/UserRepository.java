package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.dtos.UserInfoDto;
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

    @Query("SELECT new com.example.SoftbinatorProject.dtos.UserInfoDto(u.id, u.firstName, u.lastName, u.email, u.username, u.moneyBalance, u.profilePicUrl) from User u " +
            "WHERE u.id = :id")
    Optional<UserInfoDto> getUserDtoById(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.UserInfoDto(u.id, u.firstName, u.lastName, u.email, u.username, u.moneyBalance, u.profilePicUrl) from User u")
    List<UserInfoDto> getUserDtos();
}
