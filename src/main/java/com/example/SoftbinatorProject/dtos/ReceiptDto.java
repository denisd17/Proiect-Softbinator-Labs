package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDto {
    private List<DonationInfoDto> donations;

    private List<TicketInfoDto> tickets;
}
