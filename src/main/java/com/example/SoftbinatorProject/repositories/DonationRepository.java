package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.models.Donation;
import com.example.SoftbinatorProject.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Query("SELECT SUM(d.amount) from Donation d where d.fundraiser.id = :id")
    Double getRaisedAmount(Long id);


}
