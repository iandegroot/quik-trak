package com.threehundredpercentbears.quiktrak.spending;

public class CategorySpending {

    private final String category;
    private final int amount;

    public CategorySpending(String category, int amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }
}
