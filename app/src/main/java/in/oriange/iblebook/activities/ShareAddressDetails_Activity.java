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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class ShareAddressDetails_Activity extends Activity {
    private static Context context;
    private static RecyclerView rv_addresslist;
    private static String user_id;
    private static int lastSelectedPosition = -1;
    private static ArrayList<GetAddressListPojo> addressList;
    public LinearLayout ll_parent;
    ImageView img_check;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;
    private String mobile, type, name, sender_id, sender_mobile;
    private static String addresstype, u_name, alias, addresss, country,
            state, district, pincode, u_mobile, email, website, address_line_1, address_line_2;
    private TextView edt_viewloc, edt_visitcard, edt_attachphoto;
    private static String photo, visiting_card, address_id, map_location_lattitude, map_location_logitude, STATUS, type_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_address_details);

        init();
        setupToolbar();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(ShareAddressDetails_Activity.this);
    }

    private void init() {
        context = ShareAddressDetails_Activity.this;
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

                    setSelectionFilter();
                    //createDialogForShare();
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
                            user_id,
                            sender_mobile,
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


                                if (!jsonObj.getString("status").equals("Duplicate")) {
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

    public static class GetAddressForShareAdapter extends RecyclerView.Adapter<GetAddressForShareAdapter.MyViewHolder> {

        private static List<GetAddressListPojo> resultArrayList;
        private final UserSessionManager session;
        private Context context;
        private String name;

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
            holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getType().charAt(0)));
            holder.tv_alias.setText(resultArrayList.get(position).getType());
            holder.tv_name.setText(resultArrayList.get(position).getAlias());
            holder.tv_details.setText(resultArrayList.get(position).getName());

            holder.rb_selectone.setChecked(lastSelectedPosition == position);

        }

        @Override
        public int getItemCount() {
            return resultArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_initletter, tv_alias, tv_name, tv_details;
            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);
                tv_initletter = view.findViewById(R.id.tv_initletter);
                tv_alias = view.findViewById(R.id.tv_alias);
                tv_name = view.findViewById(R.id.tv_name);
                tv_details = view.findViewById(R.id.tv_details);
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
                obj.put("receiver_id", sender_id);
                obj.put("mobile", params[2]);
                obj.put("type", params[3]);
                obj.put("record_id", params[4]);
                obj.put("status", "import");
                obj.put("a_type_id", addresstype);
                obj.put("a_name", u_name);
                obj.put("a_alias", alias);
                obj.put("a_address_line_one", address_line_1);
                obj.put("a_address_line_two", address_line_2);
                obj.put("a_country", country);
                obj.put("a_state", state);
                obj.put("a_district", district);
                obj.put("a_pincode", pincode);
                obj.put("a_email_id", email);
                obj.put("a_website", website);
                obj.put("a_visiting_card", visiting_card);
                obj.put("a_map_location_logitude", map_location_logitude);
                obj.put("a_map_location_lattitude", map_location_lattitude);
                obj.put("a_photo", photo);
                obj.put("a_mobile_number", u_mobile);

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
                        builder.setIcon(R.drawable.ic_success_24dp);
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


    private void setSelectionFilter() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_shareaddress, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        final CheckBox cb_addresstype = promptView.findViewById(R.id.cb_addresstype);
        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_address = promptView.findViewById(R.id.cb_address);
        final CheckBox cb_country = promptView.findViewById(R.id.cb_country);
        final CheckBox cb_state = promptView.findViewById(R.id.cb_state);
        final CheckBox cb_district = promptView.findViewById(R.id.cb_district);
        final CheckBox cb_pincode = promptView.findViewById(R.id.cb_pincode);
        final CheckBox cb_mobile = promptView.findViewById(R.id.cb_mobile);
        final CheckBox cb_email = promptView.findViewById(R.id.cb_email);
        final CheckBox cb_website = promptView.findViewById(R.id.cb_website);
        final CheckBox cb_maplocation = promptView.findViewById(R.id.cb_maplocation);
        final CheckBox cb_visitcard = promptView.findViewById(R.id.cb_visitcard);
        final CheckBox cb_photo = promptView.findViewById(R.id.cb_photo);

        cb_country.setVisibility(View.GONE);
        cb_state.setVisibility(View.GONE);
        cb_district.setVisibility(View.GONE);
        cb_pincode.setVisibility(View.GONE);

        if (addressList.get(lastSelectedPosition).getType_id().equals("")) {
            cb_addresstype.setVisibility(View.GONE);
            cb_addresstype.setChecked(false);
        } else {
            cb_addresstype.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getName().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getAddress_id().equals("")) {
            cb_address.setVisibility(View.GONE);
            cb_address.setChecked(false);
        } else {
            cb_address.setVisibility(View.VISIBLE);
        }
        if (addressList.get(lastSelectedPosition).getMobile_number().equals("")) {
            cb_mobile.setVisibility(View.GONE);
            cb_mobile.setChecked(false);
        } else {
            cb_mobile.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getEmail_id().equals("")) {
            cb_email.setVisibility(View.GONE);
            cb_email.setChecked(false);
        } else {
            cb_email.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getWebsite().equals("")) {
            cb_website.setVisibility(View.GONE);
            cb_website.setChecked(false);
        } else {
            cb_website.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getMap_location_lattitude().equals("") && addressList.get(lastSelectedPosition).getMap_location_logitude().equals("")) {
            cb_maplocation.setVisibility(View.GONE);
            cb_maplocation.setChecked(false);
        } else {
            cb_maplocation.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getVisiting_card().equals("")) {
            cb_visitcard.setVisibility(View.GONE);
            cb_visitcard.setChecked(false);
        } else {
            cb_visitcard.setVisibility(View.VISIBLE);
        }

        if (addressList.get(lastSelectedPosition).getPhoto().equals("")) {
            cb_photo.setVisibility(View.GONE);
            cb_photo.setChecked(false);
        } else {
            cb_photo.setVisibility(View.VISIBLE);
        }


        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                if (cb_addresstype.isChecked()) {
                    addresstype = addressList.get(lastSelectedPosition).getType_id();
                } else {
                    addresstype = "";
                }

                if (cb_name.isChecked()) {
                    u_name = addressList.get(lastSelectedPosition).getName();
                    alias = addressList.get(lastSelectedPosition).getAlias();
                } else {
                    u_name = "";
                    alias = "";
                }

                if (cb_address.isChecked()) {
                    address_line_1 = addressList.get(lastSelectedPosition).getAddress_line_one();
                    address_line_2 = addressList.get(lastSelectedPosition).getAddress_line_two();
                    country = addressList.get(lastSelectedPosition).getCountry();
                    state = addressList.get(lastSelectedPosition).getState();
                    district = addressList.get(lastSelectedPosition).getDistrict();
                    pincode = addressList.get(lastSelectedPosition).getPincode();

                } else {
                    address_line_1 = "";
                    address_line_2 = "";
                    country = "";
                    district = "";
                    pincode = "";
                    state = "";
                }

                if (cb_mobile.isChecked()) {
                    u_mobile = addressList.get(lastSelectedPosition).getMobile_number();
                } else {
                    u_mobile = "";
                }

                if (cb_email.isChecked()) {
                    email = addressList.get(lastSelectedPosition).getEmail_id();
                } else {
                    email = "";
                }

                if (cb_website.isChecked()) {
                    website = addressList.get(lastSelectedPosition).getWebsite();
                } else {
                    website = "";
                }

                if (cb_maplocation.isChecked()) {
                    map_location_lattitude = addressList.get(lastSelectedPosition).getMap_location_lattitude();
                    map_location_logitude = addressList.get(lastSelectedPosition).getMap_location_logitude();
                } else {
                    map_location_lattitude = "";
                    map_location_logitude = "";
                }

                if (cb_visitcard.isChecked()) {
                    visiting_card = addressList.get(lastSelectedPosition).getVisiting_card();
                } else {
                    visiting_card = "";
                }

                if (cb_photo.isChecked()) {
                    photo = addressList.get(lastSelectedPosition).getPhoto();
                } else {
                    photo = "";
                }

                if (!cb_addresstype.isChecked() && !cb_name.isChecked() && !cb_address.isChecked() && !cb_mobile.isChecked()
                        && !cb_email.isChecked() && !cb_website.isChecked() && !cb_maplocation.isChecked() && !cb_visitcard.isChecked()
                        && !cb_photo.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                createDialogForShare();

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int view) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
}
