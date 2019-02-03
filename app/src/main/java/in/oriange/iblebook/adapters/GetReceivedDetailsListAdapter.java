package in.oriange.iblebook.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.ReceivedNotifications_Fragment;
import in.oriange.iblebook.fragments.Received_Address_Fragment;
import in.oriange.iblebook.fragments.Received_AllInOne_Fragment;
import in.oriange.iblebook.fragments.Received_Bank_Fragment;
import in.oriange.iblebook.fragments.Received_GST_Fragment;
import in.oriange.iblebook.fragments.Received_PAN_Fragment;
import in.oriange.iblebook.models.GetReceivedDetailsListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetReceivedDetailsListAdapter extends RecyclerView.Adapter<GetReceivedDetailsListAdapter.MyViewHolder> {

    private static List<GetReceivedDetailsListPojo> resultArrayList;
    private final UserSessionManager session;
    private Context context;
    private String name, user_id;

    public GetReceivedDetailsListAdapter(Context context, List<GetReceivedDetailsListPojo> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            name = json.getString("name");
            user_id = json.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_received_request, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (resultArrayList.get(position).getType().equals("address")) {
            holder.tv_type.setText("Shared Address Details");
        } else if (resultArrayList.get(position).getType().equals("pan")) {
            holder.tv_type.setText("Shared PAN Details");
        } else if (resultArrayList.get(position).getType().equals("gst")) {
            holder.tv_type.setText("Shared GST Details");
        } else if (resultArrayList.get(position).getType().equals("bank")) {
            holder.tv_type.setText("Shared Bank Details");
        } else if (resultArrayList.get(position).getType().equals("allinone")) {
            holder.tv_type.setText("Shared All in One Details");
        }

        holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getSender_name().charAt(0)));
        holder.tv_name.setText(resultArrayList.get(position).getSender_name());
        holder.tv_message.setText(resultArrayList.get(position).getMessage());

        holder.fl_mainframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogForRequest(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    private void createDialogForRequest(final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_viewshareddetails, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        TextView tv_type = promptView.findViewById(R.id.tv_type);
        TextView tv_initletter = promptView.findViewById(R.id.tv_initletter);
        TextView tv_name = promptView.findViewById(R.id.tv_name);
        TextView tv_message = promptView.findViewById(R.id.tv_message);

        if (resultArrayList.get(position).getType().equals("address")) {
            tv_type.setText("Address Details Shared By");
        } else if (resultArrayList.get(position).getType().equals("pan")) {
            tv_type.setText("PAN Details Shared By");
        } else if (resultArrayList.get(position).getType().equals("gst")) {
            tv_type.setText("GST Details Shared By");
        } else if (resultArrayList.get(position).getType().equals("bank")) {
            tv_type.setText("Bank Details Shared By");
        } else if (resultArrayList.get(position).getType().equals("allinone")) {
            tv_type.setText("Shared All in One Details");
        }

        tv_initletter.setText(String.valueOf(resultArrayList.get(position).getSender_name().charAt(0)));
        tv_name.setText(resultArrayList.get(position).getSender_name());
        tv_message.setText(resultArrayList.get(position).getMessage());

        ImageView imv_call = promptView.findViewById(R.id.imv_call);

        imv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.getPackageName(), null)));
                    Utilities.showMessageString(context, "Please provide permission for making call");
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setIcon(R.drawable.ic_alert_red_24dp);
                    alertDialogBuilder.setMessage("Are you sure you want to make a call ?");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Yes", new DialogInterface.OnClickListener() {
                                @SuppressLint("MissingPermission")
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    context.startActivity(new Intent(Intent.ACTION_CALL,
                                            Uri.parse("tel:" + resultArrayList.get(position).getSender_mobile())));
                                }
                            });
                    alertDialogBuilder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = alertDialogBuilder.create();
                    alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alert11.show();
                }
            }
        });

        alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context)) {
                    new ApproveDetails().execute(String.valueOf(position));
                } else {
                    Utilities.showMessageString(context, "Please Check Your Internet Connection");
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context)) {
                    new RejectDetails().execute(String.valueOf(position));
                } else {
                    Utilities.showMessageString(context, "Please Check Your Internet Connection");
                }
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public void removeItem(int position) {
        resultArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_initletter, tv_name, tv_message, tv_type;
        private FrameLayout fl_mainframe;

        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

    public class RejectDetails extends AsyncTask<String, Void, String> {
        int position;
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
            position = Integer.parseInt(params[0]);
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "rejectSharedRecord");
            obj.addProperty("shared_details_id", resultArrayList.get(Integer.parseInt(params[0])).getShared_details_id());
            obj.addProperty("new_record_id", resultArrayList.get(Integer.parseInt(params[0])).getNew_record_id());
            res = WebServiceCalls.APICall(ApplicationConstants.REJECTREQUESTAPI, obj.toString());
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
                        new ReceivedNotifications_Fragment.GetDetailsList().execute();
                        removeItem(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Details Rejected Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert11 = builder.create();
                        alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alert11.show();
                    } else {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ApproveDetails extends AsyncTask<String, Void, String> {
        int position;
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
            position = Integer.parseInt(params[0]);
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "importSharedRecord");
            obj.addProperty("user_id", user_id);
            obj.addProperty("new_record_id", resultArrayList.get(Integer.parseInt(params[0])).getNew_record_id());
            obj.addProperty("shared_details_id", resultArrayList.get(Integer.parseInt(params[0])).getShared_details_id());
            res = WebServiceCalls.APICall(ApplicationConstants.APPROVEREQUESTAPI, obj.toString());
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

                        new ReceivedNotifications_Fragment.GetDetailsList().execute();
                        removeItem(position);


                        new Received_Address_Fragment.GetAddressList().execute();
                        new Received_Bank_Fragment.GetBankList().execute();
                        new Received_GST_Fragment.GetGSTList().execute();
                        new Received_PAN_Fragment.GetPANList().execute();
                        new Received_AllInOne_Fragment.GetAllInOneList().execute();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Details Approved Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert11 = builder.create();
                        alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alert11.show();
                    } else {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
