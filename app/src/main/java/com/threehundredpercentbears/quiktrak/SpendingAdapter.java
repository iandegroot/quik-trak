package com.threehundredpercentbears.quiktrak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpendingAdapter extends RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder> {

    private final LayoutInflater inflater;
    private List<CategorySpending> categorySpendings;

    SpendingAdapter(Context context) { inflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public SpendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.spending_row_item, parent, false);
        return new SpendingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingViewHolder holder, int position) {
        if (categorySpendings != null) {
            CategorySpending currentSpending = categorySpendings.get(position);
            holder.categoryTextView.setText(currentSpending.getCategory());
            holder.amountTextView.setText(CurrencyFormatter.createCurrencyFormattedString(currentSpending.getAmount()));
        }
        // TODO: Add else to set rows to default values if no categorySpendings is null
    }

    void setCategorySpendings(List<CategorySpending> categorySpendings){
        this.categorySpendings = categorySpendings;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // categorySpendings has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (categorySpendings != null)
            return categorySpendings.size();
        else return 0;
    }

    class SpendingViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTextView;
        private final TextView amountTextView;

        private SpendingViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.spendingCategoryTextView);
            amountTextView = itemView.findViewById(R.id.spendingTotalByCategoryTextView);
        }
    }
}