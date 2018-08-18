package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class ShareDetails_Activity extends Activity {
    private static Context context;
    public LinearLayout ll_parent;
    private static RecyclerView rv_addresslist;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;

    private static String user_id;
    private String mobile, type, name, sender_id, sender_mobile;

    private static int lastSelectedPosition = -1;
    private static ArrayList<GetAddressListPojo> addressList;
    ImageView img_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_details);

        init();
        setupToolbar();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = ShareDetails_Activity.this;
        session = new UserSessionManager(context);
        addressList = new ArrayList<>();
        ll_parent = findViewById(R.id.ll_parent);
        rv_addresslist = findViewById(R.id.rv_addresslist);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_addresslist.setLayoutManager(layoutManager);
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("user_id");
            mobile = json.getString("mobile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIntentData() {
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        sender_id = getIntent().getStringExtra("sender_id");
        sender_mobile = getIntent().getStringExtra("mobile");
    }

    private void setDefaults() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetAddressList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }

    private void setEventHandler() {
        img_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedPosition == -1) {
                    Utilities.showAlertDialog(context, "Alert", "Please Select Any One Details", false);
                } else {
                    createDialogForShare();
                }
            }
        });
    }

    private void createDialogForShare() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_sharedetails, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        TextView tv_initletter = promptView.findViewById(R.id.tv_initletter);
        TextView tv_name = promptView.findViewById(R.id.tv_name);
        final EditText edt_message = promptView.findViewById(R.id.edt_message);

        tv_initletter.setText(String.valueOf(name.charAt(0)));
        tv_name.setText(name + " (" + sender_mobile + ") ");

        alertDialogBuilder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edt_message.getText().toString().trim().equals("")) {
                    Utilities.showMessageString(context, "Please Enter Message");
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new ShareDetails().execute(
                            edt_message.getText().toString().trim(),
                            sender_id,
                            mobile,
                            type,
                            addressList.get(lastSelectedPosition).getAddress_id()
                    );
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
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
                    addressList = new ArrayList<GetAddressListPojo>();
                    rv_addresslist.setAdapter(new GetAddressForShareAdapter(context, addressList));
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
                                summary.setMap_location_lattitude(jsonObj.getString("map_location_logitude"));
                                summary.setMap_location_logitude(jsonObj.getString("map_location_lattitude"));
                                summary.setPhoto(jsonObj.getString("photo"));
                                summary.setStatus(jsonObj.getString("status"));
                                summary.setCreated_by(jsonObj.getString("created_by"));
                                summary.setUpdated_by(jsonObj.getString("updated_by"));
                                summary.setType(jsonObj.getString("type"));
                                summary.setMobile_number(jsonObj.getString("mobile_number"));
                                addressList.add(summary);
                            }
                            rv_addresslist.setAdapter(new GetAddressForShareAdapter(context, addressList));
                        }
                    } else if (type.equalsIgnoreCase("failure")) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ShareDetails extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            String s = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("task", "SharedDetails");
                obj.put("message", params[0]);
                obj.put("sender_id", params[1]);
                obj.put("mobile", params[2]);
                obj.put("type", params[3]);
                obj.put("record_id", params[4]);
                obj.put("status", "import");
                s = obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.SHAREDETAILSAPI, s);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Details Shared Successfully");
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        img_check = findViewById(R.id.img_check);
        mToolbar.setTitle("Select Address");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public static class GetAddressForShareAdapter extends RecyclerView.Adapter<GetAddressForShareAdapter.MyViewHolder> {

        private final UserSessionManager session;
        private Context context;
        private String name;
        private static List<GetAddressListPojo> resultArrayList;

        public GetAddressForShareAdapter(Context context, List<GetAddressListPojo> resultArrayList) {
            this.context = context;
            this.resultArrayList = resultArrayList;
            session = new UserSessionManager(context);
            try {
                JSONArray user_info = new JSONArray(session.getUserDetails().get(
                        ApplicationConstants.KEY_LOGIN_INFO));
                JSONObject json = user_info.getJSONObject(0);
                name = json.getString("name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_addressshare, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getName().charAt(0)));
            holder.tv_addresstype.setText(resultArrayList.get(position).getType());
            holder.tv_name.setText(resultArrayList.get(position).getName());

            holder.rb_selectone.setChecked(lastSelectedPosition == position);

        }

        @Override
        public int getItemCount() {
            return resultArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_initletter, tv_addresstype, tv_name;
            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);
                tv_initletter = view.findViewById(R.id.tv_initletter);
                tv_addresstype = view.findViewById(R.id.tv_addresstype);
                tv_name = view.findViewById(R.id.tv_name);
                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

}
