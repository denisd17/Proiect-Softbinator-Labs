package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class EventInfoDto extends ProjectInfoDto{
    private Long ticketPrice;

    private Integer ticketAmount;

    private Integer ticketsSold;
}
