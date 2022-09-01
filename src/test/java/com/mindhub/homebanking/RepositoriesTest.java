package com.mindhub.homebanking;

import com.mindhub.homebanking.model.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoriesTest {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    TransactionRepository transactionRepository;

    //LOANS TESTS
    @Test
    public void existLoans(){

        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, is(not(empty())));
    }

    @Test
    public void existLoanPersonal(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name",is("Personal"))));
    }

    //CLIENTS TESTS
    @Test
    public void existClients(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, is(not(empty())));
    }

    @Test
    public void existClientWithAtSign(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, everyItem(hasProperty("email", containsString("@"))));
    }

    //ACCOUNTS TESTS
    @Test
    public void existAccounts(){
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, is(not(empty())));
    }

    @Test
    public void  doNotExistAccountWithNegativeBalance(){
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, everyItem(hasProperty("balance", not(lessThan(0)))));
    }

    //CARDS TESTS
    @Test
    public void existCards(){
        List<Card> cards = cardRepository.findAll();
        assertThat(cards, is(not(empty())));
    }
    @Test
    public void onlyExistCardsWithCvvOfThreeDigits(){
        List<Card> cards = cardRepository.findAll();
        assertThat(cards, everyItem(hasProperty("cvv",not(lessThan(100)))));
        assertThat(cards, everyItem(hasProperty("cvv",not(greaterThan(999)))));
    }

    //TRANSACTIONS TESTS
    @Test
    public void existTransactions(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, is(not(empty())));
    }

    @Test
    public void  doNotExistTransactionWithOutDescription(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, everyItem(hasProperty("description",not(empty()))));
    }
}
