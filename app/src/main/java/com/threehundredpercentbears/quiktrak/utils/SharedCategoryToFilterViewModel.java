package com.threehundredpercentbears.quiktrak.utils;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.Date;

public class SharedCategoryToFilterViewModel extends ViewModel {
    private String categoryToFilter = Constants.ALL_CATEGORIES;
    private Calendar calendar;
    private Date currentDate;

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

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }
}
