package com.example.localreminder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MVPAdapter extends FragmentStateAdapter {
    public MVPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new CompletedFragment();
            case 0:
            default:
                return new UpcomingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
