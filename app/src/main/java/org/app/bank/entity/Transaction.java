package org.app.bank.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Transaction {
    private @Id @GeneratedValue Long id;
    private Long fromAccount;
    private Long toAccount;
    private Double amount;
    private Instant timestamp;

    protected Transaction() {}

    public Transaction(Long fromAccount, Long toAccount, Double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    public Transaction(Long toAccount, Double amount) {
        this.fromAccount = 0L;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    
}