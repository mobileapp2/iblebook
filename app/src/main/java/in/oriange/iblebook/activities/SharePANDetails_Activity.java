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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class SharePANDetails_Activity extends Activity {

    private Context context;
    private RecyclerView rv_panlist;
    private String user_id;
    private ArrayList<GetTaxListPojo> panList;
    private ArrayList<GetTaxListPojo> toBeSharedpanList;
    public LinearLayout ll_parent;
    private ImageView img_check;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserSessionManager session;
    private String mobile, type, name, sender_id, sender_mobile, request_id;
    private String p_name, alias, pan_no, pan_doc, gst_no, gst_doc;
    private GetTaxListPojo sentTaxDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pan_details);

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
        hideSoftKeyboard(SharePANDetails_Activity.this);
    }

    private void init() {
        context = SharePANDetails_Activity.this;
        session = new UserSessionManager(context);
        panList = new ArrayList<>();
        toBeSharedpanList = new ArrayList<>();
        ll_parent = findViewById(R.id.ll_parent);
        rv_panlist = findViewById(R.id.rv_panlist);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_panlist.setLayoutManager(layoutManager);
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
            new GetPANList().execute();
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
                    toBeSharedpanList = new ArrayList<>();
                    for (int i = 0; i < panList.size(); i++) {
                        if (panList.get(i).isChecked()) {
                            toBeSharedpanList.add(panList.get(i));
                        }
                    }
                    startShareLoop();
                }
            }
        });
    }

    private void startShareLoop() {
        for (int i = 0; i < toBeSharedpanList.size(); i++) {
            if (toBeSharedpanList.get(i).isChecked()) {
                setSelectionFilter(toBeSharedpanList.get(i));
                return;
            }
        }
    }

    public class GetPANList extends AsyncTask<String, Void, String> {

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
                    rv_panlist.setAdapter(new GetPANForShareAdapter(context, panList));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                GetTaxListPojo summary = new GetTaxListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("status").equals("Duplicate")) {
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
                            }
                            rv_panlist.setAdapter(new GetPANForShareAdapter(context, panList));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetPANForShareAdapter extends RecyclerView.Adapter<GetPANForShareAdapter.MyViewHolder> {

        private List<GetTaxListPojo> resultArrayList;
        private final UserSessionManager session;
        private Context context;
        private String name;

        public GetPANForShareAdapter(Context context, List<GetTaxListPojo> resultArrayList) {
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
            holder.tv_alias.setText(resultArrayList.get(position).getAlias());
            holder.tv_name.setText(resultArrayList.get(position).getName());
            holder.tv_details.setText(resultArrayList.get(position).getPan_number());

            holder.cb_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.cb_select.isChecked())
                        panList.get(position).setChecked(true);
                    else
                        panList.get(position).setChecked(false);
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
        for (int i = 0; i < panList.size(); i++)
            if (panList.get(i).isChecked())
                return true;
        return false;
    }

    private void setSelectionFilter(final GetTaxListPojo panDetails) {
        sentTaxDetails = panDetails;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_sharepan, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle(panDetails.getName());

        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_panno = promptView.findViewById(R.id.cb_panno);
        final CheckBox cb_file = promptView.findViewById(R.id.cb_file);

        if (panDetails.getName().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (panDetails.getPan_number().equals("")) {
            cb_panno.setVisibility(View.GONE);
            cb_panno.setChecked(false);
        } else {
            cb_panno.setVisibility(View.VISIBLE);
        }

        if (panDetails.getPan_document().equals("")) {
            cb_file.setVisibility(View.GONE);
            cb_file.setChecked(false);
        } else {
            cb_file.setVisibility(View.VISIBLE);
        }

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();
                if (cb_name.isChecked()) {
                    p_name = panDetails.getName();
                    alias = panDetails.getAlias();
                } else {
                    p_name = "";
                    alias = "";
                }
                if (cb_panno.isChecked()) {
                    pan_no = panDetails.getPan_number();
                } else {
                    pan_no = "";
                }

                if (cb_file.isChecked()) {
                    pan_doc = panDetails.getPan_document();
                } else {
                    pan_doc = "";
                }
                gst_doc = "";
                gst_no = "";

                if (!cb_name.isChecked() && !cb_panno.isChecked() && !cb_file.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                createDialogForShare(panDetails);
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

    private void createDialogForShare(final GetTaxListPojo panDetails) {
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
                            panDetails.getTax_id()
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
                obj.put("mobile", params[2]);
                obj.put("type", params[3]);
                obj.put("record_id", params[4]);
                obj.put("request_id", request_id);
                obj.put("receiver_id", sender_id);
                obj.put("status", "import");
                obj.put("t_name", p_name);
                obj.put("t_alias", alias);
                obj.put("t_pan_number", pan_no);
                obj.put("t_gst_number", gst_no);
                obj.put("t_pan_document", pan_doc);
                obj.put("t_gst_document", gst_doc);

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

                        toBeSharedpanList.remove(sentTaxDetails);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Details Shared Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (toBeSharedpanList.size() != 0) {
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
        mToolbar.setTitle("Select PAN Detail");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
