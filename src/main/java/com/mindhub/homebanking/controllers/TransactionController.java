package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.model.Account;
import com.mindhub.homebanking.model.Client;
import com.mindhub.homebanking.model.Transaction;
import com.mindhub.homebanking.model.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam double amount,
                                                    @RequestParam String description,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    Authentication authentication){

        Client client = this.clientRepository.findByEmail(authentication.getName());
        Account fromAccount = this.accountRepository.findByNumber(fromAccountNumber);
        Account toAccount = this.accountRepository.findByNumber(toAccountNumber);

        if (amount < 1 || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (fromAccountNumber == toAccountNumber){
            return new ResponseEntity<>("Both accounts numbers are the same", HttpStatus.FORBIDDEN);
        }

        if (fromAccount == null){
            return new ResponseEntity<>("There isn't any account with that origin account number", HttpStatus.FORBIDDEN);
        }

        if (!client.getAccounts().contains(fromAccount)){
            return new ResponseEntity<>("The selected account isn't yours", HttpStatus.FORBIDDEN);
        }

        if (toAccount == null){
            return new ResponseEntity<>("There isn't any account with that destination account number", HttpStatus.FORBIDDEN);
        }

        if (fromAccount.getBalance() < amount){
            return new ResponseEntity<>("There isn't enough money to do the transaction", HttpStatus.FORBIDDEN);
        }

        if (fromAccount.isActive() == false || toAccount.isActive() == false){
            return new ResponseEntity<>("One of the accounts isn't enabled", HttpStatus.FORBIDDEN);
        }

        transactionRepository.save(new Transaction(TransactionType.DEBIT, -amount, fromAccountNumber + " " + description, LocalDateTime.now(), fromAccount, fromAccount.getBalance() - amount));
        transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, toAccountNumber + " " + description, LocalDateTime.now(), toAccount,  toAccount.getBalance() + amount));

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/transactions/get")
        public ResponseEntity<Object> createTransaction(@RequestParam String since,
        @RequestParam String until,
        @RequestParam String number,
        Authentication authentication){

        LocalDateTime sinceDate = LocalDateTime.parse(since);
        LocalDateTime untilDate = LocalDateTime.parse(until);
        Client client = this.clientRepository.findByEmail(authentication.getName());
        Account account = this.accountRepository.findByNumber(number);
        if(since.isEmpty() || until.isEmpty() || number.isEmpty()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if(!client.getAccounts().contains(account)){
            return new ResponseEntity<>("The selected account isn't yours", HttpStatus.FORBIDDEN);
        }
        Set<TransactionDTO> transactions = transactionRepository.findByDateBetween(sinceDate, untilDate).stream().filter(transaction -> transaction.getAccount().equals(account)).map(TransactionDTO::new).collect(Collectors.toSet());
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
