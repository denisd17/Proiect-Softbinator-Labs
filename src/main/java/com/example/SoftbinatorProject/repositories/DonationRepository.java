package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.models.Donation;
import com.example.SoftbinatorProject.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Query("SELECT p from Project p where p.organization.id = :id")
    Optional<List<Project>> findAllByOrganizationId(Long id);

    @Query("SELECT SUM(d.amount) from Donation d where d.fundraiser.id = :id")
    Double getRaisedAmount(Long id);


}
