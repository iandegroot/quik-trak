package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CategoriesViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public CategoriesViewModelFactory(Application application) {
        this.application = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoriesViewModel.class)) {
            return (T) new CategoriesViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}