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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private com.mindhub.homebanking.repositories.CardRepository cardRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.ClientRepository clientRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.AccountRepository accountRepository;

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createCard(@RequestParam CardColor cardColor, @RequestParam CardType cardType, Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());

        if (cardRepository.findByClientAndType(client, cardType).size() >= 3) {
            return new ResponseEntity<>("Already has 3 cards", HttpStatus.FORBIDDEN);
        }
        cardRepository.save(new Card(cardType, cardColor, generateCardNumber() ,ganerateCvvNumber(), LocalDateTime.now(), LocalDateTime.now().plusYears(5), client)) ;
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    public String generateCardNumber(){
        String number = ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " + ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                ((int) ((Math.random() * (9999 - 1000)) + 1000));

        while (cardRepository.findByNumber(number) != null){
            number = ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " + ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                    ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                    ((int) ((Math.random() * (9999 - 1000)) + 1000));
        }
        return number;
    }
    public int ganerateCvvNumber(){
        return ((int) ((Math.random() * (999 - 100)) + 100));
    }
}
