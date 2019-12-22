package com.threehundredpercentbears.quiktrak.transactions;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;
import com.threehundredpercentbears.quiktrak.models.transaction.TransactionRepository;

import java.util.Date;
import java.util.List;

public class TransactionsViewModel extends AndroidViewModel {

    private TransactionRepository repository;

    private LiveData<List<Transaction>> transactionsForMonth;

    public TransactionsViewModel(Application application) {
        super(application);
        repository = new TransactionRepository(application);
        transactionsForMonth = repository.getTransactionsForMonth();
    }

    LiveData<List<Transaction>> getTransactionsForMonth() {
        return transactionsForMonth;
    }

    void updateMonthFilter(Date startDate, Date endDate) {
        repository.setMonthFilter(startDate, endDate);
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void deleteTransaction(int id) {
        repository.deleteTransaction(id);
    }
}
