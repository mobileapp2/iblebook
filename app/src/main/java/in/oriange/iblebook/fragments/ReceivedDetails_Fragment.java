package in.oriange.iblebook.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import in.oriange.iblebook.adapters.GetReceivedDetailsListAdapter;
import in.oriange.iblebook.models.GetReceivedDetailsListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class ReceivedDetails_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static Context context;
    private static RecyclerView rv_detailslist;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static String user_id;
    private static LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static ArrayList<GetReceivedDetailsListPojo> detailsList;
    private SearchView searchView;

    public static void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetDetailsList().execute();
            swipeRefreshLayout.setRefreshing(true);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_detailslist.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_received_details, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
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
        rv_detailslist = rootView.findViewById(R.id.rv_detailslist);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setFocusable(false);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        detailsList = new ArrayList<GetReceivedDetailsListPojo>();
        layoutManager = new LinearLayoutManager(context);
        rv_detailslist.setLayoutManager(layoutManager);
    }

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetDetailsList().execute();
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
                    ArrayList<GetReceivedDetailsListPojo> detailSearchedList = new ArrayList<>();
                    for (GetReceivedDetailsListPojo detail : detailsList) {
                        String destilsToBeSearched = detail.getSender_name().toLowerCase() +
                                detail.getSender_mobile().toLowerCase();
                        if (destilsToBeSearched.contains(query.toLowerCase())) {
                            detailSearchedList.add(detail);
                        }
                    }
                    rv_detailslist.setAdapter(new GetReceivedDetailsListAdapter(context, detailSearchedList));
                } else {
                    rv_detailslist.setAdapter(new GetReceivedDetailsListAdapter(context, detailsList));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<GetReceivedDetailsListPojo> detailSearchedList = new ArrayList<>();
                    for (GetReceivedDetailsListPojo detail : detailsList) {
                        String destilsToBeSearched = detail.getSender_name().toLowerCase() +
                                detail.getSender_mobile().toLowerCase();
                        if (destilsToBeSearched.contains(newText.toLowerCase())) {
                            detailSearchedList.add(detail);
                        }
                    }
                    rv_detailslist.setAdapter(new GetReceivedDetailsListAdapter(context, detailSearchedList));
                } else if (newText.equals("")) {
                    rv_detailslist.setAdapter(new GetReceivedDetailsListAdapter(context, detailsList));
                }
                return true;
            }
        });

    }

    public static class GetDetailsList extends AsyncTask<String, Void, String> {

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
                obj.put("type", "GetSharedDataForMe");
                obj.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.GETREQDETAILSSAPI, obj.toString());
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
                    detailsList = new ArrayList<GetReceivedDetailsListPojo>();
                    rv_detailslist.setAdapter(new GetReceivedDetailsListAdapter(context, detailsList));

                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetReceivedDetailsListPojo summary = new GetReceivedDetailsListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                summary.setShared_details_id(jsonObj.getString("shared_details_id"));
                                summary.setMessage(jsonObj.getString("message"));
                                summary.setRecord_id(jsonObj.getString("record_id"));
                                summary.setSender_id(jsonObj.getString("sender_id"));
                                summary.setMobile(jsonObj.getString("mobile"));
                                summary.setType(jsonObj.getString("type"));
                                summary.setStatus(jsonObj.getString("status"));
                                summary.setSender_name(jsonObj.getString("sender_name"));
                                summary.setSender_mobile(jsonObj.getString("sender_mobile"));
                                summary.setNew_record_id(jsonObj.getString("new_record_id"));

                                detailsList.add(summary);
                            }
                            rv_detailslist.setVisibility(View.VISIBLE);
                            ll_nothingtoshow.setVisibility(View.GONE);
                            rv_detailslist.setAdapter(new GetReceivedDetailsListAdapter(context, detailsList));
                        }

                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_detailslist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_detailslist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }


}
