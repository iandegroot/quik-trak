package com.threehundredpercentbears.quiktrak.transactions;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;
import com.threehundredpercentbears.quiktrak.models.transaction.TransactionRepository;

import java.util.List;

public class TransactionsViewModel extends AndroidViewModel {

    private TransactionRepository repository;

    private LiveData<List<Transaction>> allTransactions;

    public TransactionsViewModel(Application application) {
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

    public void deleteTransaction(int id) {
        repository.deleteTransaction(id);
    }
}
