package com.threepbears.quiktrak;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText dateEditText = findViewById(R.id.dateEditText);
        final EditText amountEditText = findViewById(R.id.amountEditText);
        final Spinner categorySpinner = findViewById(R.id.categorySpinner);
        final EditText noteEditText = findViewById(R.id.noteEditText);
        final Button addTransButton = findViewById(R.id.addTransactionButton);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePicker = new DatePickerDialog(AddTransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        final String date = (month+1) + "/" + day + "/" + year;
                        dateEditText.setText(date);
                    }
                }, year, month, day);

                datePicker.show();
            }
        });

        addTransButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTransactionActivity.this, MainActivity.class);

                intent.putExtra(getString(R.string.date_text_view), dateEditText.getText().toString());
                intent.putExtra(getString(R.string.amount_text_view), Float.parseFloat(amountEditText.getText().toString()));
                intent.putExtra(getString(R.string.category_text_view), categorySpinner.getSelectedItem().toString());
                intent.putExtra(getString(R.string.note_text_view), noteEditText.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



}
