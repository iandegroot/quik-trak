package com.threehundredpercentbears.quiktrak.utils;

import androidx.lifecycle.ViewModel;

public class SharedCategoryToFilterViewModel extends ViewModel {
    private String categoryToFilter;

    public void setCategoryToFilter(String categoryName) {
        this.categoryToFilter = categoryName;
    }

    public String getCategoryToFilter() {
        return this.categoryToFilter;
    }
}
