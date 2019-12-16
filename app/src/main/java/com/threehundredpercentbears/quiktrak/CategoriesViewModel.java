package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private CategoryRepository repository;

    private LiveData<List<Category>> allCategories;

    public CategoriesViewModel(Application application) {
        super(application);
        repository = new CategoryRepository(application);
        allCategories = repository.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void updateRank(int id, int rank) {
        repository.updateRank(id, rank);
    }

    public void deleteCategory(int id) {
        repository.deleteCategory(id);
    }
}
