package com.threepbears.quiktrak;

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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final static private int ADD_TRANS_REQUEST_CODE = 0;
    private ArrayList<Transaction> transactions = new ArrayList<>();

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TableLayout transTable = findViewById(R.id.transactionTable);

        if (requestCode == ADD_TRANS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Transaction newTrans = new Transaction(data.getStringExtra(getString(R.string.date_text_view)),
                    data.getFloatExtra(getString(R.string.amount_text_view), 0),
                    data.getStringExtra(getString(R.string.category_text_view)),
                    data.getStringExtra(getString(R.string.note_text_view)));

            transactions.add(newTrans);

            TableRow newRow = new TableRow(this);

            TextView dateTextView = new TextView(this);
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setText(newTrans.getDate());
            dateTextView.setTextColor(Color.BLACK);
            newRow.addView(dateTextView);

            TextView amountTextView = new TextView(this);
            amountTextView.setGravity(Gravity.CENTER);
            amountTextView.setText(String.format(Locale.ENGLISH, "%.2f", newTrans.getAmount()));
            amountTextView.setTextColor(Color.BLACK);
            newRow.addView(amountTextView);

            TextView categoryTextView = new TextView(this);
            categoryTextView.setGravity(Gravity.CENTER);
            categoryTextView.setText(newTrans.getCategory());
            categoryTextView.setTextColor(Color.BLACK);
            newRow.addView(categoryTextView);

            TextView noteTextView = new TextView(this);
            noteTextView.setGravity(Gravity.CENTER);
            noteTextView.setText(newTrans.getNote());
            noteTextView.setTextColor(Color.BLACK);
            newRow.addView(noteTextView);

            transTable.addView(newRow);
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
