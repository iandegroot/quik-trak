package com.threehundredpercentbears.quiktrak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private final LayoutInflater inflater;
    private List<Category> categories;
//    private final CategoryRoomDatabase categoryDB;
//    private final TransactionRoomDatabase transDB;

    public CategoriesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
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
            holder.categoryTextView.setText(categories.get(position).getCategoryName());
        }
        // TODO: Add else to set rows to default values if no transactions is null
    }

//    @NonNull
//    @Override
//    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        View view = layoutInflater.inflate(R.layout.categories_row_item, parent, false);
//        return new CategoryViewHolder(view);
//    }

//    @Override
//    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
//        holder.categoryTextView.setText(categories.get(position).getCategoryName());
//    }

    void setCategories(List<Category> categories){
        this.categories = categories;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // transactions has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (categories != null)
            return categories.size();
        else return 0;
    }

//    @Override
//    public int getItemCount() {
//        return categories.size();
//    }

//    private boolean removeCategoryAndRow(int position) {
//        if (deleteCategory(categories.get(position).getId())) {
//            removeRow(position);
//            return true;
//        }
//
//        return false;
//    }
//
//    private boolean deleteCategory(int id) {
//
//        if (!checkForCategoryInDB(id).isEmpty()) {
//            deleteCategoryFromDB(id);
//            return true;
//        }
//
//        return false;
//    }
//
//    private void deleteCategoryFromDB(int id) {
//        CategoryDao categoryDao = categoryDB.categoryDao();
//
//        categoryDao.deleteCategory(id);
//    }
//
//    private List<Category> checkForCategoryInDB(int id) {
//        CategoryDao categoryDao = categoryDB.categoryDao();
//
//        return categoryDao.getCategory(id);
//    }
//
//    private void deleteAllCategoryTransactions(String category) {
//        TransactionDao transDao = transDB.transactionDao();
//
//        transDao.deleteAllCategoryTransactions(category);
//    }
//
//    private void removeRow(int position) {
//        categories.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, categories.size());
//    }

    class CategoryViewHolder extends RecyclerView.ViewHolder { //implements View.OnClickListener {

        TextView categoryTextView;

        private CategoryViewHolder(View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.recyclerViewRowItemTextView);

//            itemView.setOnClickListener(this);
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
