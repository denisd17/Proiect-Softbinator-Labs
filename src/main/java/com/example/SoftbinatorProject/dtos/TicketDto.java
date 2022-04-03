package com.example.SoftbinatorProject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TicketDto {
    private Long id;

    private Integer amount;

    private Double ticketPrice;

    private Long eventId;

    private Long userId;

    private String eventName;

    private String username;

    private String receiptUrl;
}
