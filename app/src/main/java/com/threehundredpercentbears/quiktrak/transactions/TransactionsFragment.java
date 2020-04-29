package com.threehundredpercentbears.quiktrak.transactions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.threehundredpercentbears.quiktrak.addtransaction.AddTransactionActivity;
import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.Constants;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsHelper;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private TransactionsViewModel transactionsViewModel;

    private MonthlyTransactionsHelper monthlyTransactionsHelper = new MonthlyTransactionsHelper();

    private List<String> allCategoryNames = new ArrayList<>();
    private Spinner categoriesFilterSpinner;
    private TransactionsAdapter transactionsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.content_transactions, container, false);
    }

    // This only runs once, the first time this activity is started
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Calendar cal = Calendar.getInstance();

        final ImageButton buttonEarlierMonth = getView().findViewById(R.id.transactionsEarlierMonthButton);
        final TextView monthTextView = getView().findViewById(R.id.transactionsMonthTextView);
        final ImageButton buttonLaterMonth = getView().findViewById(R.id.transactionsLaterMonthButton);

        EmptyMessageRecyclerView recyclerView = getView().findViewById(R.id.transactionsRecyclerView);
        transactionsAdapter = new TransactionsAdapter(getContext(), createRecyclerViewItemClickListener(getContext()));
        recyclerView.setAdapter(transactionsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyMessageView(getView().findViewById(R.id.transactionsEmptyRecyclerViewTextView));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        TransactionsViewModelFactory factory = new TransactionsViewModelFactory(getActivity().getApplication());
        transactionsViewModel = new ViewModelProvider(this, factory).get(TransactionsViewModel.class);

        transactionsViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> categories) {
                setupCategoryFilterSpinner(categories);
            }
        });

        transactionsViewModel.getTransactionsForMonth().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable final List<Transaction> transactions) {
                // Update the cached copy of the words in the adapter.
                transactionsAdapter.setTransactions(transactions);
            }
        });

        buttonEarlierMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, -1);
                monthTextView.setText(monthlyTransactionsHelper.getFormat().format(cal.getTime()));
                monthlyTransactionsHelper.updateMonthFilter(transactionsViewModel, cal);
            }
        });

        monthTextView.setText(monthlyTransactionsHelper.getFormat().format(cal.getTime()));

        buttonLaterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.MONTH, 1);
                monthTextView.setText(monthlyTransactionsHelper.getFormat().format(cal.getTime()));
                monthlyTransactionsHelper.updateMonthFilter(transactionsViewModel, cal);
            }
        });

        monthlyTransactionsHelper.updateMonthFilter(transactionsViewModel, cal);
    }

    private OnItemClickListener<Transaction> createRecyclerViewItemClickListener(final Context context) {
        return new OnItemClickListener<Transaction>() {

            @Override
            public void onItemClick(final Transaction transaction) {

                new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete the transaction?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            transactionsViewModel.deleteTransaction(transaction.getId());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        };
    }

    private void setupCategoryFilterSpinner(List<Category> categories) {
        allCategoryNames.clear();
        allCategoryNames.add(Constants.ALL_CATEGORIES);

        for (Category category : categories) {
            allCategoryNames.add(category.getCategoryName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, allCategoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesFilterSpinner.setAdapter(adapter);

        categoriesFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                transactionsAdapter.getFilter().filter(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transactions, menu);

        MenuItem item = menu.findItem(R.id.transactionsCategoryFilterSpinner);
        categoriesFilterSpinner = (Spinner) item.getActionView();
    }
}
