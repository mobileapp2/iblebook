package in.oriange.iblebook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.TopAddressNavViewPagerAdapter;

public class Address_Fragment extends Fragment {

    private Context context;
    private AHBottomNavigation topNavigation;
    private AHBottomNavigationItem topMyAddress, topReceivedAddress, topOffineAddress/*, botNavRequests, botNavContacts*/;
    private Fragment currentFragment;
    private TopAddressNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_address, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        setUpTopNavigation();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        topNavigation = rootView.findViewById(R.id.top_navigation);
        view_pager = rootView.findViewById(R.id.view_pager);
        adapter = new TopAddressNavViewPagerAdapter(getChildFragmentManager());
        view_pager.setOffscreenPageLimit(3);
        view_pager.setAdapter(adapter);
    }

    private void setDefault() {
    }

    private void setUpTopNavigation() { // Create items
        topMyAddress = new AHBottomNavigationItem("My Addresses", R.drawable.icon_my_address, R.color.colorPrimaryDark);
        topReceivedAddress = new AHBottomNavigationItem("Received", R.drawable.icon_received_address, R.color.colorPrimaryDark);
        topOffineAddress = new AHBottomNavigationItem("Offline", R.drawable.icon_offline, R.color.colorPrimaryDark);

        // Add items
        topNavigation.addItem(topMyAddress);
        topNavigation.addItem(topReceivedAddress);
        topNavigation.addItem(topOffineAddress);


        topNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        topNavigation.setDefaultBackgroundColor(Color.parseColor("#ffffff"));
        topNavigation.setAccentColor(Color.parseColor("#F57C00"));
        topNavigation.setItemDisableColor(Color.parseColor("#747474"));
    }

    private void setEventHandlers() {
        topNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

                view_pager.setCurrentItem(position, true);

                if (currentFragment == null) {
                    return true;
                }

                currentFragment = adapter.getCurrentFragment();
                return true;
            }
        });
    }

}
