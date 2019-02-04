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
import in.oriange.iblebook.models.GetSentRequestsListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetSentRequestListAdapter extends RecyclerView.Adapter<GetSentRequestListAdapter.MyViewHolder> {

    private static List<GetSentRequestsListPojo> resultArrayList;
    private final UserSessionManager session;
    private Context context;
    private String name;

    public GetSentRequestListAdapter(Context context, List<GetSentRequestsListPojo> resultArrayList) {
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
        View view = inflater.inflate(R.layout.list_sent_request, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (resultArrayList.get(position).getType().equals("address")) {
            holder.tv_type.setText("Requested For Address Details");
        } else if (resultArrayList.get(position).getType().equals("pan")) {
            holder.tv_type.setText("Requested For PAN Details");
        } else if (resultArrayList.get(position).getType().equals("gst")) {
            holder.tv_type.setText("Requested For GST Details");
        } else if (resultArrayList.get(position).getType().equals("bank")) {
            holder.tv_type.setText("Requested For Bank Details");
        } else if (resultArrayList.get(position).getType().equals("allinone")) {
            holder.tv_type.setText("Requested For All in One Details");
        }

        if (resultArrayList.get(position).getStatus().equalsIgnoreCase("Accepted")) {
            holder.tv_requeststatus.setText("Status - Responded");
        } else if (resultArrayList.get(position).getStatus().equalsIgnoreCase("Dismiss")) {
            holder.tv_requeststatus.setText("Status - Rejected");
        } else if (resultArrayList.get(position).getStatus().equalsIgnoreCase("Active")) {
            holder.tv_requeststatus.setText("Status - Pending");
        }

        holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getMessage().charAt(0)));
        holder.tv_name.setText(resultArrayList.get(position).getMobile());
        holder.tv_message.setText(resultArrayList.get(position).getMessage());

        holder.imv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                            Uri.parse("tel:" + resultArrayList.get(position).getMobile())));
                                }
                            });
                    alertDialogBuilder.setNegativeButton(
                            "No", new DialogInterface.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_initletter, tv_name, tv_message, tv_type, tv_requeststatus;
        private ImageView imv_call;
        private FrameLayout fl_mainframe;

        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            tv_requeststatus = (TextView) view.findViewById(R.id.tv_requeststatus);
            imv_call = view.findViewById(R.id.imv_call);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

}
