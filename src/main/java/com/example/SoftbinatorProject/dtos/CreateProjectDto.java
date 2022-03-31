package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class CreateProjectDto {
    private String name;

    private String description;

    private Double ticketPrice;

    private Integer ticketAmount;

    private Integer ticketsSold;

    private Double goal;

    private Double moneyRaised;

    private String type;
}
