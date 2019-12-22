package com.threehundredpercentbears.quiktrak.addtransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.Constants;
import com.threehundredpercentbears.quiktrak.utils.formatters.DateFormatter;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText dateEditText;
    private EditText amountEditText;
    private Spinner categorySpinner;
    private EditText noteEditText;
    private Button addTransButton;

    private AddTransactionViewModel addTransactionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateEditText = findViewById(R.id.dateEditText);
        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        noteEditText = findViewById(R.id.noteEditText);
        addTransButton = findViewById(R.id.addTransactionButton);

        AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(this.getApplication());
        addTransactionViewModel = new ViewModelProvider(this, factory).get(AddTransactionViewModel.class);

        addTransactionViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> categories) {
                setupCategorySpinner(categories);
            }
        });

        // Disabling manual editing of the date field, must use the popup calendar
        dateEditText.setShowSoftInputOnFocus(false);
        dateEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setFocusable(false);

        Calendar cal = Calendar.getInstance();
        dateEditText.setText(DateFormatter.intsToStringDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
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
                        final String date = DateFormatter.intsToStringDate(day, month+1, year);
                        dateEditText.setText(date);
                    }
                }, year, month, day);

                datePicker.show();
            }
        });

        addTransButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTransaction();
                finish();
            }
        });

        amountEditText.requestFocus();
        amountEditText.addTextChangedListener(new CurrencyEditTextWatcher(amountEditText));
        amountEditText.setText("0");
    }

    private void addNewTransaction() {
        // Dividing the current time by 1000 to make it fit into an int and using that as
        // the id of the transaction
        Transaction newTrans = new Transaction((int) (new Date().getTime() / 1000),
                DateFormatter.stringToDate(dateEditText.getText().toString()),
                Integer.parseInt(amountEditText.getText().toString().replaceAll("[$,.]", "")),
                categorySpinner.getSelectedItem().toString(),
                noteEditText.getText().toString());

        addTransactionViewModel.insert(newTrans);
    }

    private void setupCategorySpinner(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getCategoryName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        String chosenCategory = intent.getStringExtra(Constants.CATEGORY_EXTRA_NAME);
        if (chosenCategory != null) {
            categorySpinner.setSelection(adapter.getPosition(chosenCategory));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addTransactionCheckMark) {
            addNewTransaction();
            finish();
            return true;
        }

        return false;
    }
}
