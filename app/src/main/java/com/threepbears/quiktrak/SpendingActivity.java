package com.threepbears.quiktrak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpendingActivity extends AppCompatActivity {

    private ArrayList<Transaction> transactions = new ArrayList<>();
    private Map<String, Integer> spendingByCategory = new HashMap<>();
    private static final int MIN_ROW_HEIGHT = 100;
    SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private TransactionRoomDatabase transDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SpendingActivity.this, AddTransactionActivity.class));
            }
        });

        final Calendar cal = Calendar.getInstance();

        final Button buttonEarlierMonth = findViewById(R.id.buttonEarlierMonth);
        final TextView monthTextView = findViewById(R.id.monthTextView);
        final Button buttonLaterMonth = findViewById(R.id.buttonLaterMonth);

        buttonEarlierMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, -1);
                monthTextView.setText(format.format(cal.getTime()));
                clearSpendingRowsAndData();
                showSpendingForMonth(cal);
            }
        });

        monthTextView.setText(format.format(cal.getTime()));

        buttonLaterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, 1);
                monthTextView.setText(format.format(cal.getTime()));
                clearSpendingRowsAndData();
                showSpendingForMonth(cal);
            }
        });

        transDB = TransactionRoomDatabase.getDatabase(this);
    }

    private void clearSpendingRowsAndData() {
        spendingByCategory.clear();
        transactions.clear();
        removeAllSpendingRows();
    }

    private void showSpendingForMonth(Calendar cal) {
        final TextView totalTextView = findViewById(R.id.totalTextView);

        readAllTransactionsInMonth(cal);
        addAllTransactionRows();
        totalTextView.setText(String.format("Total: %s", CurrencyFormatter.createCurrencyFormattedString(getSpendingTotal())));
    }

    private void readAllTransactionsInMonth(Calendar cal) {
        TransactionDao transDao = transDB.transactionDao();
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.setTime(cal.getTime());
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.setTime(cal.getTime());
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        List<Transaction> transactionsFromDB = transDao.getTransactionsForMonth(firstDayOfMonth.getTime(), lastDayOfMonth.getTime());
        transactions.addAll(transactionsFromDB);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showSpendingForMonth(Calendar.getInstance());
    }

    @Override
    protected void onStop() {
        super.onStop();

        transactions.clear();
        clearSpendingRowsAndData();
    }

    private void addAllTransactionRows() {
        TableLayout transTable = findViewById(R.id.spendingTable);

        for (Transaction t : transactions) {
            if (spendingByCategory.containsKey(t.getCategory())) {
                int currentValue = spendingByCategory.get(t.getCategory());
                spendingByCategory.put(t.getCategory(), currentValue + t.getAmount());
            }
            else {
                spendingByCategory.put(t.getCategory(), t.getAmount());
            }
        }

        for (Map.Entry<String, Integer> s : spendingByCategory.entrySet()) {
            addTransactionRow(transTable, s);
        }
    }

    private void addTransactionRow(TableLayout transTable, Map.Entry<String, Integer> spendingTotal) {
        final TableRow newRow = new TableRow(this);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        final TextView categoryTextView = new TextView(this);
        categoryTextView.setGravity(Gravity.CENTER);
        categoryTextView.setText(spendingTotal.getKey());
        categoryTextView.setTextColor(Color.BLACK);
        categoryTextView.setLayoutParams(layoutParams);
        newRow.addView(categoryTextView);

        final TextView amountTextView = new TextView(this);
        amountTextView.setGravity(Gravity.CENTER);
        amountTextView.setText(CurrencyFormatter.createCurrencyFormattedString(spendingTotal.getValue()));
        amountTextView.setTextColor(Color.BLACK);
        amountTextView.setLayoutParams(layoutParams);
        newRow.addView(amountTextView);

        newRow.setMinimumHeight(MIN_ROW_HEIGHT);
        newRow.setGravity(Gravity.CENTER);
        newRow.setLayoutParams(layoutParams);
        transTable.addView(newRow);
    }

    private int getSpendingTotal() {
        int total = 0;

        for (Transaction trans : transactions) {
            total += trans.getAmount();
        }

        return total;
    }

    private void removeAllSpendingRows() {
        TableLayout transTable = findViewById(R.id.spendingTable);

        transTable.removeAllViews();
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
        if (id == R.id.transactions_page) {
            startActivity(new Intent(SpendingActivity.this, TransactionsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
