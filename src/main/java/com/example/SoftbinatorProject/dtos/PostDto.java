package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostDto {
    private Long id;

    private String title;

    private String content;

    private List<CommentDto> comments;

    public PostDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
