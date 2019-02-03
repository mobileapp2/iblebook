package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.models.AllInOnePojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class ShareAllInOneDetails_Activity extends Activity {

    private Context context;
    private RecyclerView rv_allinonelist;
    private String user_id;
    private ArrayList<AllInOneModel> allInOneList;
    private ArrayList<AllInOneModel> toBeSharedAllInOneList;
    public LinearLayout ll_parent;
    private ImageView img_check;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;
    private String mobile, type, name, sender_id, sender_mobile, request_id;
    private static String addresstype, u_name, alias, addresss, country, state, district, pincode, u_mobile, email, landline, contactPersonName,
            contactPersonMobile, website, address_line_1, address_line_2, photo, visiting_card, map_location_lattitude, map_location_logitude,
            holder_name, holder_alias, bank_name, ifsc_code, acc_no, bank_doc, pan_no, pan_doc, gst_no, gst_doc;
    private AllInOneModel sentAllInOneDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_allinonedetails);

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
        hideSoftKeyboard(ShareAllInOneDetails_Activity.this);
    }

    private void init() {
        context = ShareAllInOneDetails_Activity.this;
        session = new UserSessionManager(context);
        allInOneList = new ArrayList<>();
        toBeSharedAllInOneList = new ArrayList<>();
        ll_parent = findViewById(R.id.ll_parent);
        rv_allinonelist = findViewById(R.id.rv_allinonelist);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_allinonelist.setLayoutManager(layoutManager);
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
        request_id = getIntent().getStringExtra("request_id");
    }

    private void setDefaults() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetAllInOneList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }

    private void setEventHandler() {
        img_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAtleastOneChecked()) {
                    Utilities.showAlertDialog(context, "Alert", "Please Select Any One Details", false);
                } else {
                    toBeSharedAllInOneList = new ArrayList<>();
                    for (int i = 0; i < allInOneList.size(); i++) {
                        if (allInOneList.get(i).isChecked()) {
                            toBeSharedAllInOneList.add(allInOneList.get(i));
                        }
                    }
                    startShareLoop();
                }
            }
        });
    }

    private void startShareLoop() {
        for (int i = 0; i < toBeSharedAllInOneList.size(); i++) {
            if (toBeSharedAllInOneList.get(i).isChecked()) {
                setSelectionFilter(toBeSharedAllInOneList.get(i));
                return;
            }
        }
    }

    public class GetAllInOneList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            try {
                if (!result.equals("")) {
                    allInOneList = new ArrayList<>();
                    AllInOnePojo pojoDetails = new Gson().fromJson(result, AllInOnePojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();
                    if (type.equalsIgnoreCase("success")) {
                        allInOneList = pojoDetails.getResult();
                        ArrayList<AllInOneModel> sortedAllInOneList = new ArrayList<>();

                        for (int i = 0; i < allInOneList.size(); i++) {
                            if (!allInOneList.get(i).getStatus().equalsIgnoreCase("Duplicate")) {
                                sortedAllInOneList.add(allInOneList.get(i));
                            }
                        }
                        allInOneList = sortedAllInOneList;
                        rv_allinonelist.setAdapter(new GetAllInOneForShareAdapter(context, allInOneList));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetAllInOneForShareAdapter extends RecyclerView.Adapter<GetAllInOneForShareAdapter.MyViewHolder> {

        private List<AllInOneModel> resultArrayList;
        private final UserSessionManager session;
        private Context context;
        private String name;

        public GetAllInOneForShareAdapter(Context context, List<AllInOneModel> resultArrayList) {
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
            holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getAddress_type().charAt(0)));
            holder.tv_alias.setText(resultArrayList.get(position).getAddress_type());
            holder.tv_name.setText(resultArrayList.get(position).getAlias());
            holder.tv_details.setText(resultArrayList.get(position).getName());


            holder.cb_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.cb_select.isChecked())
                        allInOneList.get(position).setChecked(true);
                    else
                        allInOneList.get(position).setChecked(false);
                }
            });

        }

        @Override
        public int getItemCount() {
            return resultArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_initletter, tv_alias, tv_name, tv_details;
            private CheckBox cb_select;

            public MyViewHolder(View view) {
                super(view);
                tv_initletter = view.findViewById(R.id.tv_initletter);
                tv_alias = view.findViewById(R.id.tv_alias);
                tv_name = view.findViewById(R.id.tv_name);
                tv_details = view.findViewById(R.id.tv_details);
                cb_select = view.findViewById(R.id.cb_select);

            }
        }

    }

    private boolean isAtleastOneChecked() {
        for (int i = 0; i < allInOneList.size(); i++)
            if (allInOneList.get(i).isChecked())
                return true;
        return false;
    }

    private void setSelectionFilter(final AllInOneModel allInOneDetails) {
        sentAllInOneDetails = allInOneDetails;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_shareallinone, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle(allInOneDetails.getAddress_type() + " - " + allInOneDetails.getName());

        final CheckBox cb_addresstype = promptView.findViewById(R.id.cb_addresstype);
        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_address = promptView.findViewById(R.id.cb_address);
        final CheckBox cb_mobile = promptView.findViewById(R.id.cb_mobile);
        final CheckBox cb_landline = promptView.findViewById(R.id.cb_landline);
        final CheckBox cb_contactperson = promptView.findViewById(R.id.cb_contactperson);
        final CheckBox cb_email = promptView.findViewById(R.id.cb_email);
        final CheckBox cb_website = promptView.findViewById(R.id.cb_website);
        final CheckBox cb_maplocation = promptView.findViewById(R.id.cb_maplocation);
        final CheckBox cb_visitcard = promptView.findViewById(R.id.cb_visitcard);
        final CheckBox cb_photo = promptView.findViewById(R.id.cb_photo);

        final CheckBox cb_accountholdername = promptView.findViewById(R.id.cb_accountholdername);
        final CheckBox cb_bankname = promptView.findViewById(R.id.cb_bankname);
        final CheckBox cb_ifsccode = promptView.findViewById(R.id.cb_ifsccode);
        final CheckBox cb_accno = promptView.findViewById(R.id.cb_accno);
        final CheckBox cb_bankfile = promptView.findViewById(R.id.cb_bankfile);

        final CheckBox cb_panno = promptView.findViewById(R.id.cb_panno);
        final CheckBox cb_panfile = promptView.findViewById(R.id.cb_panfile);
        final CheckBox cb_gstno = promptView.findViewById(R.id.cb_gstno);
        final CheckBox cb_gstfile = promptView.findViewById(R.id.cb_gstfile);

        if (allInOneDetails.getAddress_type_id().equals("")) {
            cb_addresstype.setVisibility(View.GONE);
            cb_addresstype.setChecked(false);
        } else {
            cb_addresstype.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getName().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getMobile_number().equals("")) {
            cb_mobile.setVisibility(View.GONE);
            cb_mobile.setChecked(false);
        } else {
            cb_mobile.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getEmail_id().equals("")) {
            cb_email.setVisibility(View.GONE);
            cb_email.setChecked(false);
        } else {
            cb_email.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getLandline_number().trim().equals("")) {
            cb_landline.setVisibility(View.GONE);
            cb_landline.setChecked(false);
        } else {
            cb_landline.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getContact_person_name().trim().equals("") &&
                allInOneDetails.getContact_person_mobile().trim().equals("")) {
            cb_contactperson.setVisibility(View.GONE);
            cb_contactperson.setChecked(false);
        } else {
            cb_contactperson.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getWebsite().equals("")) {
            cb_website.setVisibility(View.GONE);
            cb_website.setChecked(false);
        } else {
            cb_website.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getMap_location_latitude().equals("") && allInOneDetails.getMap_location_longitude().equals("")) {
            cb_maplocation.setVisibility(View.GONE);
            cb_maplocation.setChecked(false);
        } else {
            cb_maplocation.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getVisiting_card().equals("")) {
            cb_visitcard.setVisibility(View.GONE);
            cb_visitcard.setChecked(false);
        } else {
            cb_visitcard.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getPhoto().equals("")) {
            cb_photo.setVisibility(View.GONE);
            cb_photo.setChecked(false);
        } else {
            cb_photo.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getAccount_holder_name().equals("")) {
            cb_accountholdername.setVisibility(View.GONE);
            cb_accountholdername.setChecked(false);
        } else {
            cb_accountholdername.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getBank_name().equals("")) {
            cb_bankname.setVisibility(View.GONE);
            cb_bankname.setChecked(false);
        } else {
            cb_bankname.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getIfsc_code().equals("")) {
            cb_ifsccode.setVisibility(View.GONE);
            cb_ifsccode.setChecked(false);
        } else {
            cb_ifsccode.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getAccount_number().equals("")) {
            cb_accno.setVisibility(View.GONE);
            cb_accno.setChecked(false);
        } else {
            cb_accno.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getBank_document().equals("")) {
            cb_bankfile.setVisibility(View.GONE);
            cb_bankfile.setChecked(false);
        } else {
            cb_bankfile.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getPan_number().equals("")) {
            cb_panno.setVisibility(View.GONE);
        } else {
            cb_panno.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getPan_document().equals("")) {
            cb_panfile.setVisibility(View.GONE);
        } else {
            cb_panfile.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getGst_number().equals("")) {
            cb_gstno.setVisibility(View.GONE);
            cb_gstno.setChecked(false);
        } else {
            cb_gstno.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getGst_document().equals("")) {
            cb_gstfile.setVisibility(View.GONE);
            cb_gstfile.setChecked(false);
        } else {
            cb_gstfile.setVisibility(View.VISIBLE);
        }

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                if (cb_addresstype.isChecked()) {
                    addresstype = allInOneDetails.getAddress_type_id();
                } else {
                    addresstype = "";
                }

                if (cb_name.isChecked()) {
                    u_name = allInOneDetails.getName();
                    alias = allInOneDetails.getAlias();
                } else {
                    u_name = "";
                    alias = "";
                }

                if (cb_address.isChecked()) {
                    address_line_1 = allInOneDetails.getAddress_line_one();
                    address_line_2 = allInOneDetails.getAddress_line_two();
                    country = allInOneDetails.getCountry();
                    state = allInOneDetails.getState();
                    district = allInOneDetails.getDistrict();
                    pincode = allInOneDetails.getPincode();

                } else {
                    address_line_1 = "";
                    address_line_2 = "";
                    country = "";
                    district = "";
                    pincode = "";
                    state = "";
                }

                if (cb_mobile.isChecked()) {
                    u_mobile = allInOneDetails.getMobile_number();
                } else {
                    u_mobile = "";
                }

                if (cb_email.isChecked()) {
                    email = allInOneDetails.getEmail_id();
                } else {
                    email = "";
                }

                if (cb_landline.isChecked()) {
                    landline = allInOneDetails.getLandline_number();
                } else {
                    landline = "";
                }

                if (cb_contactperson.isChecked()) {
                    contactPersonName = allInOneDetails.getContact_person_name();
                    contactPersonMobile = allInOneDetails.getContact_person_mobile();
                } else {
                    contactPersonName = "";
                    contactPersonMobile = "";
                }

                if (cb_website.isChecked()) {
                    website = allInOneDetails.getWebsite();
                } else {
                    website = "";
                }

                if (cb_maplocation.isChecked()) {
                    map_location_lattitude = allInOneDetails.getMap_location_latitude();
                    map_location_logitude = allInOneDetails.getMap_location_longitude();
                } else {
                    map_location_lattitude = "";
                    map_location_logitude = "";
                }

                if (cb_visitcard.isChecked()) {
                    visiting_card = allInOneDetails.getVisiting_card();
                } else {
                    visiting_card = "";
                }

                if (cb_photo.isChecked()) {
                    photo = allInOneDetails.getPhoto();
                } else {
                    photo = "";
                }


                if (cb_accountholdername.isChecked()) {
                    holder_name = allInOneDetails.getAccount_holder_name();
                    alias = allInOneDetails.getAccount_holder_alias();
                } else {
                    holder_name = "";
                    alias = "";
                }
                if (cb_bankname.isChecked()) {
                    bank_name = allInOneDetails.getBank_name();
                } else {
                    bank_name = "";
                }
                if (cb_ifsccode.isChecked()) {
                    ifsc_code = allInOneDetails.getIfsc_code();
                } else {
                    ifsc_code = "";
                }
                if (cb_accno.isChecked()) {
                    acc_no = allInOneDetails.getAccount_number();
                } else {
                    acc_no = "";
                }
                if (cb_bankfile.isChecked()) {
                    bank_doc = allInOneDetails.getBank_document();
                } else {
                    bank_doc = "";
                }

                if (cb_panno.isChecked()) {
                    pan_no = allInOneDetails.getPan_number();
                } else {
                    pan_no = "";
                }

                if (cb_panfile.isChecked()) {
                    pan_doc = allInOneDetails.getPan_document();
                } else {
                    pan_doc = "";
                }

                if (cb_gstno.isChecked()) {
                    gst_no = allInOneDetails.getGst_number();
                } else {
                    gst_no = "";
                }

                if (cb_gstfile.isChecked()) {
                    gst_doc = allInOneDetails.getGst_document();
                } else {
                    gst_doc = "";
                }

                createDialogForShare(allInOneDetails);

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

    private void createDialogForShare(final AllInOneModel allInOneDetails) {
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
                            allInOneDetails.getAll_in_one_id()
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

        alertDialogBuilder.setCancelable(false);
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
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
                obj.put("request_id", request_id);
                obj.put("status", "import");
                obj.put("o_address_type_id", addresstype);
                obj.put("o_name", u_name);
                obj.put("o_alias", alias);
                obj.put("o_landline_number", landline);
                obj.put("o_contact_person_name", contactPersonName);
                obj.put("o_contact_person_mobile", contactPersonMobile);
                obj.put("o_address_line_one", address_line_1);
                obj.put("o_address_line_two", address_line_2);
                obj.put("o_country", country);
                obj.put("o_state", state);
                obj.put("o_district", district);
                obj.put("o_pincode", pincode);
                obj.put("o_email_id", email);
                obj.put("o_website", website);
                obj.put("o_visiting_card", visiting_card);
                obj.put("o_map_location_logitude", map_location_logitude);
                obj.put("o_map_location_latitude", map_location_lattitude);
                obj.put("o_photo", photo);
                obj.put("o_mobile_number", u_mobile);
                obj.put("o_account_holder_name", holder_name);
                obj.put("o_account_holder_alias", alias);
                obj.put("o_bank_name", bank_name);
                obj.put("o_ifsc_code", ifsc_code);
                obj.put("o_account_number", acc_no);
                obj.put("o_bank_document", bank_doc);
                obj.put("o_pan_number", pan_no);
                obj.put("o_gst_number", gst_no);
                obj.put("o_pan_document", pan_doc);
                obj.put("o_gst_document", gst_doc);

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

                        toBeSharedAllInOneList.remove(sentAllInOneDetails);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Details Shared Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (toBeSharedAllInOneList.size() != 0) {
                                    startShareLoop();
                                } else {
                                    finish();
                                }
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
        mToolbar.setTitle("Select All in One Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
