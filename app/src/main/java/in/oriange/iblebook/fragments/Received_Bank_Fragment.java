package in.oriange.iblebook.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.iblebook.R;
import in.oriange.iblebook.adapters.GetReceivedBankListAdapter;
import in.oriange.iblebook.models.GetBankListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class Received_Bank_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static Context context;
    private static RecyclerView rv_banklist;
    private static String user_id;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private static ConstantData constantData;
    private FloatingActionButton fab_add_bank;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static ArrayList<GetBankListPojo> bankList;
    private static ArrayList<GetBankListPojo> sortedBankList;
    private SearchView searchView;

    public static void setDefault() {
//        if (Utilities.isNetworkAvailable(context)) {
//            new GetBankList().execute();
//            swipeRefreshLayout.setRefreshing(true);
//        } else {
//            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
//            swipeRefreshLayout.setRefreshing(false);
//            ll_nothingtoshow.setVisibility(View.VISIBLE);
//            rv_banklist.setVisibility(View.GONE);
//        }

        constantData = ConstantData.getInstance();
        bankList = new ArrayList<>();
        sortedBankList = new ArrayList<>();
        bankList = constantData.getBankList();

        if (bankList.size() != 0) {
            for (int i = 0; i < bankList.size(); i++) {
                if (bankList.get(i).getStatus().equals("received")) {
                    sortedBankList.add(bankList.get(i));
                }
            }

            if (sortedBankList.size() != 0) {
                ll_nothingtoshow.setVisibility(View.GONE);
                rv_banklist.setVisibility(View.VISIBLE);
                rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, sortedBankList, "RECEIVED"));
            } else {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_banklist.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_received_bank, container, false);
        context = getActivity();
        init(rootView);
//        setDefault();
        getSessionData();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        fab_add_bank = rootView.findViewById(R.id.fab_add_bank);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_banklist = rootView.findViewById(R.id.rv_banklist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setFocusable(false);
        layoutManager = new LinearLayoutManager(context);
        rv_banklist.setLayoutManager(layoutManager);
        constantData = ConstantData.getInstance();

        bankList = new ArrayList<>();
        if (bankList.size() == 0) {
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_banklist.setVisibility(View.GONE);
        }
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

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetBankList().execute();
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (!query.equals("")) {
                    ArrayList<GetBankListPojo> bankSearchedList = new ArrayList<>();
                    for (GetBankListPojo bank : sortedBankList) {
                        String bankToBeSearched = bank.getBank_name().toLowerCase() +
                                bank.getAlias().toLowerCase() +
                                bank.getAccount_holder_name().toLowerCase();
                        if (bankToBeSearched.contains(query.toLowerCase())) {
                            bankSearchedList.add(bank);
                        }
                    }
                    rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, bankSearchedList, "RECEIVED"));
                } else {
                    rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, sortedBankList, "RECEIVED"));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<GetBankListPojo> bankSearchedList = new ArrayList<>();
                    for (GetBankListPojo bank : sortedBankList) {
                        String bankToBeSearched = bank.getBank_name().toLowerCase() +
                                bank.getAlias().toLowerCase() +
                                bank.getAccount_holder_name().toLowerCase();
                        if (bankToBeSearched.contains(newText.toLowerCase())) {
                            bankSearchedList.add(bank);
                        }
                    }
                    rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, bankSearchedList, "RECEIVED"));
                } else if (newText.equals("")) {
                    rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, sortedBankList, "RECEIVED"));
                }
                return true;
            }
        });

    }

    public static class GetBankList extends AsyncTask<String, Void, String> {
        private ArrayList<GetBankListPojo> bankList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
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
                swipeRefreshLayout.setRefreshing(false);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    bankList = new ArrayList<GetBankListPojo>();
                    rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, bankList, "RECEIVED"));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetBankListPojo summary = new GetBankListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (jsonObj.getString("status").equals("received")) {
                                    summary.setBank_id(jsonObj.getString("bank_id"));
                                    summary.setAccount_holder_name(jsonObj.getString("account_holder_name"));
                                    summary.setAlias(jsonObj.getString("alias"));
                                    summary.setBank_name(jsonObj.getString("bank_name"));
                                    summary.setIfsc_code(jsonObj.getString("ifsc_code"));
                                    summary.setAccount_no(jsonObj.getString("account_no"));
                                    summary.setDocument(jsonObj.getString("document"));
                                    summary.setCreated_by(jsonObj.getString("created_by"));
                                    summary.setUpdated_by(jsonObj.getString("updated_by"));
                                    summary.setStatus(jsonObj.getString("status"));
                                    bankList.add(summary);
                                }
                            }
                            sortedBankList = bankList;
                            if (bankList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_banklist.setVisibility(View.GONE);
                            } else {
                                rv_banklist.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rv_banklist.setAdapter(new GetReceivedBankListAdapter(context, bankList, "RECEIVED"));
                        }
                    } else if (type.equalsIgnoreCase("failed")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_banklist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_banklist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
