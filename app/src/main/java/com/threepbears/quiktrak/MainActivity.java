package com.threepbears.quiktrak;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

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

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        TextView textView = findViewById(R.id.testTextView);
        textView.setText(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView textView = findViewById(R.id.testTextView);

        StringBuilder sb = new StringBuilder();

        if (requestCode == ADD_TRANS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            transactions.add(new Transaction(data.getStringExtra(getString(R.string.date_text_view)),
                    data.getFloatExtra(getString(R.string.amount_text_view), 0),
                    data.getStringExtra(getString(R.string.category_text_view)),
                    data.getStringExtra(getString(R.string.note_text_view))));

            for (Transaction trans : transactions) {
                sb.append("Date: ");
                sb.append(trans.getDate());
                sb.append("\n");

                sb.append("Amount: ");
                sb.append(trans.getAmount());
                sb.append("\n");

                sb.append("Category: ");
                sb.append(trans.getCategory());
                sb.append("\n");

                sb.append("Note: ");
                sb.append(trans.getNote());
                sb.append("\n");
            }
            textView.setText(sb.toString());
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
