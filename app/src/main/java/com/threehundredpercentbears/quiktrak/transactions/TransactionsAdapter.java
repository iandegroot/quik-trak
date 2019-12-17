package com.threehundredpercentbears.quiktrak.transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.threehundredpercentbears.quiktrak.utils.formatters.CurrencyFormatter;
import com.threehundredpercentbears.quiktrak.utils.formatters.DateFormatter;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.models.transaction.Transaction;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    private final LayoutInflater inflater;
    private final OnItemClickListener<Transaction> listener;
    private List<Transaction> transactions;

    TransactionsAdapter(Context context, OnItemClickListener<Transaction> listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.transactions_row_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        if (transactions != null) {
            Transaction currentTrans = transactions.get(position);
            holder.dateTextView.setText(DateFormatter.dateToString(currentTrans.getDate()));
            holder.amountTextView.setText(CurrencyFormatter.createCurrencyFormattedString(currentTrans.getAmount()));
            holder.categoryTextView.setText(currentTrans.getCategory());
            holder.noteTextView.setText(currentTrans.getNote());
            holder.bind(currentTrans, listener);
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

        private void bind(final Transaction item, final OnItemClickListener<Transaction> listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
