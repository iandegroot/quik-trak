package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TransactionRepository {

    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;

    // TODO: Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    TransactionRepository(Application application) {
        TransactionRoomDatabase transDB = TransactionRoomDatabase.getDatabase(application);
        transactionDao = transDB.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
    }

    LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    void insert(final Transaction transaction) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.insert(transaction);
            }
        });
    }
}
