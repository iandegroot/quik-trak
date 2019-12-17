package com.threehundredpercentbears.quiktrak.spending;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SpendingViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public SpendingViewModelFactory(Application application) {
        this.application = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SpendingViewModel.class)) {
            return (T) new SpendingViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}