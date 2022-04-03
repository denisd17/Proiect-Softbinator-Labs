package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT coalesce(SUM(t.amount * t.price), 0) from Ticket t where t.event.id = :id")
    Double getRaisedAmount(Long id);

    @Query("SELECT coalesce(SUM(t.amount), 0) from Ticket t where t.event.id = :id")
    Integer getTicketsSold(Long id);
}
