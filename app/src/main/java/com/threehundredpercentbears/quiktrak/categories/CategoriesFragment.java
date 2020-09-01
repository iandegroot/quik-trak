package com.threehundredpercentbears.quiktrak.categories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.threehundredpercentbears.quiktrak.models.category.Category;
import com.threehundredpercentbears.quiktrak.utils.EmptyMessageRecyclerView;
import com.threehundredpercentbears.quiktrak.utils.OnItemClickListener;
import com.threehundredpercentbears.quiktrak.R;
import com.threehundredpercentbears.quiktrak.utils.OnStartDragListener;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private CategoriesViewModel categoriesViewModel;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_categories, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Button addCategoryButton = getView().findViewById(R.id.addCategoryButton);
        final EditText addCategoryEditText = getView().findViewById(R.id.addCategoryEditText);
        addCategoryButton.requestFocus();

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newCategoryName = addCategoryEditText.getText().toString();
                if (isValidCategoryName(newCategoryName)) {
                    addNewCategory(newCategoryName);
                    Toast.makeText(view.getContext(),
                            String.format("Successfully created new category '%s'", newCategoryName),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(),
                            String.format("Could not create category '%s'. Category already exists or is empty.", newCategoryName),
                            Toast.LENGTH_LONG).show();
                }

                closeKeyboardAndClearEditText(addCategoryEditText);
            }
        });

        EmptyMessageRecyclerView recyclerView = getView().findViewById(R.id.categoriesRecyclerView);
        final CategoriesAdapter adapter = new CategoriesAdapter(getContext(), createRecyclerViewItemClickListener(getContext()), createRecyclerViewStartDragListener());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyMessageView(getView().findViewById(R.id.categoriesEmptyRecyclerViewTextView));

        CategoriesViewModelFactory factory = new CategoriesViewModelFactory(getActivity().getApplication());
        categoriesViewModel = new ViewModelProvider(this, factory).get(CategoriesViewModel.class);

        categoriesViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> categories) {
                // Update the cached copy of the words in the adapter.
                adapter.setCategories(categories);
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private boolean isValidCategoryName(String newCategoryName) {

        if (newCategoryName.isEmpty()) {
            return false;
        }

        List<Category> categories = categoriesViewModel.getAllCategories().getValue();
        for (Category category : categories) {
            if (category.getCategoryName().equalsIgnoreCase(newCategoryName)) {
                return false;
            }
        }

        return true;
    }

    private void addNewCategory(String categoryName) {
        // Dividing the current time by 1000 to make it fit into an int and using that as
        // the id of the transaction
        Category newCategory = new Category((int) (new Date().getTime() / 1000), categoryName, categoriesViewModel.getAllCategories().getValue().size());

        categoriesViewModel.insert(newCategory);
    }

    private void closeKeyboardAndClearEditText(EditText addCategoryEditText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addCategoryEditText.getWindowToken(), 0);
        addCategoryEditText.getText().clear();
    }

    @Override
    public void onPause() {
        super.onPause();

        writeNewRankingsToDB();
    }

    private void writeNewRankingsToDB() {
        List<Category> categories = categoriesViewModel.getAllCategories().getValue();

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            category.setRank(i);
            categoriesViewModel.updateRank(category.getId(), category.getRank());
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(categoriesViewModel.getAllCategories().getValue(), fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private OnItemClickListener<Category> createRecyclerViewItemClickListener(final Context context) {
        return new OnItemClickListener<Category>() {

            @Override
            public void onItemClick(final Category category) {

                new AlertDialog.Builder(context)
                        .setMessage(String.format("Are you sure you want to delete the category '%s'?\n" +
                                "All transactions of that category will also be deleted.", category.getCategoryName()))
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                categoriesViewModel.deleteAllCategoryTransactions(category.getCategoryName());
                                categoriesViewModel.deleteCategory(category.getId());
                                Toast.makeText(context,
                                        String.format("Successfully deleted category '%s'", category.getCategoryName()),
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
    }

    private OnStartDragListener createRecyclerViewStartDragListener() {
        return new OnStartDragListener() {

            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        };
    }
}
