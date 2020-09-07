package com.threehundredpercentbears.quiktrak.models.transaction;

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
    public TransactionRepository(Application application) {
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

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getTransactionsForMonth() {
        return transactionsForMonth;
    }

    public void insert(final Transaction transaction) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.insert(transaction);
            }
        });
    }

    public void setMonthFilter(Date startDate, Date endDate) {
        MyMonth update = new MyMonth(startDate, endDate);
        if (Objects.equals(transactionsForMonthFilter.getValue(), update)) {
            return;
        }
        transactionsForMonthFilter.setValue(update);
    }

    public void deleteTransaction(final int id) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteTransaction(id);
            }
        });
    }

    public void deleteAllCategoryTransactions(final String categoryName) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteAllCategoryTransactions(categoryName);
            }
        });
    }

    public void updateTransactionData(final Transaction transaction) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.updateDate(transaction.getId(), transaction.getDate());
                transactionDao.updateAmount(transaction.getId(), transaction.getAmount());
                transactionDao.updateCategory(transaction.getId(), transaction.getCategory());
                transactionDao.updateNote(transaction.getId(), transaction.getNote());
            }
        });
    }

    public void updateAllCategoryTransactionNames(final String oldName, final String newName) {
        TransactionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.updateAllCategoryTransactionNames(oldName, newName);
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
