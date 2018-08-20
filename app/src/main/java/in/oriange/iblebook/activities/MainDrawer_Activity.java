package in.oriange.iblebook.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.BotNavViewPagerAdapter;
import in.oriange.iblebook.fragments.Contacts_Fragment;
import in.oriange.iblebook.fragments.MenuListFragment;

public class MainDrawer_Activity extends FragmentActivity {
    public static FlowingDrawer mDrawer;
    private Context context;
    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationItem botNavAddress, botNavTax, botNavBank, botNavRequests, botNavContacts;
    private Fragment currentFragment;
    private BotNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindrawer);

        init();
        setUpMenuDrawer();
        setUpBottomNavigation();
        setupToolbar();
    }

    private void init() {
        context = MainDrawer_Activity.this;
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        view_pager = findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(4);
        adapter = new BotNavViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);
    }

    private void setUpMenuDrawer() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }

    private void setUpBottomNavigation() {
        // Create items
        botNavAddress = new AHBottomNavigationItem("Address", R.drawable.icon_botnav_address, R.color.colorPrimaryDark);
        botNavTax = new AHBottomNavigationItem("Tax Details", R.drawable.icon_botnav_tax, R.color.colorPrimaryDark);
        botNavBank = new AHBottomNavigationItem("Bank Details", R.drawable.icon_botnav_bank, R.color.colorPrimaryDark);
        botNavRequests = new AHBottomNavigationItem("Requests", R.drawable.icon_botnav_requests, R.color.colorPrimaryDark);
        botNavContacts = new AHBottomNavigationItem("Contacts", R.drawable.icon_botnav_contacts, R.color.colorPrimaryDark);

        // Add items
        bottomNavigation.addItem(botNavAddress);
        bottomNavigation.addItem(botNavTax);
        bottomNavigation.addItem(botNavBank);
        bottomNavigation.addItem(botNavRequests);
        bottomNavigation.addItem(botNavContacts);


        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#ffffff"));
        bottomNavigation.setAccentColor(Color.parseColor("#F57C00"));
//        bottomNavigation.setNotification("1", 3);
        bottomNavigation.setItemDisableColor(Color.parseColor("#747474"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 4)
                    ((Contacts_Fragment) adapter.getItem(position)).refresh();

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

    protected void setupToolbar() {
        ImageButton btn_drawer = findViewById(R.id.tool_leftbtn);
        TextView Title = findViewById(R.id.tool_titile);

        btn_drawer.setBackgroundResource(R.drawable.icon_threebar);
        Title.setText("Address Book");

        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.toggleMenu();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDrawer.isActivated()) {
            mDrawer.closeMenu();
        } else {
            finish();
        }
    }
}
