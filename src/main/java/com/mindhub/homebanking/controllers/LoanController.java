package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.model.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ClientLoanRepository clientLoanRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO){

        Client client = this.clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);

        double amount = loanApplicationDTO.getAmount();
        int payments = loanApplicationDTO.getPayments();
        String accountToNumber = loanApplicationDTO.getToAccountNumber();

        if (amount <= 0 || payments <= 0 || accountToNumber.isEmpty()){
            return new ResponseEntity<>("Invalid data", HttpStatus.FORBIDDEN);
        }
        if (loan == null){
            return new ResponseEntity<>("The loan don't exist", HttpStatus.FORBIDDEN);
        }
        if (loan.getMaxAmount() < amount){
            return new ResponseEntity<>("The amount exceeds the max amount possible", HttpStatus.FORBIDDEN);
        }
        if (!loan.getPayments().contains(payments)){
            return new ResponseEntity<>("The number of payments is not among those available for the loan", HttpStatus.FORBIDDEN);
        }
        if(account == null){
            return new ResponseEntity<>("The destination account don't exist", HttpStatus.FORBIDDEN);
        }
        if (!client.getAccounts().contains(account)){
            return new ResponseEntity<>("The destination account don't belong to this client", HttpStatus.FORBIDDEN);
        }

        clientLoanRepository.save(new ClientLoan(amount * 1.2, payments, client, loan));
        transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, loan.getName() + " loan approved", LocalDateTime.now(), account));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
