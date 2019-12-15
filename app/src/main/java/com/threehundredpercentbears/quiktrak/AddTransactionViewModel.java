package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AddTransactionViewModel extends AndroidViewModel {

    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    private LiveData<List<Category>> allCategories;

    public AddTransactionViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        allCategories = categoryRepository.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }
}
