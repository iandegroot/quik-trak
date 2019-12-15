package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AddTransactionViewModel extends AndroidViewModel {

    private TransactionRepository repository;

    private LiveData<List<Transaction>> allTransactions;

    public AddTransactionViewModel(Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
    }

    LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }
}