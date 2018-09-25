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
import in.oriange.iblebook.activities.Add_PAN_Activity;
import in.oriange.iblebook.adapters.GetMyPANListAdapter;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class My_PAN_Fragment extends Fragment {
    public static FlowingDrawer ll_parent;
    private static Context context;
    private static RecyclerView rv_panlist;
    private static String user_id;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private static ConstantData constantData;
    private FloatingActionButton fab_add_pan;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private SearchView searchView;

    public static void setDefault() {
//        if (Utilities.isNetworkAvailable(context)) {
//            new GetPANList().execute();
//            swipeRefreshLayout.setRefreshing(true);
//        } else {
//            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
//            swipeRefreshLayout.setRefreshing(false);
//            ll_nothingtoshow.setVisibility(View.VISIBLE);
//            rv_panlist.setVisibility(View.GONE);
//        }

        constantData = ConstantData.getInstance();
        ArrayList<GetTaxListPojo> panList = new ArrayList<>();
        ArrayList<GetTaxListPojo> sortedPanList = new ArrayList<>();
        panList = constantData.getPanList();

        if (panList.size() != 0) {
            for (int i = 0; i < panList.size(); i++) {
                if (panList.get(i).getStatus().equals("online")) {
                    sortedPanList.add(panList.get(i));
                }
            }

            if (sortedPanList.size() != 0) {
                ll_nothingtoshow.setVisibility(View.GONE);
                rv_panlist.setVisibility(View.VISIBLE);
                rv_panlist.setAdapter(new GetMyPANListAdapter(context, sortedPanList, "ONLINE"));
            } else {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_panlist.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_pan, container, false);
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
        fab_add_pan = rootView.findViewById(R.id.fab_add_pan);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_panlist = rootView.findViewById(R.id.rv_panlist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setFocusable(false);
        layoutManager = new LinearLayoutManager(context);
        rv_panlist.setLayoutManager(layoutManager);
        constantData = ConstantData.getInstance();
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
        fab_add_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Add_PAN_Activity.class);
                intent.putExtra("STATUS", "ONLINE");
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetPANList().execute();
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public static class GetPANList extends AsyncTask<String, Void, String> {
        private ArrayList<GetTaxListPojo> panList;

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
                    panList = new ArrayList<GetTaxListPojo>();
                    rv_panlist.setAdapter(new GetMyPANListAdapter(context, panList, "ONLINE"));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetTaxListPojo summary = new GetTaxListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (jsonObj.getString("gst_number").equals("") && jsonObj.getString("status").equals("online")) {
                                    summary.setTax_id(jsonObj.getString("tax_id"));
                                    summary.setName(jsonObj.getString("name"));
                                    summary.setAlias(jsonObj.getString("alias"));
                                    summary.setPan_number(jsonObj.getString("pan_number"));
                                    summary.setPan_document(jsonObj.getString("pan_document"));
                                    summary.setCreated_by(jsonObj.getString("created_by"));
                                    summary.setUpdated_by(jsonObj.getString("updated_by"));
                                    summary.setStatus(jsonObj.getString("status"));
                                    panList.add(summary);
                                }
                            }
                            if (panList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_panlist.setVisibility(View.GONE);
                            } else {
                                rv_panlist.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rv_panlist.setAdapter(new GetMyPANListAdapter(context, panList, "ONLINE"));
                        }
                    } else if (type.equalsIgnoreCase("failed")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_panlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_panlist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
