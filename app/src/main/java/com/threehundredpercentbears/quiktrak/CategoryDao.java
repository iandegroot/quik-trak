package com.threehundredpercentbears.quiktrak;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Query("DELETE FROM categories_table")
    void deleteAll();

    @Query("DELETE FROM categories_table WHERE id = :id")
    void deleteCategory(int id);

    @Query("SELECT * from categories_table WHERE id = :id")
    List<Category> getCategory(int id);

    @Query("SELECT * from categories_table")
    List<Category> getAllCategories();
}
