package com.threepbears.quiktrak;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String date;
    private float amount;
    private String category;
    private String note;

    public Transaction(String date, float amount, String category, String note) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
