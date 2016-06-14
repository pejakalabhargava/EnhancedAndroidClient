package com.codepath.apps.mysimpletweets.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.mysimpletweets.fragment.HomeTimeLineFragment;
import com.codepath.apps.mysimpletweets.fragment.MentionsTimeLineFragment;

//return order of the fragment in view pager
public class TweetsPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = {"Home", "Mentions"};

    //Adapter gets manager ,insert or remove fragment from activity
    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int position) {
        if (position == 0) return new HomeTimeLineFragment();
        else if (position == 1) return new MentionsTimeLineFragment();
        else return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
