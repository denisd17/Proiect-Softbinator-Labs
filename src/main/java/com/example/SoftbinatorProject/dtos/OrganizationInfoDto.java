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
public class OrganizationInfoDto {
    private Long id;

    private String name;

    private String description;

    private String organizationOwner;

    private List<ProjectInfoDto> projects;

    public OrganizationInfoDto(Long id, String name, String description, String organizationOwner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.organizationOwner = organizationOwner;
    }
}
