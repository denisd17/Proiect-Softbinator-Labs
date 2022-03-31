package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class FundraiserInfoDto extends ProjectInfoDto{
    private Double goal;

    private Double moneyRaised;
}
