package com.threehundredpercentbears.quiktrak.categories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.utils.OnStartDragListener;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private final LayoutInflater inflater;
    private final OnItemClickListener<Category> clickListener;
    private final OnStartDragListener dragStartListener;
    private List<Category> categories;

    public CategoriesAdapter(Context context, OnItemClickListener<Category> clickListener, OnStartDragListener dragStartListener) {
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
        this.dragStartListener = dragStartListener;
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
            final Category currentCat = categories.get(position);
            holder.categoryTextView.setText(currentCat.getCategoryName());
            holder.bind(dragStartListener);
            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(currentCat);
                }
            });
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
        ImageView deleteImageView;
        ImageView dragHandleImageView;

        private CategoryViewHolder(View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.recyclerViewRowItemTextView);
            deleteImageView = itemView.findViewById(R.id.categoriesDeleteIcon);
            dragHandleImageView = itemView.findViewById(R.id.categoriesDragHandle);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void bind(final OnStartDragListener startDragListener) {

            dragHandleImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        startDragListener.onStartDrag(CategoryViewHolder.this);
                    }
                    return false;
                }
            });
        }
    }
}
