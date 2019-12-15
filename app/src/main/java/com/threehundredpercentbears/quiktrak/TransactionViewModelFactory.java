package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TransactionViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public TransactionViewModelFactory(Application application) {
        this.application = application;
    }

    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionViewModel.class)) {
            return (T) new TransactionViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}