package com.threehundredpercentbears.quiktrak.categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private final LayoutInflater inflater;
    private final OnItemClickListener<Category> listener;
    private List<Category> categories;

    public CategoriesAdapter(Context context, OnItemClickListener<Category> listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.categories_row_item, parent, false);
        return new CategoriesAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (categories != null) {
            Category currentCat = categories.get(position);
            holder.categoryTextView.setText(currentCat.getCategoryName());
            holder.bind(currentCat, listener);
        }
        // TODO: Add else to set rows to default values if no transactions is null
    }

    void setCategories(List<Category> categories){
        this.categories = categories;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // categories has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (categories != null)
            return categories.size();
        else return 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTextView;

        private CategoryViewHolder(View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.recyclerViewRowItemTextView);
        }

        private void bind(final Category item, final OnItemClickListener<Category> listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

//        @Override
//        public void onClick(View view) {
//            new AlertDialog.Builder(view.getContext())
//                    .setMessage("Are you sure you want to delete the category '" + categoryTextView.getText().toString() + "'?\n" +
//                            "All transactions of that category will also be deleted.")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            if (removeCategoryAndRow(getAdapterPosition())) {
//                                deleteAllCategoryTransactions(categoryTextView.getText().toString());
//                            }
//                        }
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        }
    }
}
