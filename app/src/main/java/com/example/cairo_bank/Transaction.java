package com.example.cairo_bank;

public class Transaction {
    private long timestamp;
    private double amount;
    private String type;
    private String details;

    public Transaction(long timestamp, double amount, String type, String details) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.type = type;
        this.details = details;
    }

    public long getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDetails() { return details; }
}
