package com.debarunlahiri.burnabvideo.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.debarunlahiri.burnabvideo.Home.Categories.CategoriesFragment;
import com.debarunlahiri.burnabvideo.Home.SubscriptionFeeds.SubscriptionFeedFragment;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 1) {
            fragment = new SubscriptionFeedFragment();
        } else if (position == 0) {
            fragment = new CategoriesFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
