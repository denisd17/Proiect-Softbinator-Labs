package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.DonationInfoDto;
import com.example.SoftbinatorProject.dtos.TicketInfoDto;
import com.example.SoftbinatorProject.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT coalesce(SUM(t.amount * t.price), 0) from Ticket t where t.event.id = :id")
    Double getRaisedAmount(Long id);

    @Query("SELECT coalesce(SUM(t.amount), 0) from Ticket t where t.event.id = :id")
    Integer getTicketsSold(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.TicketInfoDto(t.id, t.amount, t.price, t.event.id, t.receiptUrl) from Ticket t " +
            "WHERE t.user.id = :uid")
    List<TicketInfoDto> getTicketDtos(Long uid);
}
