package in.oriange.iblebook.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.BotNavViewPagerAdapter;
import in.oriange.iblebook.fragments.Contacts_Fragment;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;

public class MainNormalDrawer_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Context context;
    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationItem botNavAddress, botNavTax, botNavBank, botNavRequests, botNavContacts;
    private Fragment currentFragment;
    private BotNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;
    private static UserSessionManager session;

    private static TextView tv_name;
    private static ImageView ivMenuUserProfilePhoto;
    private static String photo;
    private static String name;

    public static void setupHeader() {
        getSessionData();
        Picasso.with(context)
                .load(photo)
                .placeholder(R.drawable.icon_userprofile)
                .into(ivMenuUserProfilePhoto);
        Picasso.with(context).setLoggingEnabled(true);

        tv_name.setText(name.trim());
    }

    private static void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            photo = json.getString("photo");
            name = json.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_normal_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
        getSessionData();
        setUpBottomNavigation();
        setupToolbar();
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

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader();
    }

    private void init() {
        context = MainNormalDrawer_Activity.this;
        session = new UserSessionManager(context);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        view_pager = findViewById(R.id.view_pager);
        tv_name = findViewById(R.id.tv_bankname);
        ivMenuUserProfilePhoto = findViewById(R.id.ivMenuUserProfilePhoto);
        view_pager.setOffscreenPageLimit(4);
        adapter = new BotNavViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);
    }

    private void setupToolbar() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            startActivity(new Intent(context, Profile_Activity.class));
        } else if (id == R.id.menu_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to log out?");
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.ic_alert_red_24dp);
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    session.logoutUser();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertD = builder.create();
            alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
            alertD.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
