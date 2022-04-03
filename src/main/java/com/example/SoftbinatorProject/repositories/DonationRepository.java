package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.DonationDto;
import com.example.SoftbinatorProject.dtos.DonationInfoDto;
import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.models.Donation;
import com.example.SoftbinatorProject.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Query("SELECT coalesce(SUM(d.amount), 0) from Donation d where d.fundraiser.id = :id")
    Double getRaisedAmount(Long id);

    @Query("SELECT new com.example.SoftbinatorProject.dtos.DonationInfoDto(d.id, d.amount, d.fundraiser.id, d.receiptUrl) from Donation d " +
    "WHERE d.user.id = :uid")
    List<DonationInfoDto> getDonationDtos(Long uid);


}
