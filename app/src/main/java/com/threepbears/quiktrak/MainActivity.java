package com.threepbears.quiktrak;

import android.content.Context;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_TRANS_REQUEST_CODE = 0;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String TRANACTION_FILE_NAME = "transactions";

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
                startActivityForResult(new Intent(MainActivity.this, AddTransactionActivity.class), ADD_TRANS_REQUEST_CODE);
            }
        });

        readAllTransactionsFromFile();
        addAllTransactionRows();
    }

    private void readAllTransactionsFromFile()
    {
        FileInputStream fis;
        ObjectInputStream is;
        try {
            fis = openFileInput(TRANACTION_FILE_NAME);
            is = new ObjectInputStream(fis);

            while (fis.available() != 0) {
                Transaction trans = (Transaction) is.readObject();
                transactions.add(trans);
            }
//
//            for (Transaction t : input) {
//                transactions.add(t);
//            }
//
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAllTransactionRows() {
        TableLayout transTable = findViewById(R.id.transactionTable);

        for (Transaction t : transactions) {
            addTransactionRow(transTable, t);
        }
    }

    private void addTransactionRow(TableLayout transTable, Transaction trans) {
        TableRow newRow = new TableRow(this);

        TextView dateTextView = new TextView(this);
        dateTextView.setGravity(Gravity.CENTER);
        dateTextView.setText(trans.getDate());
        dateTextView.setTextColor(Color.BLACK);
        newRow.addView(dateTextView);

        TextView amountTextView = new TextView(this);
        amountTextView.setGravity(Gravity.CENTER);
        amountTextView.setText(String.format(Locale.ENGLISH, "%.2f", trans.getAmount()));
        amountTextView.setTextColor(Color.BLACK);
        newRow.addView(amountTextView);

        TextView categoryTextView = new TextView(this);
        categoryTextView.setGravity(Gravity.CENTER);
        categoryTextView.setText(trans.getCategory());
        categoryTextView.setTextColor(Color.BLACK);
        newRow.addView(categoryTextView);

        TextView noteTextView = new TextView(this);
        noteTextView.setGravity(Gravity.CENTER);
        noteTextView.setText(trans.getNote());
        noteTextView.setTextColor(Color.BLACK);
        newRow.addView(noteTextView);

        transTable.addView(newRow);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TableLayout transTable = findViewById(R.id.transactionTable);

        if (requestCode == ADD_TRANS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Transaction newTrans = new Transaction(data.getStringExtra(getString(R.string.date_text_view)),
                    data.getFloatExtra(getString(R.string.amount_text_view), 0),
                    data.getStringExtra(getString(R.string.category_text_view)),
                    data.getStringExtra(getString(R.string.note_text_view)));

            writeTractionToFile(newTrans);
            transactions.add(newTrans);

            addTransactionRow(transTable, newTrans);
        }
    }

    private void writeTractionToFile(Transaction newTrans) {
        FileOutputStream outputStream;
        ObjectOutputStream objectOutputStream;

        try {
            outputStream = openFileOutput(TRANACTION_FILE_NAME, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(newTrans);

            objectOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
