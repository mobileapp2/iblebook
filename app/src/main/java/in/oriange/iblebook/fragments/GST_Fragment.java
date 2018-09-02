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
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.TopGSTNavViewPagerAdapter;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GST_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static String user_id;
    private static ConstantData constantData;
    private Context context;
    private AHBottomNavigation topNavigation;
    private AHBottomNavigationItem topMyGST, topReceivedGST, topOffineGST/*, botNavRequests, botNavContacts*/;
    private Fragment currentFragment;
    private TopGSTNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;
    private UserSessionManager session;
    private static ArrayList<GetTaxListPojo> gstList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gst, container, false);
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
        adapter = new TopGSTNavViewPagerAdapter(getChildFragmentManager());
        view_pager.setOffscreenPageLimit(3);
        view_pager.setAdapter(adapter);
        gstList = new ArrayList<GetTaxListPojo>();
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
            new GetGSTList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            constantData.setGstList(gstList);

            My_GST_Fragment.setDefault();
            Received_GST_Fragment.setDefault();
            Offline_GST_Fragment.setDefault();
        }
    }

    private void setUpTopNavigation() { // Create items
        topMyGST = new AHBottomNavigationItem("My GST", R.drawable.icon_my_address, R.color.colorPrimaryDark);
        topReceivedGST = new AHBottomNavigationItem("Received", R.drawable.icon_received_address, R.color.colorPrimaryDark);
        topOffineGST = new AHBottomNavigationItem("Offline", R.drawable.icon_offline, R.color.colorPrimaryDark);

        // Add items
        topNavigation.addItem(topMyGST);
        topNavigation.addItem(topReceivedGST);
        topNavigation.addItem(topOffineGST);


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

    public static class GetGSTList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gstList = new ArrayList<GetTaxListPojo>();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "GetData");
                obj.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.TAXAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    gstList = new ArrayList<GetTaxListPojo>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetTaxListPojo summary = new GetTaxListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (jsonObj.getString("pan_number").equals("")) {
                                    summary.setTax_id(jsonObj.getString("tax_id"));
                                    summary.setName(jsonObj.getString("name"));
                                    summary.setAlias(jsonObj.getString("alias"));
                                    summary.setGst_number(jsonObj.getString("gst_number"));
                                    summary.setGst_document(jsonObj.getString("gst_document"));
                                    summary.setCreated_by(jsonObj.getString("created_by"));
                                    summary.setUpdated_by(jsonObj.getString("updated_by"));
                                    summary.setStatus(jsonObj.getString("status"));
                                    gstList.add(summary);
                                }
                            }

                            constantData.setGstList(gstList);

                            My_GST_Fragment.setDefault();
                            Received_GST_Fragment.setDefault();
                            Offline_GST_Fragment.setDefault();
                        }
                    } else if (type.equalsIgnoreCase("failed")) {

                        constantData.setGstList(gstList);

                        My_GST_Fragment.setDefault();
                        Received_GST_Fragment.setDefault();
                        Offline_GST_Fragment.setDefault();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                constantData.setGstList(gstList);

                My_GST_Fragment.setDefault();
                Received_GST_Fragment.setDefault();
                Offline_GST_Fragment.setDefault();
            }
        }
    }
}
