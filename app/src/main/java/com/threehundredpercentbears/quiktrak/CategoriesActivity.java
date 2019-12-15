package com.threehundredpercentbears.quiktrak;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private CategoriesViewModel categoriesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button addCategoryButton = findViewById(R.id.addCategoryButton);
        final EditText addCategoryEditText = findViewById(R.id.addCategoryEditText);
        addCategoryButton.requestFocus();

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCategory(addCategoryEditText.getText().toString());

                closeKeyboardAndClearEditText(addCategoryEditText);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.categoriesRecyclerView);
        final CategoriesAdapter adapter = new CategoriesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CategoriesViewModelFactory factory = new CategoriesViewModelFactory(this.getApplication());
        categoriesViewModel = new ViewModelProvider(this, factory).get(CategoriesViewModel.class);

        categoriesViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> categories) {
                // Update the cached copy of the words in the adapter.
                adapter.setCategories(categories);
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addNewCategory(String categoryName) {
        // Dividing the current time by 1000 to make it fit into an int and using that as
        // the id of the transaction
        Category newCategory = new Category((int) (new Date().getTime() / 1000), categoryName, categoriesViewModel.getAllCategories().getValue().size());

        categoriesViewModel.insert(newCategory);
    }

    private void closeKeyboardAndClearEditText(EditText addCategoryEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addCategoryEditText.getWindowToken(), 0);
        addCategoryEditText.getText().clear();
    }

    @Override
    protected void onPause() {
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
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
