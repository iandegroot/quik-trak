package com.threehundredpercentbears.quiktrak.spending;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.threehundredpercentbears.quiktrak.addtransaction.AddTransactionActivity;
import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.Constants;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.utils.SharedCategoryToFilterViewModel;
import com.threehundredpercentbears.quiktrak.utils.SharedCategoryToFilterViewModelFactory;
import com.threehundredpercentbears.quiktrak.utils.ViewPagerFragmentLifecycle;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsHelper;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.formatters.CurrencyFormatter;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SpendingFragment extends Fragment implements ViewPagerFragmentLifecycle {

    private SpendingViewModel spendingViewModel;
    private SharedCategoryToFilterViewModel categoryToFilterViewModel;

    private MonthlyTransactionsHelper monthlyTransactionsHelper = new MonthlyTransactionsHelper();

    private Button quickOpCatButton1;
    private Button quickOpCatButton2;
    private Button quickOpCatButton3;
    private Button quickOpCatButton4;
    private TextView monthTextView;

    private Calendar calendar;
    private Date currentDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_spending, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        calendar = Calendar.getInstance();

        final ImageButton buttonEarlierMonth = getView().findViewById(R.id.buttonEarlierMonth);
        monthTextView = getView().findViewById(R.id.monthTextView);
        final ImageButton buttonLaterMonth = getView().findViewById(R.id.buttonLaterMonth);

        EmptyMessageRecyclerView recyclerView = getView().findViewById(R.id.spendingRecyclerView);
        final SpendingAdapter adapter = new SpendingAdapter(getContext(), createRecyclerViewItemClickListener(getContext()));
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

        SharedCategoryToFilterViewModelFactory sharedVMFactory = new SharedCategoryToFilterViewModelFactory();
        categoryToFilterViewModel = new ViewModelProvider(requireActivity(), sharedVMFactory).get(SharedCategoryToFilterViewModel.class);
        categoryToFilterViewModel.setCalendar(calendar);

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
                calendar.add(Calendar.MONTH, -1);
                setActiveMonth();
            }
        });

        currentDate = calendar.getTime();

        buttonLaterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                setActiveMonth();
            }
        });

        final TextView dayOfMonthTextView = getView().findViewById(R.id.dayOfMonthTextView);
        SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("EEE, MMM d", Locale.ENGLISH);
        dayOfMonthTextView.setText(dayOfMonthFormat.format(calendar.getTime()));
        dayOfMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.setTime(currentDate);
                setActiveMonth();
            }
        });

        setActiveMonth();
    }

    private void setActiveMonth() {
        Date selectedDate = calendar.getTime();

        monthTextView.setText(monthlyTransactionsHelper.getFormat().format(selectedDate));
        monthlyTransactionsHelper.updateMonthFilter(spendingViewModel, selectedDate);
    }

    private OnItemClickListener<CategorySpending> createRecyclerViewItemClickListener(final Context context) {
        return new OnItemClickListener<CategorySpending>() {

            @Override
            public void onItemClick(final CategorySpending categorySpending) {
                categoryToFilterViewModel.setCategoryToFilter(categorySpending.getCategory());

                TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
                tabLayout.getTabAt(Constants.TRANSACTIONS_TAB_POS).select();
            }
        };
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

    @Override
    public void onViewPagerResume() {
        setActiveMonth();
    }

    @Override
    public void onViewPagerPause() {

    }
}
