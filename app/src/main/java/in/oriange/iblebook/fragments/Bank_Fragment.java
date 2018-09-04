package in.oriange.iblebook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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
import in.oriange.iblebook.adapters.TopBankNavViewPagerAdapter;
import in.oriange.iblebook.models.GetBankListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class Bank_Fragment extends Fragment {
    public static DrawerLayout ll_parent;
    private static String user_id;
    private static ConstantData constantData;
    private static ArrayList<GetBankListPojo> bankList;
    private Context context;
    private AHBottomNavigation topNavigation;
    private AHBottomNavigationItem topMyBank, topReceivedBank, topOffineBank;
    private Fragment currentFragment;
    private TopBankNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;
    private UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bank, container, false);
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
        adapter = new TopBankNavViewPagerAdapter(getChildFragmentManager());
        view_pager.setOffscreenPageLimit(3);
        view_pager.setAdapter(adapter);
        bankList = new ArrayList<>();
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
            new GetBankList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            constantData.setBankList(bankList);

            My_Bank_Fragment.setDefault();
            Received_Bank_Fragment.setDefault();
            Offline_Bank_Fragment.setDefault();
        }
    }

    private void setUpTopNavigation() { // Create items
        topMyBank = new AHBottomNavigationItem("My Bank", R.drawable.icon_my_bank, R.color.colorPrimaryDark);
        topReceivedBank = new AHBottomNavigationItem("Received", R.drawable.icon_received_address, R.color.colorPrimaryDark);
        topOffineBank = new AHBottomNavigationItem("Offline", R.drawable.icon_offline, R.color.colorPrimaryDark);

        // Add items
        topNavigation.addItem(topMyBank);
        topNavigation.addItem(topReceivedBank);
        topNavigation.addItem(topOffineBank);

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

    public static class GetBankList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bankList = new ArrayList<GetBankListPojo>();
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
            res = WebServiceCalls.APICall(ApplicationConstants.BANKAPI, obj.toString());
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
                    bankList = new ArrayList<GetBankListPojo>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetBankListPojo summary = new GetBankListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                summary.setBank_id(jsonObj.getString("bank_id"));
                                summary.setAccount_holder_name(jsonObj.getString("account_holder_name"));
                                summary.setAlias(jsonObj.getString("alias"));
                                summary.setBank_name(jsonObj.getString("bank_name"));
                                summary.setIfsc_code(jsonObj.getString("ifsc_code"));
                                summary.setAccount_no(jsonObj.getString("account_no"));
                                summary.setDocument(jsonObj.getString("document"));
                                summary.setCreated_by(jsonObj.getString("created_by"));
                                summary.setStatus(jsonObj.getString("status"));
                                bankList.add(summary);
                            }
                            constantData.setBankList(bankList);

                            My_Bank_Fragment.setDefault();
                            Received_Bank_Fragment.setDefault();
                            Offline_Bank_Fragment.setDefault();

                        }
                    } else if (type.equalsIgnoreCase("failed")) {

                        constantData.setBankList(bankList);

                        My_Bank_Fragment.setDefault();
                        Received_Bank_Fragment.setDefault();
                        Offline_Bank_Fragment.setDefault();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                constantData.setBankList(bankList);

                My_Bank_Fragment.setDefault();
                Received_Bank_Fragment.setDefault();
                Offline_Bank_Fragment.setDefault();
            }
        }
    }

}
