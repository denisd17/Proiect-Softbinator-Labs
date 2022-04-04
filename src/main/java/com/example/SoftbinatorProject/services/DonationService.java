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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final AmazonService amazonService;
    private final MailService mailService;

    @Autowired
    public DonationService(DonationRepository donationRepository, ProjectRepository projectRepository, OrganizationRepository organizationRepository, UserRepository userRepository, AmazonService amazonService, MailService mailService) {
        this.donationRepository = donationRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.amazonService = amazonService;
        this.mailService = mailService;
    }

    public DonationDto donate(Long orgId, Long projectId, Long uid, DonationDto donationDto) throws FileNotFoundException, DocumentException {
        User user = userRepository.getById(uid);
        Project project = projectRepository.findById(projectId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or organization do not exist!"));

        if(donationDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid donation amount!");
        }
        else if(user.getMoneyBalance() - donationDto.getAmount() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have sufficient funds!");
        }
        else if(project.getDecriminatorValue().equals("event")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only donate to fundraisers!");
        }
        else {
            Fundraiser fundraiser = (Fundraiser) project;
            Donation donation = Donation.builder()
                    .amount(donationDto.getAmount())
                    .fundraiser(fundraiser)
                    .user(user)
                    .build();

            Long donId = donationRepository.save(donation).getId();

            // Actualizare fonduri utilizator
            Double newBalance = user.getMoneyBalance() - donation.getAmount();
            user.setMoneyBalance(newBalance);
            userRepository.save(user);

            // Generare factura donatie
            Map<String, String> receiptInfo = new HashMap<>();
            receiptInfo.put("NR", donId.toString());
            String currentDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
            receiptInfo.put("DATA", currentDate);
            receiptInfo.put("NUME", user.getFirstName() + "_" + user.getLastName());
            receiptInfo.put("NUME_ORGANIZATIE", fundraiser.getOrganization().getName());
            receiptInfo.put("NUME_PROIECT", fundraiser.getName());
            receiptInfo.put("NR_CRT", "1");
            receiptInfo.put("SERVICII", "Donatie");
            receiptInfo.put("CANTITATE", "1");
            receiptInfo.put("VALOARE", donation.getAmount().toString());
            receiptInfo.put("TOTAL", donation.getAmount().toString());

            String docName = ReceiptUtility.generateReceipt(receiptInfo);
            File file = new File(docName);
            String receiptUrl = amazonService.uploadFile("receipts",docName, file);
            mailService.sendReceiptEmail(user.getEmail(), docName, receiptUrl);

            donation.setReceiptUrl(receiptUrl);
            donationRepository.save(donation);

            return DonationDto.builder()
                    .id(donation.getId())
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
