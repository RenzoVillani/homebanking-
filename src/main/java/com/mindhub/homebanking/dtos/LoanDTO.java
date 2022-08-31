package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.model.ClientLoan;
import com.mindhub.homebanking.model.Loan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LoanDTO {
    private long id;
    private String name;
    private double maxAmount;
    private List<Integer> payments = new ArrayList<>();

    private Set<ClientLoanDTO> loans = new HashSet<>();

    public LoanDTO() {
    }

    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = loan.getPayments();
        this.loans = loan.getClientLoans().stream().map(clientLoan -> new ClientLoanDTO(clientLoan)).collect(Collectors.toSet());
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public Set<ClientLoanDTO> getLoans() {
        return loans;
    }
}
