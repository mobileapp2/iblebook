package in.oriange.iblebook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.gson.Gson;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.TopAllInOneNavViewPagerAdapter;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.models.AllInOnePojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class AllInOne_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static String user_id;
    private static ConstantData constantData;
    private static ArrayList<AllInOneModel> allInOneList;
    private Context context;
    private AHBottomNavigation topNavigation;
    private AHBottomNavigationItem topMyAllInOne, topReceivedAllInOne, topOffineAllInOne;
    private Fragment currentFragment;
    private TopAllInOneNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;
    private UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_allinone, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setUpTopNavigation();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        topNavigation = rootView.findViewById(R.id.top_navigation);
        view_pager = rootView.findViewById(R.id.view_pager);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        constantData = ConstantData.getInstance();
        adapter = new TopAllInOneNavViewPagerAdapter(getChildFragmentManager());
        view_pager.setOffscreenPageLimit(3);
        view_pager.setAdapter(adapter);
        allInOneList = new ArrayList<AllInOneModel>();
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetAllInOneList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            constantData.setAllInOneList(allInOneList);
            My_AllInOne_Fragment.setDefault();
//            Received_AllInOne_Fragment.setDefault();
//            Offline_AllInOne_Fragment.setDefault();
        }
    }

    private void setUpTopNavigation() { // Create items
        topMyAllInOne = new AHBottomNavigationItem("Personal", R.drawable.icon_my_address, R.color.colorPrimaryDark);
        topReceivedAllInOne = new AHBottomNavigationItem("Received", R.drawable.icon_received_address, R.color.colorPrimaryDark);
        topOffineAllInOne = new AHBottomNavigationItem("Others", R.drawable.icon_others, R.color.colorPrimaryDark);

        // Add items
        topNavigation.addItem(topMyAllInOne);
        topNavigation.addItem(topReceivedAllInOne);
        topNavigation.addItem(topOffineAllInOne);

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

    public static class GetAllInOneList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            allInOneList = new ArrayList<AllInOneModel>();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "getAllInOne");
                obj.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.ALLINONEAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    allInOneList = new ArrayList<>();
                    AllInOnePojo pojoDetails = new Gson().fromJson(result, AllInOnePojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();
                    if (type.equalsIgnoreCase("success")) {
                        allInOneList = pojoDetails.getResult();
                        constantData.setAllInOneList(allInOneList);
                        My_AllInOne_Fragment.setDefault();
                        Received_Address_Fragment.setDefault();
                        Offline_AllInOne_Fragment.setDefault();
                    } else {
                        constantData.setAllInOneList(allInOneList);
                        My_AllInOne_Fragment.setDefault();
                        Received_Address_Fragment.setDefault();
                        Offline_AllInOne_Fragment.setDefault();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                constantData.setAllInOneList(allInOneList);
                My_AllInOne_Fragment.setDefault();
                Received_Address_Fragment.setDefault();
                Offline_AllInOne_Fragment.setDefault();
            }
        }
    }


}
