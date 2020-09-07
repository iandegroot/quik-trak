package com.threehundredpercentbears.quiktrak.models.category;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;

    // TODO: Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public CategoryRepository(Application application) {
        CategoryRoomDatabase transDB = CategoryRoomDatabase.getDatabase(application);
        categoryDao = transDB.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(final Category category) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.insert(category);
            }
        });
    }

    public void updateRank(final int id, final int rank) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.updateRank(id, rank);
            }
        });
    }

    public void deleteCategory(final int id) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.deleteCategory(id);
            }
        });
    }

    public void updateName(final String oldName, final String newName) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.updateName(oldName, newName);
            }
        });
    }
}