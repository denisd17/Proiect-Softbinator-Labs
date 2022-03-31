package com.example.SoftbinatorProject.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double amount;

    private String receiptUrl;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="fundraiser_id", nullable=false)
    private Fundraiser fundraiser;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
}
