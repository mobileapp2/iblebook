package in.oriange.iblebook.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.Add_AllInOne_Activity;
import in.oriange.iblebook.adapters.GetOfflineAllInOneListAdapter;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.models.AllInOnePojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class Offline_AllInOne_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static Context context;
    private static RecyclerView rv_allinonelist;
    private static String user_id;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private static ConstantData constantData;
    private FloatingActionButton fab_add_allinone;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static ArrayList<AllInOneModel> allInOneList;
    private static ArrayList<AllInOneModel> sortedAllInOneList;
    private SearchView searchView;

    public static void setDefault() {

        constantData = ConstantData.getInstance();
        allInOneList = new ArrayList<>();
        sortedAllInOneList = new ArrayList<>();
        allInOneList = constantData.getAllInOneList();

        if (allInOneList.size() != 0) {
            for (int i = 0; i < allInOneList.size(); i++) {
                if (allInOneList.get(i).getStatus().equals("offline")) {
                    sortedAllInOneList.add(allInOneList.get(i));
                }
            }

            if (sortedAllInOneList.size() != 0) {
                ll_nothingtoshow.setVisibility(View.GONE);
                rv_allinonelist.setVisibility(View.VISIBLE);
                rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, sortedAllInOneList, "OFFLINE"));
            } else {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_allinonelist.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offline_allinone, container, false);
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
        fab_add_allinone = rootView.findViewById(R.id.fab_add_allinone);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_allinonelist = rootView.findViewById(R.id.rv_allinonelist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setFocusable(false);
        layoutManager = new LinearLayoutManager(context);
        rv_allinonelist.setLayoutManager(layoutManager);
        constantData = ConstantData.getInstance();

        allInOneList = new ArrayList<>();
        if (allInOneList.size() == 0) {
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_allinonelist.setVisibility(View.GONE);
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
        fab_add_allinone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Add_AllInOne_Activity.class);
                intent.putExtra("STATUS", "OFFLINE");
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetAllInOneList().execute();
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
                    ArrayList<AllInOneModel> addressSearchedList = new ArrayList<>();
                    for (AllInOneModel address : sortedAllInOneList) {
                        String addressToBeSearched = address.getName().toLowerCase() +
                                address.getAlias().toLowerCase() +
                                address.getMobile_number().toLowerCase();
                        if (addressToBeSearched.contains(query.toLowerCase())) {
                            addressSearchedList.add(address);
                        }
                    }
                    rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, addressSearchedList, "OFFLINE"));
                } else {
                    rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, sortedAllInOneList, "OFFLINE"));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<AllInOneModel> addressSearchedList = new ArrayList<>();
                    for (AllInOneModel address : sortedAllInOneList) {
                        String addressToBeSearched = address.getName().toLowerCase() +
                                address.getAlias().toLowerCase() +
                                address.getMobile_number().toLowerCase();
                        if (addressToBeSearched.contains(newText.toLowerCase())) {
                            addressSearchedList.add(address);
                        }
                    }
                    rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, addressSearchedList, "OFFLINE"));
                } else if (newText.equals("")) {
                    rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, sortedAllInOneList, "OFFLINE"));
                }
                return true;
            }
        });

    }

    public static class GetAllInOneList extends AsyncTask<String, Void, String> {
        private ArrayList<AllInOneModel> allInOneList;

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
            swipeRefreshLayout.setRefreshing(false);
            try {
                if (!result.equals("")) {
                    allInOneList = new ArrayList<>();
                    AllInOnePojo pojoDetails = new Gson().fromJson(result, AllInOnePojo.class);
                    type = pojoDetails.getType();
                    rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, allInOneList, "OFFLINE"));
                    if (type.equalsIgnoreCase("success")) {
                        for (int i = 0; i < pojoDetails.getResult().size(); i++) {
                            if (pojoDetails.getResult().get(i).getStatus().equalsIgnoreCase("offline")) {
                                allInOneList.add(pojoDetails.getResult().get(i));
                            }
                        }
                        sortedAllInOneList = allInOneList;
                        if (allInOneList.size() == 0) {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            rv_allinonelist.setVisibility(View.GONE);
                        } else {
                            rv_allinonelist.setVisibility(View.VISIBLE);
                            ll_nothingtoshow.setVisibility(View.GONE);
                        }
                        rv_allinonelist.setAdapter(new GetOfflineAllInOneListAdapter(context, allInOneList, "OFFLINE"));
                    } else {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_allinonelist.setVisibility(View.GONE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_allinonelist.setVisibility(View.GONE);
            }
        }
    }

}
