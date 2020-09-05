package com.threehundredpercentbears.quiktrak.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.threehundredpercentbears.quiktrak.categories.CategoriesFragment;
import com.threehundredpercentbears.quiktrak.spending.SpendingFragment;
import com.threehundredpercentbears.quiktrak.transactions.TransactionsFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_LAYOUT_SIZE = 3;

    private Fragment spendingFragment;
    private Fragment transactionsFragment;
    private Fragment categoriesFragment;

    HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull @Override public Fragment createFragment(int position) {
        if (position == 0) {
            spendingFragment = new SpendingFragment();
            return spendingFragment;
        } else if (position == 1) {
            transactionsFragment = new TransactionsFragment();
            return transactionsFragment;
        } else {
            categoriesFragment = new CategoriesFragment();
            return categoriesFragment;
        }
    }

    @Override public int getItemCount() {
        return TAB_LAYOUT_SIZE;
    }

    Fragment getFragment(int position) {
        if (position == 0) {
            return spendingFragment;
        } else if (position == 1) {
            return transactionsFragment;
        } else {
            return categoriesFragment;
        }
    }
}
