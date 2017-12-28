package com.example.jackypeng.multidownloader.simple;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.example.jackypeng.multidownloader.R;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeListActivity extends FragmentActivity {

    @BindView(R.id.sliding_tab)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private Unbinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        binder = ButterKnife.bind(this);

        ArrayList<String> titles = new ArrayList<>();
        titles.add("全部任务");
        titles.add("已完成任务");
        ListActivityAdapter adapter = new ListActivityAdapter(getSupportFragmentManager(), titles);

        viewPager.setAdapter(adapter);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binder.unbind();
    }
}
