package com.splitter.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.splitter.Fragments.ChatListFragment;
import com.splitter.Fragments.ProfileFragment;
import com.splitter.Fragments.UsersListFragment;

public class TabsAdapter extends FragmentStateAdapter {
    private final int numOfTabs;
    public TabsAdapter(@NonNull FragmentActivity fragmentActivity, int numOfTabs) {
        super(fragmentActivity);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatListFragment();
            case 1:
                return new ProfileFragment();
            case 2:
                return new UsersListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
