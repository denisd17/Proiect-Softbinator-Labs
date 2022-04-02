package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.CommentDto;
import com.example.SoftbinatorProject.models.Comment;
import com.example.SoftbinatorProject.models.Post;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.CommentRepository;
import com.example.SoftbinatorProject.repositories.PostRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, KeycloakAdminService keycloakAdminService, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.userRepository = userRepository;
    }

    public CommentDto createComment(CommentDto commentDto, Long postId, Long uid) {
        //TODO: Check if exists
        Post post = postRepository.getById(postId);
        User user = userRepository.getById(uid);

        Comment newComment = Comment.builder()
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(newComment);

        return CommentDto.builder()
                .id(newComment.getId())
                .content(newComment.getContent())
                .uid(user.getId())
                .build();
    }

    /*
    public CommentDto getComment(Long postId, Long commentId) {

    }*/

    public List<CommentDto> getComments(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post does not exist!"));

        List<CommentDto> commentDtos = new ArrayList<>();

        for(Comment comment : comments) {
            commentDtos.add(CommentDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .uid(comment.getUser().getId())
                    .build());
        }

        return commentDtos;

    }

    public CommentDto updateComment(CommentDto commentDto, Long postId, Long commentId, Long uid) {
        Comment comment = commentRepository.findById(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post or comment do not exist!"));

        //TODO: Also allow app admin
        if(comment.getUser().getId().equals(uid)) {
            comment.setContent(commentDto.getContent());
            commentRepository.save(comment);

            return CommentDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .uid(comment.getUser().getId())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this comment!");
    }

    public void deleteComment(Long postId, Long commentId, Long uid, Set<String> roles) {
        Comment comment = commentRepository.findById(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post or comment do not exist!"));

        //TODO: Also allow app admin
        if(comment.getUser().getId().equals(uid)) {
            commentRepository.delete(comment);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this comment!");
        }
    }
}
