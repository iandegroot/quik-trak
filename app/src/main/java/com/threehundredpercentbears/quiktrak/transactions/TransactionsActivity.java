package com.threehundredpercentbears.quiktrak.transactions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.threehundredpercentbears.quiktrak.addtransaction.AddTransactionActivity;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private TransactionsViewModel transactionsViewModel;

    // This only runs once, the first time this activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EmptyMessageRecyclerView recyclerView = findViewById(R.id.transactionsRecyclerView);
        final TransactionsAdapter adapter = new TransactionsAdapter(this, createRecyclerViewItemClickListener(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEmptyMessageView(findViewById(R.id.transactionsEmptyRecyclerViewTextView));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        TransactionsViewModelFactory factory = new TransactionsViewModelFactory(this.getApplication());
        transactionsViewModel = new ViewModelProvider(this, factory).get(TransactionsViewModel.class);

        transactionsViewModel.getAllTransactions().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable final List<Transaction> transactions) {
                // Update the cached copy of the words in the adapter.
                adapter.setTransactions(transactions);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionsActivity.this, AddTransactionActivity.class));
            }
        });
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
