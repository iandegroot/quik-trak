package com.threehundredpercentbears.quiktrak.addtransaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
    private Button deleteTransButton;

    private AddTransactionViewModel addTransactionViewModel;

    private Transaction transactionToUpdate;
    private boolean isUpdating;

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
        deleteTransButton = findViewById(R.id.deleteTransactionButton);

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
                if (isUpdating) {
                    updateTransaction();
                } else {
                    addNewTransaction();
                }
                finish();
            }
        });

        deleteTransButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Are you sure you want to delete this transaction?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteTransaction();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        deleteTransButton.setVisibility(View.INVISIBLE);

        amountEditText.addTextChangedListener(new CurrencyEditTextWatcher(amountEditText));

        Intent intent = getIntent();
        transactionToUpdate = (Transaction) intent.getSerializableExtra(Constants.UPDATING_TRANSACTION_EXTRA_NAME);
        isUpdating = transactionToUpdate != null;

        if (isUpdating) {
            dateEditText.setText(DateFormatter.dateToString(transactionToUpdate.getDate()));
            amountEditText.setText(Integer.toString(transactionToUpdate.getAmount()));
            noteEditText.setText(transactionToUpdate.getNote());
            addTransButton.setText(R.string.update_transaction_button);
            deleteTransButton.setVisibility(View.VISIBLE);
            setTitle(R.string.title_activity_update_transaction);
        } else {
            amountEditText.requestFocus();
            amountEditText.setText("0");
        }
    }

    private void updateTransaction() {
        Transaction transToUpdate = new Transaction(transactionToUpdate.getId(),
                DateFormatter.stringToDate(dateEditText.getText().toString()),
                Integer.parseInt(amountEditText.getText().toString().replaceAll("[$,.]", "")),
                categorySpinner.getSelectedItem().toString(),
                noteEditText.getText().toString());

        addTransactionViewModel.updateTransaction(transToUpdate);
        showActionFinishedToast(transToUpdate, "updated");
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
        showActionFinishedToast(newTrans, "created");
    }

    private void deleteTransaction() {
        addTransactionViewModel.deleteTransaction(transactionToUpdate.getId());
        showActionFinishedToast(transactionToUpdate, "deleted");
    }

    private void showActionFinishedToast(Transaction transaction, String action) {
        Toast.makeText(getBaseContext(),
                String.format("Successfully %s '%s' transaction.", action, transaction.getCategory()),
                Toast.LENGTH_SHORT).show();
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

        if (isUpdating) {
            categorySpinner.setSelection(adapter.getPosition(transactionToUpdate.getCategory()));
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
            if (isUpdating) {
                updateTransaction();
            } else {
                addNewTransaction();
            }
            finish();
            return true;
        }

        return false;
    }
}
