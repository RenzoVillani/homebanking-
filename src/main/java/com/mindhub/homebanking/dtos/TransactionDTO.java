package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.model.Transaction;
import com.mindhub.homebanking.model.TransactionType;

import java.time.LocalDateTime;

public class TransactionDTO {
    private long id;

    private TransactionType type;

    private String description;

    private LocalDateTime date;

    private double amount;

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType();
        this.description = transaction.getDesciption();
        this.date = transaction.getDate();
        this.amount = transaction.getAmount();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }
}
