package com.mindhub.homebanking.controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
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

        transactionRepository.save(new Transaction(TransactionType.DEBIT, -amount, fromAccountNumber + " " + description, LocalDateTime.now(), fromAccount));
        transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, toAccountNumber + " " + description, LocalDateTime.now(), toAccount));

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
