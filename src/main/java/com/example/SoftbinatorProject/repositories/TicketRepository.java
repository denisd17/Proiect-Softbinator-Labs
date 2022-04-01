package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT SUM(t.amount * t.price) from Ticket t where t.event.id = :id")
    Double getRaisedAmount(Long id);

    @Query("SELECT SUM(t.amount) from Ticket t where t.event.id = :id")
    Integer getTicketsSold(Long id);
}
