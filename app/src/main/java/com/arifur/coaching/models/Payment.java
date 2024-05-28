package com.arifur.coaching.models;

public class Payment {
    private String id;
    private String studentId;
    private double amount;
    private String date;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(Payment.class)
    }

    public Payment(String id, String studentId, double amount, String date) {
        this.id = id;
        this.studentId = studentId;
        this.amount = amount;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
