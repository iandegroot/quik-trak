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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Transaction> transactions = new ArrayList<>();
    private static final int MIN_ROW_HEIGHT = 100;
    private TransactionRoomDatabase transDB;

    // This only runs once, the first time this activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
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

        final TextView dateTextView = new TextView(this);
        dateTextView.setGravity(Gravity.CENTER);
        dateTextView.setText(DateFormatter.dateToString(trans.getDate()));
        dateTextView.setTextColor(Color.BLACK);
        newRow.addView(dateTextView);

        final TextView amountTextView = new TextView(this);
        amountTextView.setGravity(Gravity.CENTER);
        amountTextView.setText(String.format(Locale.ENGLISH, "%.2f", trans.getAmount()));
        amountTextView.setTextColor(Color.BLACK);
        newRow.addView(amountTextView);

        final TextView categoryTextView = new TextView(this);
        categoryTextView.setGravity(Gravity.CENTER);
        categoryTextView.setText(trans.getCategory());
        categoryTextView.setTextColor(Color.BLACK);
        newRow.addView(categoryTextView);

        final TextView noteTextView = new TextView(this);
        noteTextView.setGravity(Gravity.CENTER);
        noteTextView.setText(trans.getNote());
        noteTextView.setTextColor(Color.BLACK);
        newRow.addView(noteTextView);

        newRow.setMinimumHeight(MIN_ROW_HEIGHT);
        newRow.setGravity(Gravity.CENTER);
        newRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View finalView = v;

                new AlertDialog.Builder(v.getContext())
                        .setMessage("Are you sure you want to delete the transaction?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeRow(dateTextView.getText().toString(), amountTextView.getText().toString(),
                                        categoryTextView.getText().toString(), noteTextView.getText().toString());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        transTable.addView(newRow);
    }

    private void removeRow(String date, String amount, String cat, String note) {
        if (deleteTransaction(date, amount, cat, note)) {
            removeAllTransactionRows();
            addAllTransactionRows();
        }
    }

    private boolean deleteTransaction(String date, String amount, String cat, String note) {

        Iterator<Transaction> iter = transactions.iterator();

        while (iter.hasNext()) {
            Transaction tempTrans = iter.next();

            Date formattedDate = DateFormatter.stringToDate(date);

            if (tempTrans.getDate().equals(formattedDate)
                    && String.format(Locale.ENGLISH, "%.2f", tempTrans.getAmount()).equals(amount)
                    && tempTrans.getCategory().equals(cat)
                    && tempTrans.getNote().equals(note)) {
                deleteTransactionFromDB(tempTrans);
                iter.remove();
                return true;
            }
        }

        return false;
    }

    private void deleteTransactionFromDB(Transaction newTrans) {
        TransactionDao transDao = transDB.transactionDao();

        transDao.deleteUser(newTrans.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
