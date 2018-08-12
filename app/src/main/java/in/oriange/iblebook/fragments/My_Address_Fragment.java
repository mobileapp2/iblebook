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
import in.oriange.iblebook.activities.Add_Address_Activity;
import in.oriange.iblebook.activities.View_Address_Activity;
import in.oriange.iblebook.adapters.GetMyAddressListAdapter;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.RecyclerItemClickListener;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class My_Address_Fragment extends Fragment {
    private static Context context;
    private FloatingActionButton fab_add_address;
    private static RecyclerView rv_addresslist;
    private LinearLayoutManager layoutManager;
    public static FlowingDrawer ll_parent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;
    private static String user_id;
    private static ArrayList<GetAddressListPojo> addressList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_address, container, false);
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
        fab_add_address = rootView.findViewById(R.id.fab_add_address);
        rv_addresslist = rootView.findViewById(R.id.rv_addresslist);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_addresslist.setLayoutManager(layoutManager);
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
        }
    }

    private void setEventHandlers() {
        fab_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Add_Address_Activity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetAddressList().execute();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        rv_addresslist.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, View_Address_Activity.class);
                intent.putExtra("created_by", addressList.get(position).getCreated_by());
                intent.putExtra("status", addressList.get(position).getStatus());
                intent.putExtra("alias", addressList.get(position).getAlias());
                intent.putExtra("website", addressList.get(position).getWebsite());
                intent.putExtra("address_line_one", addressList.get(position).getAddress_line_one());
                intent.putExtra("state", addressList.get(position).getState());
                intent.putExtra("address_line_two", addressList.get(position).getAddress_line_two());
                intent.putExtra("photo", addressList.get(position).getPhoto());
                intent.putExtra("type_id", addressList.get(position).getType_id());
                intent.putExtra("country", addressList.get(position).getCountry());
                intent.putExtra("pincode", addressList.get(position).getPincode());
                intent.putExtra("visiting_card", addressList.get(position).getVisiting_card());
                intent.putExtra("map_location_logitude", addressList.get(position).getMap_location_logitude());
                intent.putExtra("address_id", addressList.get(position).getAddress_id());
                intent.putExtra("map_location_lattitude", addressList.get(position).getMap_location_lattitude());
                intent.putExtra("name", addressList.get(position).getName());
                intent.putExtra("updated_by", addressList.get(position).getUpdated_by());
                intent.putExtra("district", addressList.get(position).getDistrict());
                intent.putExtra("email_id", addressList.get(position).getEmail_id());
                startActivity(intent);
            }
        }));
    }

    public static class GetAddressList extends AsyncTask<String, Void, String> {

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
//                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        addressList = new ArrayList<GetAddressListPojo>();
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
                                summary.setMap_location_lattitude(jsonObj.getString("map_location_logitude"));
                                summary.setMap_location_logitude(jsonObj.getString("map_location_lattitude"));
                                summary.setPhoto(jsonObj.getString("photo"));
                                summary.setStatus(jsonObj.getString("status"));
                                summary.setCreated_by(jsonObj.getString("created_by"));
                                summary.setUpdated_by(jsonObj.getString("updated_by"));
                                addressList.add(summary);
                            }
                            rv_addresslist.setAdapter(new GetMyAddressListAdapter(context, addressList));
                        }
                    } else if (type.equalsIgnoreCase("failure")) {
//                        Utilities.showSnackBar(ll_parent, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
