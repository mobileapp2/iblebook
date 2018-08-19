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
import android.view.animation.OvershootInterpolator;

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
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class ReceivedDetails_Fragment extends Fragment {

    public static FlowingDrawer ll_parent;
    private static Context context;
    private static RecyclerView rv_detailslist;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static String user_id;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;

    public static void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetDetailsList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
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

    private void init(View rootView) {
        session = new UserSessionManager(context);
        rv_detailslist = rootView.findViewById(R.id.rv_detailslist);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_detailslist.setLayoutManager(layoutManager);
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
                    new GetDetailsList().execute();
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public static class GetDetailsList extends AsyncTask<String, Void, String> {
        private ArrayList<GetReceivedDetailsListPojo> detailsList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setMessage("Please wait...");
//            pd.setCancelable(false);
//            pd.show();
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
//                pd.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    detailsList = new ArrayList<GetReceivedDetailsListPojo>();

                    ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new GetReceivedDetailsListAdapter(context, detailsList)));
                    alphaAdapter.setDuration(500);
                    alphaAdapter.setInterpolator(new OvershootInterpolator());
                    alphaAdapter.setFirstOnly(false);
                    rv_detailslist.setAdapter(alphaAdapter);

                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetReceivedDetailsListPojo summary = new GetReceivedDetailsListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                summary.setShared_details_id(jsonObj.getString("shared_details_id"));
//                                summary.setMessage(jsonObj.getString("message"));
                                summary.setRecord_id(jsonObj.getString("record_id"));
                                summary.setSender_id(jsonObj.getString("sender_id"));
                                summary.setMobile(jsonObj.getString("mobile"));
                                summary.setType(jsonObj.getString("type"));
                                summary.setStatus(jsonObj.getString("status"));
                                summary.setSender_name(jsonObj.getString("sender_name"));
//                                summary.setSender_mobile(jsonObj.getString("sender_mobile"));
                                detailsList.add(summary);
                            }

                            ScaleInAnimationAdapter alphaAdapter1 = new ScaleInAnimationAdapter(new ScaleInAnimationAdapter(new GetReceivedDetailsListAdapter(context, detailsList)));
                            alphaAdapter1.setDuration(500);
                            alphaAdapter1.setInterpolator(new OvershootInterpolator());
                            alphaAdapter1.setFirstOnly(false);
                            rv_detailslist.setAdapter(alphaAdapter1);
                        }

                    } else if (type.equalsIgnoreCase("failure")) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
