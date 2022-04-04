package com.example.SoftbinatorProject.dtos;

import com.example.SoftbinatorProject.models.Organization;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProjectInfoDto {
    private String type;

    private Long id;

    private String name;

    private String description;

    private List<PostDto> posts;

    public ProjectInfoDto(Class<?> type, Long id, String name, String description) {
        this.type = type.getAnnotation(DiscriminatorValue.class).value();
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
