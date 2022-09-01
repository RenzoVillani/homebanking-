package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.model.Card;
import com.mindhub.homebanking.model.CardColor;
import com.mindhub.homebanking.model.CardType;
import com.mindhub.homebanking.model.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
        if (!client.getCard().contains(card)){
            return new ResponseEntity<>("This card isn't yours", HttpStatus.FORBIDDEN);
        }
        cardRepository.delete(card);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
