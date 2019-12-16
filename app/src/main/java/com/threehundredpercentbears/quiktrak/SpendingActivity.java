package com.threehundredpercentbears.quiktrak;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SpendingActivity extends AppCompatActivity {

    private SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SpendingViewModel spendingViewModel;

    Button quickOpCatButton1;
    Button quickOpCatButton2;
    Button quickOpCatButton3;
    Button quickOpCatButton4;

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

        final ImageButton buttonEarlierMonth = findViewById(R.id.buttonEarlierMonth);
        final TextView monthTextView = findViewById(R.id.monthTextView);
        final ImageButton buttonLaterMonth = findViewById(R.id.buttonLaterMonth);

        RecyclerView recyclerView = findViewById(R.id.spendingRecyclerView);
        final SpendingAdapter adapter = new SpendingAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SpendingViewModelFactory factory = new SpendingViewModelFactory(this.getApplication());
        spendingViewModel = new ViewModelProvider(this, factory).get(SpendingViewModel.class);

        spendingViewModel.getTransactionsForMonth().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable final List<Transaction> transactionsForMonth) {
                // Update the cached copy of the words in the adapter.
                adapter.setCategorySpendings(spendingViewModel.groupByCategoryAndSort(transactionsForMonth));
                showTotalSpendingForMonth();
            }
        });

        quickOpCatButton1 = findViewById(R.id.quickOpCategoryButton1);
        quickOpCatButton2 = findViewById(R.id.quickOpCategoryButton2);
        quickOpCatButton3 = findViewById(R.id.quickOpCategoryButton3);
        quickOpCatButton4 = findViewById(R.id.quickOpCategoryButton4);

        spendingViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                setupQuikAddButtons(categories);
            }
        });

        buttonEarlierMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, -1);
                monthTextView.setText(format.format(cal.getTime()));
                updateMonthFilter(cal);
            }
        });

        monthTextView.setText(format.format(cal.getTime()));

        buttonLaterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, 1);
                monthTextView.setText(format.format(cal.getTime()));
                updateMonthFilter(cal);
            }
        });

        updateMonthFilter(cal);
    }

    private View.OnClickListener createOnClickListenerForQuickOpCatButton(final String category) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpendingActivity.this, AddTransactionActivity.class);
                intent.putExtra(Constants.CATEGORY_EXTRA_NAME, category);
                startActivity(intent);
            }
        };
    }

    private void showTotalSpendingForMonth() {
        final TextView totalTextView = findViewById(R.id.totalTextView);

        totalTextView.setText(String.format("Total: %s", CurrencyFormatter.createCurrencyFormattedString(getSpendingTotal())));
    }

    private void updateMonthFilter(Calendar cal) {
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.setTime(cal.getTime());
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.setTime(cal.getTime());
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, lastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        spendingViewModel.updateMonthFilter(firstDayOfMonth.getTime(), lastDayOfMonth.getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();

        showTotalSpendingForMonth();
    }

    private void setupQuikAddButtons(List<Category> categories) {
        quickOpCatButton1.setText(categories.get(0).getCategoryName());
        quickOpCatButton2.setText(categories.get(1).getCategoryName());
        quickOpCatButton3.setText(categories.get(2).getCategoryName());
        quickOpCatButton4.setText(categories.get(3).getCategoryName());

        quickOpCatButton1.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton1.getText().toString()));
        quickOpCatButton2.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton2.getText().toString()));
        quickOpCatButton3.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton3.getText().toString()));
        quickOpCatButton4.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton4.getText().toString()));
    }

    private int getSpendingTotal() {
        int total = 0;

        List<Transaction> transactions = spendingViewModel.getTransactionsForMonth().getValue();

        if (transactions != null) {
            for (Transaction trans : transactions) {
                total += trans.getAmount();
            }
        }

        return total;
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
        } else if (id == R.id.categories_page) {
            startActivity(new Intent(SpendingActivity.this, CategoriesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
