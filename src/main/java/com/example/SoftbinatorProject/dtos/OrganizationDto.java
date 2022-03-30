package com.example.SoftbinatorProject.dtos;

import com.example.SoftbinatorProject.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrganizationDto {
    private String name;

    private String description;

    private User user;
}