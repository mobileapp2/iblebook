package in.oriange.iblebook.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.ReceivedDetails_Fragment;
import in.oriange.iblebook.fragments.ReceivedRequests_Fragment;

public class Requests_Activity extends FragmentActivity {

    private Context context;
    private TabLayout tl_tabnames;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        init();
        setEventHandler();
        setupToolbar();
    }

    private void init() {
        tl_tabnames = findViewById(R.id.tl_tabnames);
        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tl_tabnames.setupWithViewPager(viewPager);
        LinearLayout linearLayout = (LinearLayout) tl_tabnames.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ReceivedRequests_Fragment(), "Requests");
        adapter.addFragment(new ReceivedDetails_Fragment(), "Notifications");
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setEventHandler() {
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Requests");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
