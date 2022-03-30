package com.example.SoftbinatorProject.dtos;

import com.example.SoftbinatorProject.models.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProjectInfoDto {
    private Long id;

    private String name;

    private String description;

    private Organization organization;
}
