package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p from Project p where p.organization.id = :id")
    Optional<List<Project>> findAllByOrganizationId(Long id);

    @Query("SELECT p from Project p where p.organization.id = :organizationId and p.id = :id")
    Optional<Project> findById(Long id, Long organizationId);

}
