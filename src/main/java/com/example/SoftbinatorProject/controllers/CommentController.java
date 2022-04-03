package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.CommentDto;
import com.example.SoftbinatorProject.services.CommentService;
import com.example.SoftbinatorProject.utils.KeycloakHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.SoftbinatorProject.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("")
    public ResponseEntity<?> createComment(@PathVariable Long postId, @RequestBody CommentDto commentDto, Authentication authentication) {
        return new ResponseEntity<>(commentService.createComment(commentDto, postId, Long.parseLong(KeycloakHelper.getUser(authentication))), HttpStatus.OK);
    }

    /*@GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.getComment(postId, commentId), HttpStatus.OK);
    }*/

    @GetMapping("")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return new ResponseEntity<>(commentService.getComments(postId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        commentService.deleteComment(postId, commentId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication));
        return successResponse();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentDto commentDto, Authentication authentication) {
        return new ResponseEntity<>(commentService.updateComment(commentDto, postId, commentId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }
}
