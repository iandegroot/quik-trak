package com.threehundredpercentbears.quiktrak;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CategoriesActivity extends AppCompatActivity {

    private ArrayList<Category> categories = new ArrayList<>();
    private CategoryRoomDatabase categoryDB;
    private TransactionRoomDatabase transDB;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryDB = CategoryRoomDatabase.getDatabase(this);
        transDB = TransactionRoomDatabase.getDatabase(this);

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

        recyclerView = findRecyclerView();
        readAllCategoriesFromDB();
        recyclerAdapter = new RecyclerViewAdapter(categories, categoryDB, transDB);
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addNewCategory(String categoryName) {
        // Dividing the current time by 1000 to make it fit into an int and using that as
        // the id of the transaction
        Category newCategory = new Category((int) (new Date().getTime() / 1000), categoryName);

        categories.add(newCategory);
        writeCategoryToDB(newCategory);
    }

    private void writeCategoryToDB(Category newCategory) {
        CategoryDao categoryDao = categoryDB.categoryDao();

        categoryDao.insert(newCategory);
    }

    private void closeKeyboardAndClearEditText(EditText addCategoryEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addCategoryEditText.getWindowToken(), 0);
        addCategoryEditText.getText().clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void readAllCategoriesFromDB() {
        CategoryDao categoryDao = categoryDB.categoryDao();

        categories.clear();
        categories.addAll(categoryDao.getAllCategories());
    }

    @Override
    protected void onStop() {
        super.onStop();

        clearCategoriesRowsAndData();
    }

    private void clearCategoriesRowsAndData() {
        categories.clear();
        removeAllCategoryRows();
    }

    private void removeAllCategoryRows() {
        findRecyclerView().removeAllViews();
    }

    private RecyclerView findRecyclerView() {
        return findViewById(R.id.categoriesRecyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(categories, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
