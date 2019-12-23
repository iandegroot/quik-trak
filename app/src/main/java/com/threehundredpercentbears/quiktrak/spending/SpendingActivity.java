package com.threehundredpercentbears.quiktrak.spending;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.threehundredpercentbears.quiktrak.addtransaction.AddTransactionActivity;
import com.threehundredpercentbears.quiktrak.categories.CategoriesActivity;
import com.threehundredpercentbears.quiktrak.home.HomeActivity;
import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.Constants;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsHelper;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.formatters.CurrencyFormatter;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;
import com.threehundredpercentbears.quiktrak.transactions.TransactionsActivity;

import java.util.Calendar;
import java.util.List;

public class SpendingActivity extends Fragment {

    private SpendingViewModel spendingViewModel;

    private MonthlyTransactionsHelper monthlyTransactionsHelper = new MonthlyTransactionsHelper();

    private Button quickOpCatButton1;
    private Button quickOpCatButton2;
    private Button quickOpCatButton3;
    private Button quickOpCatButton4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_spending, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setContentView(R.layout.activity_spending);
//        Toolbar toolbar = getView().findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = getView().findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SpendingActivity.this, AddTransactionActivity.class));
//            }
//        });

        final Calendar cal = Calendar.getInstance();

        final ImageButton buttonEarlierMonth = getView().findViewById(R.id.buttonEarlierMonth);
        final TextView monthTextView = getView().findViewById(R.id.monthTextView);
        final ImageButton buttonLaterMonth = getView().findViewById(R.id.buttonLaterMonth);

        EmptyMessageRecyclerView recyclerView = getView().findViewById(R.id.spendingRecyclerView);
        final SpendingAdapter adapter = new SpendingAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyMessageView(getView().findViewById(R.id.spendingEmptyRecyclerViewTextView));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        SpendingViewModelFactory factory = new SpendingViewModelFactory(getActivity().getApplication());
        spendingViewModel = new ViewModelProvider(this, factory).get(SpendingViewModel.class);

        spendingViewModel.getTransactionsForMonth().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable final List<Transaction> transactionsForMonth) {
                // Update the cached copy of the words in the adapter.
                adapter.setCategorySpendings(spendingViewModel.groupByCategoryAndSort(transactionsForMonth));
                showTotalSpendingForMonth();
            }
        });

        quickOpCatButton1 = getView().findViewById(R.id.quickOpCategoryButton1);
        quickOpCatButton2 = getView().findViewById(R.id.quickOpCategoryButton2);
        quickOpCatButton3 = getView().findViewById(R.id.quickOpCategoryButton3);
        quickOpCatButton4 = getView().findViewById(R.id.quickOpCategoryButton4);

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
                monthTextView.setText(monthlyTransactionsHelper.getFormat().format(cal.getTime()));
                monthlyTransactionsHelper.updateMonthFilter(spendingViewModel, cal);
            }
        });

        monthTextView.setText(monthlyTransactionsHelper.getFormat().format(cal.getTime()));

        buttonLaterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, 1);
                monthTextView.setText(monthlyTransactionsHelper.getFormat().format(cal.getTime()));
                monthlyTransactionsHelper.updateMonthFilter(spendingViewModel, cal);
            }
        });

        monthlyTransactionsHelper.updateMonthFilter(spendingViewModel, cal);
    }

    private View.OnClickListener createOnClickListenerForQuickOpCatButton(final String category) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                intent.putExtra(Constants.CATEGORY_EXTRA_NAME, category);
                startActivity(intent);
            }
        };
    }

    private void showTotalSpendingForMonth() {
        final TextView totalTextView = getView().findViewById(R.id.totalTextView);

        totalTextView.setText(String.format("Total: %s", CurrencyFormatter.createCurrencyFormattedString(getSpendingTotal())));
    }

    @Override
    public void onStart() {
        super.onStart();

        showTotalSpendingForMonth();
    }

    private void setupQuikAddButtons(List<Category> categories) {
        if (categories.size() >= 4) {
            quickOpCatButton1.setText(categories.get(0).getCategoryName());
            quickOpCatButton2.setText(categories.get(1).getCategoryName());
            quickOpCatButton3.setText(categories.get(2).getCategoryName());
            quickOpCatButton4.setText(categories.get(3).getCategoryName());

            quickOpCatButton1.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton1.getText().toString()));
            quickOpCatButton2.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton2.getText().toString()));
            quickOpCatButton3.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton3.getText().toString()));
            quickOpCatButton4.setOnClickListener(createOnClickListenerForQuickOpCatButton(quickOpCatButton4.getText().toString()));
        }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.transactions_page) {
//            startActivity(new Intent(SpendingActivity.this, TransactionsActivity.class));
//            return true;
//        } else if (id == R.id.categories_page) {
//            startActivity(new Intent(SpendingActivity.this, CategoriesActivity.class));
//            return true;
//        } else if (id == R.id.home_page) {
//        startActivity(new Intent(SpendingActivity.this, HomeActivity.class));
//        return true;
//    }
//
//        return super.onOptionsItemSelected(item);
//    }
}
