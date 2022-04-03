package com.example.SoftbinatorProject.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("event")
public class Event extends Project{
    private Double ticketPrice;

    private Integer ticketAmount;

    private Integer extraTicketsSold;

    private Double extraAmount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private List<Ticket> tickets;

    public void addExtra(Integer extraTicketsSold, Double extraAmount) {
        this.extraTicketsSold += extraTicketsSold;
        this.extraAmount += extraAmount * extraTicketsSold;
    }
}
