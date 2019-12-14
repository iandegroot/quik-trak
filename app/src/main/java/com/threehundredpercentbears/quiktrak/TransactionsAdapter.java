package com.threehundredpercentbears.quiktrak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView amountTextView;
        private final TextView categoryTextView;
        private final TextView noteTextView;

        private TransactionViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
        }
    }

    private final LayoutInflater inflater;
    private List<Transaction> transactions; // Cached copy of transactions

    TransactionsAdapter(Context context) { inflater = LayoutInflater.from(context); }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.transactions_row_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        if (transactions != null) {
            Transaction currentTrans = transactions.get(position);
            holder.dateTextView.setText(DateFormatter.dateToString(currentTrans.getDate()));
            holder.amountTextView.setText(CurrencyFormatter.createCurrencyFormattedString(currentTrans.getAmount()));
            holder.categoryTextView.setText(currentTrans.getCategory());
            holder.noteTextView.setText(currentTrans.getNote());
        }
        // TODO: Add else to set rows to default values if no transactions is null
    }

    void setTransactions(List<Transaction> transactions){
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // transactions has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (transactions != null)
            return transactions.size();
        else return 0;
    }
}
