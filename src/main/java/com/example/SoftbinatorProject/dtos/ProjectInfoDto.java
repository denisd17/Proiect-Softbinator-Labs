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
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = EventInfoDto.class, name = "Event"),
//        @JsonSubTypes.Type(value = FundraiserInfoDto.class, name = "Fundraiser")
//})
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
