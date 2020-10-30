package com.splitter.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.splitter.Fragments.ChatsFragment;
import com.splitter.Fragments.UsersFragment;
import com.splitter.Fragments.SecondFragment;

public class PagerAdapter extends FragmentStateAdapter {
    private int numOfTabs;
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, int numOfTabs) {
        super(fragmentActivity);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new SecondFragment();
            case 2:
                return new UsersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
