package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.PostDto;
import com.example.SoftbinatorProject.models.Post;
import com.example.SoftbinatorProject.models.Project;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.PostRepository;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
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

    @Autowired
    public PostService(PostRepository postRepository, ProjectRepository projectRepository, KeycloakAdminService keycloakAdminService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.projectRepository = projectRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.userRepository = userRepository;
    }

    public PostDto createPost(PostDto postDto, Long projectId, Long uid, Set<String> roles) {
        //TODO: Check if project / user exist
        Project project = projectRepository.getById(projectId);
        User user = userRepository.getById(uid);

        if(project.getOrganization().getModerators().contains(user) || project.getOrganization().getUser().getId().equals(uid)) {
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
        //TODO: check if exists
        Post post = postRepository.findById(postId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or post do not exist!"));

        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public List<PostDto> getPosts(Long projectId) {
        List<Post> posts = postRepository.findAllByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or post do not exist!"));

        List<PostDto> postDtos = new ArrayList<>();

        for(Post post : posts) {
            postDtos.add(PostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .build());
        }

        return postDtos;
    }

    public PostDto updatePost(PostDto postDto, Long projectId, Long postId, Long uid, Set<String> roles) {
        Post post = postRepository.findById(postId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or post do not exist!"));

        Project project = projectRepository.getById(projectId);
        User user = userRepository.getById(uid);

        //TODO: Also allow app admin
        if(project.getOrganization().getModerators().contains(user) || project.getOrganization().getUser().getId().equals(uid)) {
            post.setTitle(postDto.getTitle());
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

        //TODO: Also allow app admin
        if(project.getOrganization().getModerators().contains(user) || project.getOrganization().getUser().getId().equals(uid)) {
            postRepository.delete(post);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this post!");
        }
    }
}
