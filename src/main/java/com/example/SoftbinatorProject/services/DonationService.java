package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.DonationDto;
import com.example.SoftbinatorProject.models.Donation;
import com.example.SoftbinatorProject.models.Fundraiser;
import com.example.SoftbinatorProject.models.Project;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.DonationRepository;
import com.example.SoftbinatorProject.repositories.OrganizationRepository;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import com.example.SoftbinatorProject.utils.ReceiptUtility;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final AmazonService amazonService;

    @Autowired
    public DonationService(DonationRepository donationRepository, ProjectRepository projectRepository, OrganizationRepository organizationRepository, UserRepository userRepository, AmazonService amazonService) {
        this.donationRepository = donationRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.amazonService = amazonService;
    }

    public DonationDto donate(Long orgId, Long projectId, Long uid, DonationDto donationDto) throws FileNotFoundException, DocumentException {
        User user = userRepository.getById(uid);
        //TODO: check or else throw
        Project project = projectRepository.findById(projectId, orgId).orElseThrow();

        if(donationDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid donation amount!");
        }
        else if(user.getMoneyBalance() - donationDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have sufficient funds!");
        }
        else if(project.getDecriminatorValue().equals("event")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only donate to fundraisers!");
        }
        else {
            //TODO: check if project exists
            Fundraiser fundraiser = (Fundraiser) project;
            Donation donation = Donation.builder()
                    .amount(donationDto.getAmount())
                    .fundraiser(fundraiser)
                    .user(user)
                    .build();

            donationRepository.save(donation);

            // Actualizare fonduri utilizator
            Double newBalance = user.getMoneyBalance() - donation.getAmount();
            user.setMoneyBalance(newBalance);
            userRepository.save(user);

            // Generare factura donatie
            Map<String, String> receiptInfo = new HashMap<>();
            //TODO: Numar corect
            receiptInfo.put("NR", "1");
            //TODO: Data corecta
            receiptInfo.put("DATA", "DATA_CURENTA");
            receiptInfo.put("NUME", user.getFirstName() + user.getLastName());
            receiptInfo.put("NUME_ORGANIZATIE", fundraiser.getOrganization().getName());
            receiptInfo.put("NUME_PROIECT", fundraiser.getName());
            receiptInfo.put("NR_CRT", "1");
            receiptInfo.put("SERVICII", "Donatie");
            receiptInfo.put("CANTITATE", "1");
            receiptInfo.put("VALOARE", donation.getAmount().toString());
            receiptInfo.put("TOTAL", donation.getAmount().toString());

            String docName = ReceiptUtility.generateReceipt(receiptInfo);
            File file = new File(docName);
            String receiptUrl = amazonService.uploadFile(docName, file);

            return DonationDto.builder()
                    .amount(donation.getAmount())
                    .projectId(fundraiser.getId())
                    .projectName(fundraiser.getName())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .receiptUrl(receiptUrl)
                    .build();
        }

    }
}
