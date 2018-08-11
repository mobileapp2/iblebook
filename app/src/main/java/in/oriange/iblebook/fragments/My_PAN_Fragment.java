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

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.Add_PAN_Activity;
import in.oriange.iblebook.adapters.GetPANListAdapter;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class My_PAN_Fragment extends Fragment {
    private static Context context;
    private FloatingActionButton fab_add_pan;
    private static RecyclerView rv_panlist;
    private LinearLayoutManager layoutManager;
    public static FlowingDrawer ll_parent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;
    private static String user_id;
    private static ArrayList<GetTaxListPojo> panList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_pan, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionData();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        ll_parent = getActivity().findViewById(R.id.drawerlayout);
        fab_add_pan = rootView.findViewById(R.id.fab_add_pan);
        rv_panlist = rootView.findViewById(R.id.rv_panlist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_panlist.setLayoutManager(layoutManager);
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
            new GetPANList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }

    private void setEventHandlers() {
        fab_add_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Add_PAN_Activity.class);
                intent.putExtra("NETSTAT", "ONLINE");
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetPANList().execute();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
//        rv_panlist.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(context, View_PAN_Activity.class);
//                intent.putExtra("tax_id", panList.get(position).getTax_id());
//                intent.putExtra("name", panList.get(position).getName());
//                intent.putExtra("alias", panList.get(position).getAlias());
//                intent.putExtra("pan_number", panList.get(position).getPan_number());
//                intent.putExtra("pan_document", panList.get(position).getPan_document());
//                intent.putExtra("created_by", panList.get(position).getCreated_by());
//                intent.putExtra("updated_by", panList.get(position).getUpdated_by());
//                startActivity(intent);
//            }
//        }));
    }

    public static class GetPANList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setMessage("Please wait...");
//            pd.setCancelable(false);
//            pd.show();
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
//                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    panList = new ArrayList<GetTaxListPojo>();
                    rv_panlist.setAdapter(new GetPANListAdapter(context, panList, "ONLINE"));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetTaxListPojo summary = new GetTaxListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (jsonObj.getString("gst_number").equals("")) {
                                    summary.setTax_id(jsonObj.getString("tax_id"));
                                    summary.setName(jsonObj.getString("name"));
                                    summary.setAlias(jsonObj.getString("alias"));
                                    summary.setPan_number(jsonObj.getString("pan_number"));
                                    summary.setPan_document(jsonObj.getString("pan_document"));
                                    summary.setCreated_by(jsonObj.getString("created_by"));
                                    summary.setUpdated_by(jsonObj.getString("updated_by"));
                                    panList.add(summary);
                                }
                            }
                            rv_panlist.setAdapter(new GetPANListAdapter(context, panList, "ONLINE"));
                        }
                    } else if (type.equalsIgnoreCase("failed")) {
//                        Utilities.showSnackBar(ll_parent, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
