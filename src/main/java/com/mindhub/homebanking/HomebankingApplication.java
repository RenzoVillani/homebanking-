package com.mindhub.homebanking;

import com.mindhub.homebanking.model.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository,
									  LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository){
		return (args)->
		{
			Client client1 = new Client("Renzo", "Villani", "renzo@gmail.com", passwordEncoder.encode("1234"));
			Client client2 = new Client("Berbi", "Villani", "berbi@gmail.com", passwordEncoder.encode("0000"));

			Account account1 = new Account("112", LocalDateTime.now(), 34235.33);
			Account account2 = new Account("115", LocalDateTime.now().plusDays(1), 34565.33);
			Account account3 = new Account("120", LocalDateTime.now().plusDays(3), 20000);
			Account account4 = new Account("112", LocalDateTime.now(), 42000.30);

			client1.addAccount(account1);
			client1.addAccount(account2);
			client2.addAccount(account3);
			client2.addAccount(account4);

			clientRepository.save(client1);
			clientRepository.save(client2);
			accountRepository.save (account1);
			accountRepository.save (account2);
			accountRepository.save (account3);
			accountRepository.save (account4);


			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 2000, "transferencia recibida" ,LocalDateTime.now(), account1);
			Transaction transaction2 = new Transaction(TransactionType.DEBIT, -4000, "Compra tienda xx" ,LocalDateTime.now(), account1);
			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 1000, "transferencia recibida" ,LocalDateTime.now(), account2);
			Transaction transaction4 = new Transaction(TransactionType.DEBIT, -200, "Compra tienda xy" ,LocalDateTime.now(), account2);
			Transaction transaction5 = new Transaction(TransactionType.CREDIT, 8000, "transferencia recibida" ,LocalDateTime.now(), account3);
			Transaction transaction6 = new Transaction(TransactionType.DEBIT, -2000, "Compra tienda xz" ,LocalDateTime.now(), account3);
			Transaction transaction7 = new Transaction(TransactionType.CREDIT, 700, "transferencia recibida" ,LocalDateTime.now(), account4);
			Transaction transaction8 = new Transaction(TransactionType.DEBIT, -2000, "Compra tienda xi" ,LocalDateTime.now(), account4);

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);

			Loan loan1 = new Loan("Hipotecario", 500000, List.of(12,24,36,48,60));
			Loan loan2 = new Loan("Personal", 100000, List.of(6,12,24));
			Loan loan3 = new Loan("Automotriz", 300000, List.of(6,12,24,36));

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400.000, 60, client1, loan1);
			ClientLoan clientLoan2 = new ClientLoan(50.000, 12, client1, loan2);
			ClientLoan clientLoan3 = new ClientLoan(100.000, 24, client2, loan2);
			ClientLoan clientLoan4 = new ClientLoan(200.000, 36, client2, loan3);

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

			Card card1 = new Card(CardType.DEBIT , CardColor.GOLD, "3241-4214-5364-3253", 321, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client1);
			Card card2 = new Card(CardType.CREDIT , CardColor.TITANIUM, "1412-8968-6636-2435", 367, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client1);

			cardRepository.save(card1);
			cardRepository.save(card2);
		};
	}
}
