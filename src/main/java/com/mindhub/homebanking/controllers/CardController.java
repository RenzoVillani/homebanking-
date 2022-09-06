package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardPaymentDTO;
import com.mindhub.homebanking.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.CardUtils.ganerateCvvNumber;
import static com.mindhub.homebanking.utils.CardUtils.generateCardNumber;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private com.mindhub.homebanking.repositories.CardRepository cardRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.ClientRepository clientRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.AccountRepository accountRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.TransactionRepository transactionRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardColor cardColor, @RequestParam CardType cardType, Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());

        if (cardRepository.findByClientAndType(client, cardType).size() >= 3) {
            return new ResponseEntity<>("Already has 3 cards", HttpStatus.FORBIDDEN);
        }
        cardRepository.save(new Card(cardType, cardColor, generateCardNumber(cardRepository) ,ganerateCvvNumber(), LocalDateTime.now(), LocalDateTime.now().plusYears(5), client)) ;
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/current/cards")
    public ResponseEntity<Object> deleteCard(@RequestParam String number, Authentication authentication){

        Card card = this.cardRepository.findByNumber(number);
        Client client = this.clientRepository.findByEmail(authentication.getName());

        if (cardRepository.findByNumber(number) == null){
            return new ResponseEntity<>("This card don't exist", HttpStatus.FORBIDDEN);
        }
        if (!client.getCards().contains(card)){
            return new ResponseEntity<>("This card isn't yours", HttpStatus.FORBIDDEN);
        }
        cardRepository.delete(card);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/clients/current/cards/payments")
    public ResponseEntity<Object> createCard(@RequestBody CardPaymentDTO cardPaymentDTO, Authentication authentication){
        String number = cardPaymentDTO.getNumber();
        int cvv = cardPaymentDTO.getCvv();
        double amount = cardPaymentDTO.getAmount();
        String description = cardPaymentDTO.getDescription();

        Card card = this.cardRepository.findByNumber(number);

        Client client = this.clientRepository.findByCards(card);
        List<Account> accounts = client.getAccounts().stream().filter(account -> account.getBalance()>=amount).collect(Collectors.toList());
        if(accounts.isEmpty()){
            return new ResponseEntity<>("There isn´t accounts", HttpStatus.FORBIDDEN);
        }
        Account account = accounts.stream().findAny().get();
        if(number.isEmpty() || cvv <= 100 || amount <= 0 || description.isEmpty()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if(cvv != card.getCvv()){
            return new ResponseEntity<>("The security number isn't correct", HttpStatus.FORBIDDEN);
        }


        if(card.getThruDate().isBefore(LocalDateTime.now())){
            return new ResponseEntity<>("The card expired", HttpStatus.FORBIDDEN);
        }

        transactionRepository.save(new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now(), account, account.getBalance()));

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);


        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
