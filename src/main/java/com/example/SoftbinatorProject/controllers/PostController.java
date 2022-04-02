package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.PostDto;
import com.example.SoftbinatorProject.dtos.ProjectInfoDto;
import com.example.SoftbinatorProject.dtos.UpdateProjectDto;
import com.example.SoftbinatorProject.services.PostService;
import com.example.SoftbinatorProject.utils.KeycloakHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.SoftbinatorProject.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("/projects/{projectId}/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("")
    public ResponseEntity<?> createPost(@PathVariable Long projectId, @RequestBody PostDto postDto, Authentication authentication) {
        return new ResponseEntity<>(postService.createPost(postDto, projectId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long projectId, @PathVariable Long postId) {
        return new ResponseEntity<>(postService.getPost(projectId, postId), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable Long projectId) {
        return new ResponseEntity<>(postService.getPosts(projectId), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long projectId, @PathVariable Long postId, Authentication authentication) {
        postService.deletePost(projectId, postId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication));
        return successResponse();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long projectId, @PathVariable Long postId, @RequestBody PostDto PostDto, Authentication authentication) {
        return new ResponseEntity<>(postService.updatePost(PostDto, projectId, postId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }
}
