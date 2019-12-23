package com.threehundredpercentbears.quiktrak.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomeViewPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_LAYOUT_SIZE = 3;

    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull @Override public Fragment createFragment(int position) {
        return CardFragment.newInstance(position);
    }

    @Override public int getItemCount() {
        return TAB_LAYOUT_SIZE;
    }
}
