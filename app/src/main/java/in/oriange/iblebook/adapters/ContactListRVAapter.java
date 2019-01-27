package in.oriange.iblebook.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.ContactListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class ContactListRVAapter extends RecyclerView.Adapter<ContactListRVAapter.MyViewHolder> {

    private Context context;
    private List<ContactListPojo> contactList;
    private UserSessionManager session;
    private String user_id;
    private List<String> mobileNumbers;
    private static JSONArray MobileNumbers;

    public ContactListRVAapter(Context context, List<ContactListPojo> contactList, List<String> mobileNumbers) {
        this.context = context;
        this.contactList = contactList;
        this.mobileNumbers = mobileNumbers;
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_contact, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_initletter.setText(contactList.get(position).getInitLetter());
        holder.tv_name.setText(contactList.get(position).getName());
        holder.tv_phoneno.setText(contactList.get(position).getPhoneNo());
        if (checkMobile(contactList.get(position).getPhoneNo())) {
            holder.tv_img.setVisibility(View.VISIBLE);
        } else {
            holder.tv_img.setVisibility(View.INVISIBLE);
        }
        holder.rl_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRequestAlert(contactList, position);
            }
        });
    }

    public boolean checkMobile(String mobile) {
        try {
            for (String m : mobileNumbers) {
                if (mobile.equals(m) || mobile.equals("+91".concat(m)) || mobile.equals("0".concat(m))) {
                    return true;
                }

            }
        } catch (Exception e) {

        }

        return false;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private void createRequestAlert(final List<ContactListPojo> contactList, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_requestlayout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView tv_initletter = promptView.findViewById(R.id.tv_initletter);
        final TextView tv_name = promptView.findViewById(R.id.tv_name);
        final EditText edt_message = promptView.findViewById(R.id.edt_message);
        final TextView tv_requesttype = promptView.findViewById(R.id.tv_requesttype);

        tv_initletter.setText(String.valueOf(contactList.get(position).getName().charAt(0)));
        tv_name.setText(contactList.get(position).getName() + " (" + contactList.get(position).getPhoneNo() + ") ");

        tv_requesttype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] remarkName = {"Address Detail", "PAN Detail", "GST Detail", "Bank Detail", "All in One Details"};

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setTitle("Select Type");
                builderSingle.setCancelable(false);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

                for (int i = 0; i < remarkName.length; i++) {
                    arrayAdapter.add(remarkName[i]);
                }

                builderSingle.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_requesttype.setText(remarkName[which]);
                    }
                });
                AlertDialog alert11 = builderSingle.create();
                alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alert11.show();
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                if (tv_requesttype.getText().toString().trim().equals("")) {
                    Utilities.showMessageString(context, "Please Select Type");
                    return;
                }
                if (edt_message.getText().toString().trim().equals("")) {
                    Utilities.showMessageString(context, "Please Enter Message");
                    return;
                }
                String type = "";

                if (tv_requesttype.getText().toString().trim().equals("Address Detail"))
                    type = "address";
                else if (tv_requesttype.getText().toString().trim().equals("PAN Detail"))
                    type = "pan";
                else if (tv_requesttype.getText().toString().trim().equals("GST Detail"))
                    type = "gst";
                else if (tv_requesttype.getText().toString().trim().equals("Bank Detail"))
                    type = "bank";
                else if (tv_requesttype.getText().toString().trim().equals("All in One Details"))
                    type = "allinone";

                if (Utilities.isNetworkAvailable(context)) {
                    new SendRequest().execute(
                            edt_message.getText().toString().trim(),
                            user_id,
                            contactList.get(position).getPhoneNo(),
                            type
                    );
                } else {
                    Utilities.showMessageString(context, "Please Check Internet Connection");
                }

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int view) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert.show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_mainlayout;
        private TextView tv_initletter, tv_name, tv_phoneno;
        private ImageView tv_img;


        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_name = (TextView) view.findViewById(R.id.tv_bankname);
            tv_phoneno = (TextView) view.findViewById(R.id.tv_accountno);
            rl_mainlayout = view.findViewById(R.id.rl_mainlayout);
            tv_img = view.findViewById(R.id.ic_image);
        }
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

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
            JSONObject obj = new JSONObject();
            try {
                obj.put("task", "RequestData");
                obj.put("message", params[0]);
                obj.put("sender_id", params[1]);
                obj.put("mobile", params[2]);
                obj.put("type", params[3]);
                obj.put("record_id", "");
                obj.put("status", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.SENDREQUESTAPI, obj.toString());
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
                        Utilities.showAlertDialog(context, "Success", "Request Sent Successfully", true);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
