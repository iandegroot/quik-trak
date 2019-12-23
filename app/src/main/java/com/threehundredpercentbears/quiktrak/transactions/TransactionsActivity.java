package com.threehundredpercentbears.quiktrak.transactions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.threehundredpercentbears.quiktrak.addtransaction.AddTransactionActivity;
import com.threehundredpercentbears.quiktrak.utils.monthlytransactions.MonthlyTransactionsHelper;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.util.Calendar;
import java.util.List;

public class TransactionsActivity extends Fragment {

    private TransactionsViewModel transactionsViewModel;

    private MonthlyTransactionsHelper monthlyTransactionsHelper = new MonthlyTransactionsHelper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_transactions, container, false);
    }

    // This only runs once, the first time this activity is started
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setContentView(R.layout.activity_transactions);
//        Toolbar toolbar = getView().findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Calendar cal = Calendar.getInstance();

        final ImageButton buttonEarlierMonth = getView().findViewById(R.id.transactionsEarlierMonthButton);
        final TextView monthTextView = getView().findViewById(R.id.transactionsMonthTextView);
        final ImageButton buttonLaterMonth = getView().findViewById(R.id.transactionsLaterMonthButton);

        EmptyMessageRecyclerView recyclerView = getView().findViewById(R.id.transactionsRecyclerView);
        final TransactionsAdapter adapter = new TransactionsAdapter(getContext(), createRecyclerViewItemClickListener(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyMessageView(getView().findViewById(R.id.transactionsEmptyRecyclerViewTextView));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        TransactionsViewModelFactory factory = new TransactionsViewModelFactory(getActivity().getApplication());
        transactionsViewModel = new ViewModelProvider(this, factory).get(TransactionsViewModel.class);

        transactionsViewModel.getTransactionsForMonth().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable final List<Transaction> transactions) {
                // Update the cached copy of the words in the adapter.
                adapter.setTransactions(transactions);
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

//        FloatingActionButton fab = getView().findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(TransactionsActivity.this, AddTransactionActivity.class));
//            }
//        });
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
}
