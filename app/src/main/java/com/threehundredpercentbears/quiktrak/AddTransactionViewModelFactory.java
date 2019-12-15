package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddTransactionViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public AddTransactionViewModelFactory(Application application) {
        this.application = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddTransactionViewModel.class)) {
            return (T) new AddTransactionViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}