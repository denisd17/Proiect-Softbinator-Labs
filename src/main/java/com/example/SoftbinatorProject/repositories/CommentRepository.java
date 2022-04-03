package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.CommentDto;
import com.example.SoftbinatorProject.dtos.PostDto;
import com.example.SoftbinatorProject.models.Comment;
import com.example.SoftbinatorProject.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c from Comment c where c.post.id = :id")
    Optional<List<Comment>> findAllByPostId(Long id);

    @Query("SELECT c from Comment c where c.post.id = :postId and c.id = :id")
    Optional<Comment> findById(Long id, Long postId);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.CommentDto(c.id, c.user.id, c.content) from Comment c " +
            "WHERE c.post.id = :postId")
    List<CommentDto> getCommentDtosByPostId(Long postId);
}
