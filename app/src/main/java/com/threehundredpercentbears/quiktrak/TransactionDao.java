package com.threehundredpercentbears.quiktrak;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("DELETE FROM transactions_table")
    void deleteAll();

    @Query("DELETE FROM transactions_table WHERE id = :id")
    void deleteTransaction(int id);

    @Query("DELETE FROM transactions_table WHERE category = :category")
    void deleteAllCategoryTransactions(String category);

    @Query("SELECT * from transactions_table ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * from transactions_table WHERE id = :id")
    LiveData<List<Transaction>> getTransaction(int id);

    @TypeConverters({DateConverter.class})
    @Query("SELECT * FROM transactions_table WHERE date BETWEEN :startDate AND :endDate")
    LiveData<List<Transaction>> getTransactionsForMonth(Date startDate, Date endDate);
}
