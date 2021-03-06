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
import in.oriange.iblebook.adapters.TopAddressNavViewPagerAdapter;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class Address_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static String user_id;
    private static ConstantData constantData;
    private static ArrayList<GetAddressListPojo> addressList;
    private Context context;
    private AHBottomNavigation topNavigation;
    private AHBottomNavigationItem topMyAddress, topReceivedAddress, topOffineAddress/*, botNavRequests, botNavContacts*/;
    private Fragment currentFragment;
    private TopAddressNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;
    private UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_address, container, false);
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
        adapter = new TopAddressNavViewPagerAdapter(getChildFragmentManager());
        view_pager.setOffscreenPageLimit(3);
        view_pager.setAdapter(adapter);
        addressList = new ArrayList<GetAddressListPojo>();
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
            new GetAddressList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            constantData.setAddressList(addressList);
            My_Address_Fragment.setDefault();
            Received_Address_Fragment.setDefault();
            Offline_Address_Fragment.setDefault();
        }
    }

    private void setUpTopNavigation() { // Create items
        topMyAddress = new AHBottomNavigationItem("Personal", R.drawable.icon_my_address, R.color.colorPrimaryDark);
        topReceivedAddress = new AHBottomNavigationItem("Received", R.drawable.icon_received_address, R.color.colorPrimaryDark);
        topOffineAddress = new AHBottomNavigationItem("Others", R.drawable.icon_others, R.color.colorPrimaryDark);

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

    public static class GetAddressList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addressList = new ArrayList<GetAddressListPojo>();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "getAllAddresses");
                obj.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.ADDRESSAPI, obj.toString());
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
                    addressList = new ArrayList<GetAddressListPojo>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetAddressListPojo summary = new GetAddressListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                summary.setAddress_id(jsonObj.getString("address_id"));
                                summary.setType_id(jsonObj.getString("type_id"));
                                summary.setName(jsonObj.getString("name"));
                                summary.setAlias(jsonObj.getString("alias"));
                                summary.setAddress_line_one(jsonObj.getString("address_line_one"));
                                summary.setAddress_line_two(jsonObj.getString("address_line_two"));
                                summary.setCountry(jsonObj.getString("country"));
                                summary.setState(jsonObj.getString("state"));
                                summary.setDistrict(jsonObj.getString("district"));
                                summary.setPincode(jsonObj.getString("pincode"));
                                summary.setEmail_id(jsonObj.getString("email_id"));
                                summary.setWebsite(jsonObj.getString("website"));
                                summary.setVisiting_card(jsonObj.getString("visiting_card"));
                                summary.setMap_location_lattitude(jsonObj.getString("map_location_lattitude"));
                                summary.setMap_location_logitude(jsonObj.getString("map_location_logitude"));
                                summary.setPhoto(jsonObj.getString("photo"));
                                summary.setStatus(jsonObj.getString("status"));
                                summary.setCreated_by(jsonObj.getString("created_by"));
                                summary.setUpdated_by(jsonObj.getString("updated_by"));
                                summary.setType(jsonObj.getString("type"));
                                summary.setMobile_number(jsonObj.getString("mobile_number"));
                                summary.setLandline_number(jsonObj.getString("landline_number"));
                                summary.setContact_person_name(jsonObj.getString("contact_person_name"));
                                summary.setContact_person_mobile(jsonObj.getString("contact_person_mobile"));
                                addressList.add(summary);
                            }
                            constantData.setAddressList(addressList);

                            My_Address_Fragment.setDefault();
                            Received_Address_Fragment.setDefault();
                            Offline_Address_Fragment.setDefault();
                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        constantData.setAddressList(addressList);

                        My_Address_Fragment.setDefault();
                        Received_Address_Fragment.setDefault();
                        Offline_Address_Fragment.setDefault();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                constantData.setAddressList(addressList);

                My_Address_Fragment.setDefault();
                Received_Address_Fragment.setDefault();
                Offline_Address_Fragment.setDefault();
            }
        }
    }

}
