package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.DonationDto;
import com.example.SoftbinatorProject.dtos.TicketDto;
import com.example.SoftbinatorProject.models.*;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import com.example.SoftbinatorProject.repositories.TicketRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import com.example.SoftbinatorProject.utils.ReceiptUtility;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AmazonService amazonService;
    private final MailService mailService;

    @Autowired
    public TicketService(TicketRepository ticketRepository, ProjectRepository projectRepository, UserRepository userRepository, AmazonService amazonService, MailService mailService) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.amazonService = amazonService;
        this.mailService = mailService;
    }

    public TicketDto purchase(Long orgId, Long projectId, Long uid, TicketDto ticketDto) throws FileNotFoundException, DocumentException {
        User user = userRepository.getById(uid);
        //TODO: check or else throw
        Project project = projectRepository.findById(projectId, orgId).orElseThrow();
        if(project.getDecriminatorValue().equals("fundraiser")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only purchase tickets from events!");
        }

        Event event = (Event) project;

        if(ticketDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket amount!");
        }
        else if(user.getMoneyBalance() - ticketDto.getAmount() * event.getTicketPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have sufficient funds!");
        }
        else if(event.getTicketAmount() - ticketDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough tickets are available!");
        }
        else {
            //TODO: check if project exists
            //TODO: save receipt url in model

            Ticket ticket = Ticket.builder()
                    .amount(ticketDto.getAmount())
                    .price(event.getTicketPrice())
                    .event(event)
                    .user(user)
                    .build();

            Long ticketId = ticketRepository.save(ticket).getId();

            // Actualizare fonduri utilizator
            Double newBalance = user.getMoneyBalance() - ticket.getAmount() * ticket.getPrice();
            user.setMoneyBalance(newBalance);
            userRepository.save(user);
            
            // Generare factura donatie
            Map<String, String> receiptInfo = new HashMap<>();
            receiptInfo.put("NR", ticketId.toString());
            String currentDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
            receiptInfo.put("DATA", currentDate);
            receiptInfo.put("NUME", user.getFirstName() + "_" + user.getLastName());
            receiptInfo.put("NUME_ORGANIZATIE", event.getOrganization().getName());
            receiptInfo.put("NUME_PROIECT", event.getName());
            receiptInfo.put("NR_CRT", "1");
            receiptInfo.put("SERVICII", "Bilete");
            receiptInfo.put("CANTITATE", ticket.getAmount().toString());
            receiptInfo.put("VALOARE", ticket.getPrice().toString());
            receiptInfo.put("TOTAL", String.valueOf((ticket.getAmount() * ticket.getPrice())));

            String docName = ReceiptUtility.generateReceipt(receiptInfo);
            File file = new File(docName);
            String receiptUrl = amazonService.uploadFile("receipts",docName, file);
            mailService.sendReceiptEmail(user.getEmail(), docName, receiptUrl);

            ticket.setReceiptUrl(receiptUrl);
            ticketRepository.save(ticket);

            return TicketDto.builder()
                    .id(ticketId)
                    .amount(ticket.getAmount())
                    .ticketPrice(ticket.getPrice())
                    .eventId(event.getId())
                    .eventName(event.getName())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .receiptUrl(receiptUrl)
                    .build();

        }
    }
}
