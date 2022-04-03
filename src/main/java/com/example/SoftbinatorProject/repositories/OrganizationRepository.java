package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findById(Long id);

    @Query("SELECT 1 from Organization o where o.id = :id")
    Optional<Integer> checkIfOrganizationExists(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.OrganizationInfoDto(o.id, o.name, o.description, o.user.username) from Organization o " +
    "WHERE o.id = :id")
    Optional<OrganizationInfoDto> getOrganizationDtoById(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.OrganizationInfoDto(o.id, o.name, o.description, o.user.username) from Organization o ")
    List<OrganizationInfoDto> getOrganizationDtos();
}
