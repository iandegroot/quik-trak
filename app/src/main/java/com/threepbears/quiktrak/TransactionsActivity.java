package com.threepbears.quiktrak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private ArrayList<Transaction> transactions = new ArrayList<>();
    private static final int MIN_ROW_HEIGHT = 100;
    private TransactionRoomDatabase transDB;

    // This only runs once, the first time this activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionsActivity.this, AddTransactionActivity.class));
            }
        });

        transDB = TransactionRoomDatabase.getDatabase(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        readAllTransactionsFromDB();
        addAllTransactionRows();
    }

    private void readAllTransactionsFromDB() {
        TransactionDao transDao = transDB.transactionDao();

        transactions.clear();
        List<Transaction> transactionsFromDB = transDao.getAllTransactions();
        transactions.addAll(transactionsFromDB);
    }

    private void addAllTransactionRows() {
        TableLayout transTable = findViewById(R.id.transactionTable);

        for (Transaction t : transactions) {
            addTransactionRow(transTable, t);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        transactions.clear();
        removeAllTransactionRows();
    }

    private void removeAllTransactionRows() {
        TableLayout transTable = findViewById(R.id.transactionTable);

        int childCount = transTable.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            transTable.removeViews(1, childCount - 1);
        }
    }

    private void addTransactionRow(TableLayout transTable, Transaction trans) {
        final TableRow newRow = new TableRow(this);

        newRow.setId(trans.getId());

        final TextView dateTextView = new TextView(this);
        dateTextView.setGravity(Gravity.CENTER);
        dateTextView.setText(DateFormatter.dateToString(trans.getDate()));
        newRow.addView(dateTextView);

        final TextView amountTextView = new TextView(this);
        amountTextView.setGravity(Gravity.CENTER);
        amountTextView.setText(CurrencyFormatter.createCurrencyFormattedString(trans.getAmount()));
        newRow.addView(amountTextView);

        final TextView categoryTextView = new TextView(this);
        categoryTextView.setGravity(Gravity.CENTER);
        categoryTextView.setText(trans.getCategory());
        newRow.addView(categoryTextView);

        final TextView noteTextView = new TextView(this);
        noteTextView.setGravity(Gravity.CENTER);
        noteTextView.setText(trans.getNote());
        newRow.addView(noteTextView);

        newRow.setMinimumHeight(MIN_ROW_HEIGHT);
        newRow.setGravity(Gravity.CENTER);
        newRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage("Are you sure you want to delete the transaction?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeTransactionAndRow(newRow.getId());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        transTable.addView(newRow);
    }

    private void removeTransactionAndRow(int transId) {
        if (deleteTransaction(transId)) {
            removeAllTransactionRows();
            addAllTransactionRows();
        }
    }

    private boolean deleteTransaction(int id) {

        if (!checkForTransactionInDB(id).isEmpty()) {
            deleteTransactionFromDB(id);
            readAllTransactionsFromDB();
            return true;
        }

        return false;
    }

    private void deleteTransactionFromDB(int id) {
        TransactionDao transDao = transDB.transactionDao();

        transDao.deleteTransaction(id);
    }

    private List<Transaction> checkForTransactionInDB(int id) {
        TransactionDao transDao = transDB.transactionDao();

        return transDao.getTransaction(id);
    }
}
