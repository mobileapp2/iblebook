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

import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.Add_GST_Activity;
import in.oriange.iblebook.adapters.GetOfflineGSTListAdapter;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class Offline_GST_Fragment extends Fragment {
    public static FlowingDrawer ll_parent;
    private static Context context;
    private static RecyclerView rv_gstlist;
    private static String user_id;
    private static DataBaseHelper dbHelper;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private static ConstantData constantData;
    private FloatingActionButton fab_add_gst;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static ArrayList<GetTaxListPojo> gstList;
    private static ArrayList<GetTaxListPojo> sortedGstList;
    private SearchView searchView;

    public static void setDefault() {
//        if (Utilities.isNetworkAvailable(context)) {
//            new GetGSTList().execute();
//            swipeRefreshLayout.setRefreshing(true);
//        } else {
//            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
//            swipeRefreshLayout.setRefreshing(false);
//            ll_nothingtoshow.setVisibility(View.VISIBLE);
//            rv_gstlist.setVisibility(View.GONE);
//        }

        constantData = ConstantData.getInstance();
        gstList = new ArrayList<>();
        sortedGstList = new ArrayList<>();
        gstList = constantData.getGstList();

        if (gstList.size() != 0) {
            for (int i = 0; i < gstList.size(); i++) {
                if (gstList.get(i).getStatus().equals("offline")) {
                    sortedGstList.add(gstList.get(i));
                }
            }

            if (sortedGstList.size() != 0) {
                ll_nothingtoshow.setVisibility(View.GONE);
                rv_gstlist.setVisibility(View.VISIBLE);
                rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, sortedGstList, "OFFLINE"));
            } else {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_gstlist.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offline_gst, container, false);
        context = getActivity();
        init(rootView);
//        setDefault();
        getSessionData();
        setEventHandlers();
        return rootView;
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

    private void init(View rootView) {
        session = new UserSessionManager(context);
        dbHelper = new DataBaseHelper(context);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        fab_add_gst = rootView.findViewById(R.id.fab_add_gst);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_gstlist = rootView.findViewById(R.id.rv_gstlist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setFocusable(false);
        layoutManager = new LinearLayoutManager(context);
        rv_gstlist.setLayoutManager(layoutManager);
        constantData = ConstantData.getInstance();

        gstList = new ArrayList<>();
        if (gstList.size() == 0) {
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_gstlist.setVisibility(View.GONE);
        }
    }

    private void setEventHandlers() {
        fab_add_gst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Add_GST_Activity.class);
                intent.putExtra("STATUS", "OFFLINE");
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetGSTList().execute();
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
                    ArrayList<GetTaxListPojo> gstSearchedList = new ArrayList<>();
                    for (GetTaxListPojo gst : sortedGstList) {
                        String gstToBeSearched = gst.getName().toLowerCase() +
                                gst.getAlias().toLowerCase() +
                                gst.getGst_number().toLowerCase();
                        if (gstToBeSearched.contains(query.toLowerCase())) {
                            gstSearchedList.add(gst);
                        }
                    }
                    rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, gstSearchedList, "OFFLINE"));
                } else {
                    rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, sortedGstList, "OFFLINE"));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<GetTaxListPojo> gstSearchedList = new ArrayList<>();
                    for (GetTaxListPojo gst : sortedGstList) {
                        String gstToBeSearched = gst.getName().toLowerCase() +
                                gst.getAlias().toLowerCase() +
                                gst.getGst_number().toLowerCase();
                        if (gstToBeSearched.contains(newText.toLowerCase())) {
                            gstSearchedList.add(gst);
                        }
                    }
                    rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, gstSearchedList, "OFFLINE"));
                } else if (newText.equals("")) {
                    rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, sortedGstList, "OFFLINE"));
                }
                return true;
            }
        });

    }

    public static class GetGSTList extends AsyncTask<String, Void, String> {
        private ArrayList<GetTaxListPojo> gstList;

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
            res = WebServiceCalls.APICall(ApplicationConstants.TAXAPI, obj.toString());
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
                    gstList = new ArrayList<GetTaxListPojo>();
                    rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, gstList, "OFFLINE"));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetTaxListPojo summary = new GetTaxListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (jsonObj.getString("pan_number").equals("") && jsonObj.getString("status").equals("offline")) {
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
                            sortedGstList = gstList;
                            if (gstList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_gstlist.setVisibility(View.GONE);
                            } else {
                                rv_gstlist.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rv_gstlist.setAdapter(new GetOfflineGSTListAdapter(context, gstList, "OFFLINE"));
                        }
                    } else if (type.equalsIgnoreCase("failed")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_gstlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_gstlist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
