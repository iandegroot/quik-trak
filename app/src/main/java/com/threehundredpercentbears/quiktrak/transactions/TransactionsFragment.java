package com.threehundredpercentbears.quiktrak.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.threehundredpercentbears.quiktrak.utils.SharedCategoryToFilterViewModel;
import com.threehundredpercentbears.quiktrak.utils.SharedCategoryToFilterViewModelFactory;
import com.threehundredpercentbears.quiktrak.utils.ViewPagerFragmentLifecycle;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsHelper;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionsFragment extends Fragment implements ViewPagerFragmentLifecycle {

    private TransactionsViewModel transactionsViewModel;
    private SharedCategoryToFilterViewModel categoryToFilterViewModel;

    private MonthlyTransactionsHelper monthlyTransactionsHelper = new MonthlyTransactionsHelper();

    private List<String> allCategoryNames = new ArrayList<>();
    private Spinner categoriesFilterSpinner;
    private TextView monthTextView;
    private TransactionsAdapter transactionsAdapter;
    private String currentCategory = Constants.ALL_CATEGORIES;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.content_transactions, container, false);
    }

    // This only runs once, the first time this activity is started
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ImageButton buttonEarlierMonth = getView().findViewById(R.id.transactionsEarlierMonthButton);
        monthTextView = getView().findViewById(R.id.transactionsMonthTextView);
        final ImageButton buttonLaterMonth = getView().findViewById(R.id.transactionsLaterMonthButton);

        EmptyMessageRecyclerView recyclerView = getView().findViewById(R.id.transactionsRecyclerView);
        transactionsAdapter = new TransactionsAdapter(getContext(), createRecyclerViewItemClickListener());
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
                transactionsAdapter.getFilter().filter(currentCategory);
            }
        });

        SharedCategoryToFilterViewModelFactory sharedVMFactory = new SharedCategoryToFilterViewModelFactory();
        categoryToFilterViewModel = new ViewModelProvider(requireActivity(), sharedVMFactory).get(SharedCategoryToFilterViewModel.class);
        final Calendar calendar = getCalendar();

        buttonEarlierMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                setActiveMonth();
            }
        });

        buttonLaterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                setActiveMonth();
            }
        });

        setActiveMonth();
    }

    private Calendar getCalendar() {
        Calendar spendingCalendar = categoryToFilterViewModel.getCalendar();
        if (spendingCalendar == null) {
            return Calendar.getInstance();
        } else {
            return spendingCalendar;
        }
    }

    private void setActiveMonth() {
        Date selectedDate = categoryToFilterViewModel.getCalendar().getTime();

        monthTextView.setText(monthlyTransactionsHelper.getFormat().format(selectedDate));
        monthlyTransactionsHelper.updateMonthFilter(transactionsViewModel, selectedDate);
    }

    private OnItemClickListener<Transaction> createRecyclerViewItemClickListener() {
        return new OnItemClickListener<Transaction>() {

            @Override
            public void onItemClick(final Transaction transaction) {
                Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                intent.putExtra(Constants.UPDATING_TRANSACTION_EXTRA_NAME, transaction);
                startActivity(intent);
            }
        };
    }

    private void setupCategoryFilterSpinner(List<Category> categories) {
        allCategoryNames.clear();
        allCategoryNames.add(Constants.ALL_CATEGORIES);

        for (Category category : categories) {
            allCategoryNames.add(category.getCategoryName());
        }

        setupCategoryFilterSpinner();
    }

    private void setupCategoryFilterSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, allCategoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesFilterSpinner.setAdapter(adapter);

        categoriesFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = adapterView.getItemAtPosition(i).toString();
                transactionsAdapter.getFilter().filter(currentCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing here
            }
        });

        currentCategory = categoryToFilterViewModel.getCategoryToFilter();
        categoriesFilterSpinner.setSelection(adapter.getPosition(currentCategory));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transactions, menu);

        MenuItem item = menu.findItem(R.id.transactionsCategoryFilterSpinner);
        categoriesFilterSpinner = (Spinner) item.getActionView();
        categoriesFilterSpinner.setGravity(Gravity.END);

        // If allCategoryNames already has values then use those to populate the spinner,
        // otherwise we'll wait for the categories LiveData to be updated which will trigger
        // setting up the category filter spinner.
        setupCategoryFilterSpinnerIfCategoriesAvailable();
    }

    @Override
    public void onViewPagerResume() {
        setActiveMonth();
        setupCategoryFilterSpinnerIfCategoriesAvailable();
    }

    private void setupCategoryFilterSpinnerIfCategoriesAvailable() {
        if (allCategoryNames.size() > 0) {
            setupCategoryFilterSpinner();
        }
    }

    @Override
    public void onViewPagerPause() {
        categoryToFilterViewModel.setCategoryToFilter(Constants.ALL_CATEGORIES);
    }
}
