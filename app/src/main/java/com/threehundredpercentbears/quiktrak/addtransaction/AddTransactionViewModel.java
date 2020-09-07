package com.threehundredpercentbears.quiktrak.addtransaction;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.models.category.CategoryRepository;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;
import com.threehundredpercentbears.quiktrak.models.transaction.TransactionRepository;

import java.util.List;

public class AddTransactionViewModel extends AndroidViewModel {

    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    private LiveData<List<Category>> allCategories;

    public AddTransactionViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        allCategories = categoryRepository.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        transactionRepository.updateTransactionData(transaction);
    }

    public void deleteTransaction(int id) {
        transactionRepository.deleteTransaction(id);
    }
}
