package com.threepbears.quiktrak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private ArrayList<Category> categories = new ArrayList<>();
    private CategoryRoomDatabase categoryDB;

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

        categoryDB = CategoryRoomDatabase.getDatabase(this);
    }

    private void addNewCategory(String categoryName) {
        TableLayout categoryTable = findViewById(R.id.categoriesTable);

        // Dividing the current time by 1000 to make it fit into an int and using that as
        // the id of the transaction
        Category newCategory = new Category((int) (new Date().getTime() / 1000), categoryName);

        categories.add(newCategory);
        addCategoryRow(categoryTable, newCategory);
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

        showAllCategories();
    }

    private void showAllCategories() {
        readAllCategoriesFromDB();
        addAllCategoryRows();
    }

    private void readAllCategoriesFromDB() {
        CategoryDao categoryDao = categoryDB.categoryDao();

        categories.clear();
        categories.addAll(categoryDao.getAllCategories());
    }

    private void addAllCategoryRows() {
        TableLayout categoryTable = findViewById(R.id.categoriesTable);

        for (Category category : categories) {
            addCategoryRow(categoryTable, category);
        }
    }

    private void addCategoryRow(TableLayout categoryTable, Category category) {
        final TableRow newRow = new TableRow(this);

        newRow.setId(category.getId());

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        final TextView amountTextView = new TextView(this);
        amountTextView.setGravity(Gravity.CENTER);
        amountTextView.setText(category.getCategoryName());
        amountTextView.setLayoutParams(layoutParams);
        newRow.addView(amountTextView);

        newRow.setMinimumHeight(Constants.MIN_ROW_HEIGHT);
        newRow.setGravity(Gravity.CENTER);
        newRow.setLayoutParams(layoutParams);
        newRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage("Are you sure you want to delete the category?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeCategoryAndRow(newRow.getId());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        categoryTable.addView(newRow);
    }

    private void removeCategoryAndRow(int transId) {
        if (deleteCategory(transId)) {
            removeAllCategoryRows();
            addAllCategoryRows();
        }
    }

    private boolean deleteCategory(int id) {

        if (!checkForCategoryInDB(id).isEmpty()) {
            deleteCategoryFromDB(id);
            readAllCategoriesFromDB();
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

    @Override
    protected void onStop() {
        super.onStop();

        categories.clear();
        clearCategoriesRowsAndData();
    }

    private void clearCategoriesRowsAndData() {
        categories.clear();
        removeAllCategoryRows();
    }

    private void removeAllCategoryRows() {
        TableLayout categoriesTable = findViewById(R.id.categoriesTable);

        categoriesTable.removeAllViews();
    }

}
