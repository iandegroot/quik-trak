package com.threehundredpercentbears.quiktrak.transactions;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.models.category.CategoryRepository;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;
import com.threehundredpercentbears.quiktrak.models.transaction.TransactionRepository;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsViewModel;

import java.util.Date;
import java.util.List;

public class TransactionsViewModel extends AndroidViewModel implements MonthlyTransactionsViewModel {

    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    private LiveData<List<Transaction>> transactionsForMonth;
    private LiveData<List<Category>> allCategories;

    public TransactionsViewModel(Application application) {
        super(application);

        transactionRepository = new TransactionRepository(application);
        transactionsForMonth = transactionRepository.getTransactionsForMonth();

        categoryRepository = new CategoryRepository(application);
        allCategories = categoryRepository.getAllCategories();
    }

    LiveData<List<Transaction>> getTransactionsForMonth() {
        return transactionsForMonth;
    }

    public void updateMonthFilter(Date startDate, Date endDate) {
        transactionRepository.setMonthFilter(startDate, endDate);
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }

    public void deleteTransaction(int id) {
        transactionRepository.deleteTransaction(id);
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
}
