package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.dtos.PostDto;
import com.example.SoftbinatorProject.models.Post;
import com.example.SoftbinatorProject.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    @Query("SELECT p from Post p where p.project.id = :id")
    Optional<List<Post>> findAllByProjectId(Long id);

    @Query("SELECT p from Post p where p.project.id = :projectId and p.id = :id")
    Optional<Post> findById(Long id, Long projectId);

    @Query("SELECT 1 from Post p where p.id = :id")
    Optional<Integer> checkIfPostExists(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.PostDto(p.id, p.title, p.content) from Post p " +
            "WHERE p.id = :id")
    Optional<PostDto> getPostDtoById(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.PostDto(p.id, p.title, p.content) from Post p " +
            "WHERE p.project.id = :projectId")
    List<PostDto> getPostDtosByProjectId(Long projectId);
}
