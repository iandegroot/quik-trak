package com.threehundredpercentbears.quiktrak;

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
    CategoryRepository(Application application) {
        CategoryRoomDatabase transDB = CategoryRoomDatabase.getDatabase(application);
        categoryDao = transDB.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    void insert(final Category category) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.insert(category);
            }
        });
    }
}