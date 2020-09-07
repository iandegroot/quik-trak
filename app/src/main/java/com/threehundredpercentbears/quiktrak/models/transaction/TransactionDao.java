package com.threehundredpercentbears.quiktrak.models.transaction;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.threehundredpercentbears.quiktrak.utils.converters.DateConverter;

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
    @Query("SELECT * FROM transactions_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsForMonth(Date startDate, Date endDate);

    @TypeConverters({DateConverter.class})
    @Query("UPDATE transactions_table SET date = :date WHERE id = :id")
    void updateDate(int id, Date date);

    @Query("UPDATE transactions_table SET amount = :amount WHERE id = :id")
    void updateAmount(int id, int amount);

    @Query("UPDATE transactions_table SET category = :category WHERE id = :id")
    void updateCategory(int id, String category);

    @Query("UPDATE transactions_table SET note = :note WHERE id = :id")
    void updateNote(int id, String note);

    @Query("UPDATE transactions_table SET category = :newName WHERE category = :oldName")
    void updateAllCategoryTransactionNames(String oldName, String newName);
}
