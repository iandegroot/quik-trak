package com.threehundredpercentbears.quiktrak.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.threehundredpercentbears.quiktrak.spending.SpendingViewModel;

public class SharedCategoryToFilterViewModelFactory implements ViewModelProvider.Factory {

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SharedCategoryToFilterViewModel.class)) {
            return (T) new SharedCategoryToFilterViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}