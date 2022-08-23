package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.model.Account;
import com.mindhub.homebanking.model.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private com.mindhub.homebanking.repositories.AccountRepository accountRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccount(){
        return this.accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        System.out.println(id);
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());

        if (client.getAccounts().size() >= 3) {
            return new ResponseEntity<>("Already has 3 accounts", HttpStatus.FORBIDDEN);
        }
        Account account = new Account(generateAccountNumber(), LocalDateTime.now(), 0);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public String generateAccountNumber(){
        String number = "VIN" + ((int) ((Math.random() * (999 - 0)) + 0));

        while(accountRepository.findByNumber(number) != null){
            number = "VIN" + ((int) ((Math.random() * (999 - 0)) + 0));
        }
        return number;
    }
}
