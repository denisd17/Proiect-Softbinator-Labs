package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationInfoDto {
    private Long id;

    private Double amount;

    private Long projectId;

    private String receiptUrl;
}
