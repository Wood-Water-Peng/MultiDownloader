package com.example.jackypeng.multidownloader.simple;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackypeng on 2017/12/26.
 */

public class ListActivityAdapter extends FragmentStatePagerAdapter {

    private List<String> titles = new ArrayList<>();

    public ListActivityAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WholeTaskFragment();
            case 1:
                return new FinishedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.size();
    }
}
