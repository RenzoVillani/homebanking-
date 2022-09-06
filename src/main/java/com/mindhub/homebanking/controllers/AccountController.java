package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.model.Account;
import com.mindhub.homebanking.model.AccountType;
import com.mindhub.homebanking.model.Card;
import com.mindhub.homebanking.model.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.AccountUtils.generateAccountNumber;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private com.mindhub.homebanking.repositories.AccountRepository accountRepository;

    @Autowired
    private com.mindhub.homebanking.repositories.ClientRepository clientRepository;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccount(){
        return this.accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id, Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findById(id).orElse(null);

        if (!client.getAccounts().contains(account)){
            return null;
        }

        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts().stream().filter(account -> account.isActive().equals(true)).collect(Collectors.toSet());

        if (accounts.size() >= 3) {
            return new ResponseEntity<>("Already has 3 accounts", HttpStatus.FORBIDDEN);
        }
        Account account = new Account(generateAccountNumber(accountRepository), LocalDateTime.now(), 0);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PostMapping("/clients/current/accounts/type")
    public ResponseEntity<Object> changeAccountType(@RequestParam AccountType type, Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts().stream().filter(account -> account.isActive().equals(true)).collect(Collectors.toSet());

        if (accounts.size() >= 3) {
            return new ResponseEntity<>("Already has 3 accounts", HttpStatus.FORBIDDEN);
        }
        Account account = new Account(generateAccountNumber(accountRepository), LocalDateTime.now(), 0, type);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());

        return client.getAccounts().stream().filter(account -> account.isActive().equals(true)).map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    @PutMapping("/clients/current/accounts")
    public ResponseEntity<Object> disabelAccount(String number, Authentication authentication){
        Account account = this.accountRepository.findByNumber(number);
        Client client = this.clientRepository.findByEmail(authentication.getName());

        if (accountRepository.findByNumber(number) == null){
            return new ResponseEntity<>("This account don't exist", HttpStatus.FORBIDDEN);
        }
        if (!client.getAccounts().contains(account)){
            return new ResponseEntity<>("This account isn't yours", HttpStatus.FORBIDDEN);
        }
        if (account.getBalance() <0){
            return new ResponseEntity<>("You can't disable an account with money", HttpStatus.FORBIDDEN);
        }
        account.setActive(false);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}