package com.threehundredpercentbears.quiktrak;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TransactionRepository {

    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<List<Transaction>> transactionsForMonth;
    private final MutableLiveData<MyMonth> transactionsForMonthFilter;

    // TODO: Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    TransactionRepository(Application application) {
        TransactionRoomDatabase transDB = TransactionRoomDatabase.getDatabase(application);
        transactionDao = transDB.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
        transactionsForMonthFilter = new MutableLiveData<>();
        transactionsForMonth = Transformations.switchMap(transactionsForMonthFilter, new Function<MyMonth, LiveData<List<Transaction>>>() {
            @Override
            public LiveData<List<Transaction>> apply(MyMonth month) {
                return transactionDao.getTransactionsForMonth(month.startDate, month.endDate);
            }
        });
    }

    LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    LiveData<List<Transaction>> getTransactionsForMonth() {
        return transactionsForMonth;
    }

    void insert(final Transaction transaction) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.insert(transaction);
            }
        });
    }

    void setMonthFilter(Date startDate, Date endDate) {
        MyMonth update = new MyMonth(startDate, endDate);
        if (Objects.equals(transactionsForMonthFilter.getValue(), update)) {
            return;
        }
        transactionsForMonthFilter.setValue(update);
    }

    void deleteTransaction(final int id) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteTransaction(id);
            }
        });
    }

    static class MyMonth {
        final Date startDate;
        final Date endDate;

        MyMonth(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
