package com.threehundredpercentbears.quiktrak;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories_table")
public class Category {

    @PrimaryKey
    private int id;
    private String categoryName;
    private int rank;

    public Category(int id, String categoryName, int rank) {
        this.id = id;
        this.categoryName = categoryName;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
