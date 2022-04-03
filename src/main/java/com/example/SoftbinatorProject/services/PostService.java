package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.PostDto;
import com.example.SoftbinatorProject.models.Post;
import com.example.SoftbinatorProject.models.Project;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.CommentRepository;
import com.example.SoftbinatorProject.repositories.PostRepository;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import com.example.SoftbinatorProject.utils.AccessUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ProjectRepository projectRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, ProjectRepository projectRepository, KeycloakAdminService keycloakAdminService, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.projectRepository = projectRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public PostDto createPost(PostDto postDto, Long projectId, Long uid, Set<String> roles) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project does not exist!"));

        User user = userRepository.getById(uid);

        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdminOrMod(project.getOrganization(), user)) {
            Post newPost = Post.builder()
                    .title(postDto.getTitle())
                    .content(postDto.getContent())
                    .project(project)
                    .build();

            postRepository.save(newPost);

            return PostDto.builder()
                    .id(newPost.getId())
                    .title(newPost.getTitle())
                    .content(newPost.getContent())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this project!");
    }

    public PostDto getPost(Long projectId, Long postId) {
        if(projectRepository.checkIfProjectExists(projectId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project does not exist");
        }

        PostDto postDto = postRepository.getPostDtoById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post does not exist"));

        postDto.setComments(commentRepository.getCommentDtosByPostId(postId));

        return postDto;
    }

    public List<PostDto> getPosts(Long projectId) {
        if(projectRepository.checkIfProjectExists(projectId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project does not exist");
        }

        return postRepository.getPostDtosByProjectId(projectId);
    }

    public PostDto updatePost(PostDto postDto, Long projectId, Long postId, Long uid, Set<String> roles) {
        Post post = postRepository.findById(postId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or post do not exist!"));

        Project project = projectRepository.getById(projectId);
        User user = userRepository.getById(uid);

        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdminOrMod(project.getOrganization(), user)) {
            if(postDto.getTitle() != null)
                post.setTitle(postDto.getTitle());
            if(postDto.getContent() != null)
                post.setContent(postDto.getContent());

            postRepository.save(post);

            return PostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this project!");
    }

    public void deletePost(Long projectId, Long postId, Long uid, Set<String> roles) {
        Post post = postRepository.findById(postId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or post do not exist!"));

        Project project = projectRepository.getById(projectId);
        User user = userRepository.getById(uid);

        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdminOrMod(project.getOrganization(), user)) {
            postRepository.delete(post);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this post!");
        }
    }
}
