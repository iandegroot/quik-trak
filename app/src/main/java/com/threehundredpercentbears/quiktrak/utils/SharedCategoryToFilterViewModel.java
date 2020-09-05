package com.threehundredpercentbears.quiktrak.utils;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class SharedCategoryToFilterViewModel extends ViewModel {
    private String categoryToFilter;
    private Calendar calendar;

    public void setCategoryToFilter(String categoryName) {
        this.categoryToFilter = categoryName;
    }

    public String getCategoryToFilter() {
        return this.categoryToFilter;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
