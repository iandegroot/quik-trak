package com.threehundredpercentbears.quiktrak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<Category> categoriesList;
    private final CategoryRoomDatabase categoryDB;
    private final TransactionRoomDatabase transDB;

    public RecyclerViewAdapter(List<Category> categoriesList, CategoryRoomDatabase categoryDB, TransactionRoomDatabase transDB) {
        this.categoriesList = Objects.requireNonNull(categoriesList);
        this.categoryDB = Objects.requireNonNull(categoryDB);
        this.transDB = Objects.requireNonNull(transDB);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_view_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryTextView.setText(categoriesList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoryTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.recyclerViewRowItemTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage("Are you sure you want to delete the category '" + categoryTextView.getText().toString() + "'?\n" +
                            "All transactions of that category will also be deleted.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (removeCategoryAndRow(getAdapterPosition())) {
                                deleteAllCategoryTransactions(categoryTextView.getText().toString());
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private boolean removeCategoryAndRow(int position) {
        if (deleteCategory(categoriesList.get(position).getId())) {
            removeRow(position);
            return true;
        }

        return false;
    }

    private boolean deleteCategory(int id) {

        if (!checkForCategoryInDB(id).isEmpty()) {
            deleteCategoryFromDB(id);
            return true;
        }

        return false;
    }

    private void deleteCategoryFromDB(int id) {
        CategoryDao categoryDao = categoryDB.categoryDao();

        categoryDao.deleteCategory(id);
    }

    private List<Category> checkForCategoryInDB(int id) {
        CategoryDao categoryDao = categoryDB.categoryDao();

        return categoryDao.getCategory(id);
    }

    private void deleteAllCategoryTransactions(String category) {
        TransactionDao transDao = transDB.transactionDao();

        transDao.deleteAllCategoryTransactions(category);
    }

    private void removeRow(int position) {
        categoriesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, categoriesList.size());
    }
}
