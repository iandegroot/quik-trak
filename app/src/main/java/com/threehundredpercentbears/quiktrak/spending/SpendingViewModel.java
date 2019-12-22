package com.threehundredpercentbears.quiktrak.spending;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.models.category.CategoryRepository;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;
import com.threehundredpercentbears.quiktrak.models.transaction.TransactionRepository;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpendingViewModel extends AndroidViewModel implements MonthlyTransactionsViewModel {

    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    private LiveData<List<Transaction>> transactionsForMonth;
    private LiveData<List<Category>> allCategories;

    public SpendingViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        transactionsForMonth = transactionRepository.getTransactionsForMonth();
        allCategories = categoryRepository.getAllCategories();
    }

    LiveData<List<Transaction>> getTransactionsForMonth() {
        return transactionsForMonth;
    }

    public void updateMonthFilter(Date startDate, Date endDate) {
        transactionRepository.setMonthFilter(startDate, endDate);
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    List<CategorySpending> groupByCategoryAndSort(List<Transaction> transactionsByMonth) {
        return sortSpendingByAmount(groupByCategory(transactionsByMonth));
    }

    private Map<String, Integer> groupByCategory(List<Transaction> transactionsByMonth) {
        Map<String, Integer> spendingByCategory = new LinkedHashMap<>();

        for (Transaction t : transactionsByMonth) {
            if (spendingByCategory.containsKey(t.getCategory())) {
                int currentValue = spendingByCategory.get(t.getCategory());
                spendingByCategory.put(t.getCategory(), currentValue + t.getAmount());
            } else {
                spendingByCategory.put(t.getCategory(), t.getAmount());
            }
        }

        return spendingByCategory;
    }

    private List<CategorySpending> sortSpendingByAmount(Map<String, Integer> spendingByCategory) {
        // TODO - Make this more efficient
        List<Map.Entry<String, Integer>> mapAsList = new ArrayList<>(spendingByCategory.entrySet());

        Collections.sort(mapAsList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        List<CategorySpending> categorySpendings = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mapAsList) {
            categorySpendings.add(new CategorySpending(entry.getKey(), entry.getValue()));
        }

        return categorySpendings;
    }
}
