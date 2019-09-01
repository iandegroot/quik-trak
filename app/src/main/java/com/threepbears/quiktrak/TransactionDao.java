package com.threepbears.quiktrak;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("DELETE FROM transactions_table")
    void deleteAll();

    @Query("DELETE FROM transactions_table WHERE id = :id")
    void deleteUser(long id);

    @Query("SELECT * from transactions_table ORDER BY date ASC")
    List<Transaction> getAllTransactions();
}
