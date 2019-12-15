package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TransactionsViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public TransactionsViewModelFactory(Application application) {
        this.application = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionsViewModel.class)) {
            return (T) new TransactionsViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}