package com.threehundredpercentbears.quiktrak.categories;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.models.category.CategoryRepository;
import com.threehundredpercentbears.quiktrak.models.transaction.TransactionRepository;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    private LiveData<List<Category>> allCategories;

    public CategoriesViewModel(Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        transactionRepository = new TransactionRepository(application);
        allCategories = categoryRepository.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Category category) {
        categoryRepository.insert(category);
    }

    public void updateRank(int id, int rank) {
        categoryRepository.updateRank(id, rank);
    }

    public void deleteCategory(int id) {
        categoryRepository.deleteCategory(id);
    }

    public void updateName(String oldName, String newName) {
        categoryRepository.updateName(oldName, newName);
    }

    public void deleteAllCategoryTransactions(String categoryName) {
        transactionRepository.deleteAllCategoryTransactions(categoryName);
    }

    public void updateAllCategoryTransactionNames(String oldName, String newName) {
        transactionRepository.updateAllCategoryTransactionNames(oldName, newName);
    }
}
