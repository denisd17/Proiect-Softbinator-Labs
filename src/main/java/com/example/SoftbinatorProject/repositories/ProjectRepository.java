package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.dtos.ProjectInfoDto;
import com.example.SoftbinatorProject.models.Project;
import com.example.SoftbinatorProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findById(Long id);

    @Query("SELECT p from Project p where p.organization.id = :id")
    Optional<List<Project>> findAllByOrganizationId(Long id);

    @Query("SELECT p from Project p where p.organization.id = :organizationId and p.id = :id")
    Optional<Project> findById(Long id, Long organizationId);

    @Query("SELECT 1 from Project p where p.id = :id")
    Optional<Integer> checkIfProjectExists(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.ProjectInfoDto(TYPE(p), p.id, p.name, p.description) from Project p " +
            "WHERE p.organization.id = :id")
    List<ProjectInfoDto> getProjectsDtoByOrgId(Long id);
}
