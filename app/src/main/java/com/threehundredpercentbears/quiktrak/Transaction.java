package com.threehundredpercentbears.quiktrak;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "transactions_table")
public class Transaction {

    @PrimaryKey
    private int id;
    @TypeConverters({DateConverter.class})
    private Date date;
    private int amount;
    private String category;
    private String note;

    public Transaction(int id, Date date, int amount, String category, String note) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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
