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
import in.oriange.iblebook.activities.ShareAddressDetails_Activity;
import in.oriange.iblebook.activities.ShareBankDetails_Activity;
import in.oriange.iblebook.activities.ShareGSTDetails_Activity;
import in.oriange.iblebook.activities.SharePANDetails_Activity;
import in.oriange.iblebook.fragments.ReceivedRequests_Fragment;
import in.oriange.iblebook.models.GetRequestsListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetReceivedRequestListAdapter extends RecyclerView.Adapter<GetReceivedRequestListAdapter.MyViewHolder> {

    private final UserSessionManager session;
    private Context context;
    private static List<GetRequestsListPojo> resultArrayList;
    private String name;

    public GetReceivedRequestListAdapter(Context context, List<GetRequestsListPojo> resultArrayList) {
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
        View view = inflater.inflate(R.layout.list_received_request, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout fl_mainframe;
        TextView tv_initletter, tv_name, tv_message;

        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

    private void createDialogForRequest(final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_viewrequest, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        TextView tv_initletter = promptView.findViewById(R.id.tv_initletter);
        TextView tv_name = promptView.findViewById(R.id.tv_name);
        TextView tv_message = promptView.findViewById(R.id.tv_message);

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

        alertDialogBuilder.setPositiveButton("share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                if (resultArrayList.get(position).getType().equals("address")) {
                    Intent intent = new Intent(context, ShareAddressDetails_Activity.class);
                    intent.putExtra("name", resultArrayList.get(position).getSender_name());
                    intent.putExtra("mobile", resultArrayList.get(position).getSender_mobile());
                    intent.putExtra("sender_id", resultArrayList.get(position).getSender_id());
                    intent.putExtra("type", resultArrayList.get(position).getType());
                    context.startActivity(intent);
                } else if (resultArrayList.get(position).getType().equals("pan")) {
                    Intent intent = new Intent(context, SharePANDetails_Activity.class);
                    intent.putExtra("name", resultArrayList.get(position).getSender_name());
                    intent.putExtra("mobile", resultArrayList.get(position).getSender_mobile());
                    intent.putExtra("sender_id", resultArrayList.get(position).getSender_id());
                    intent.putExtra("type", resultArrayList.get(position).getType());
                    context.startActivity(intent);
                } else if (resultArrayList.get(position).getType().equals("gst")) {
                    Intent intent = new Intent(context, ShareGSTDetails_Activity.class);
                    intent.putExtra("name", resultArrayList.get(position).getSender_name());
                    intent.putExtra("mobile", resultArrayList.get(position).getSender_mobile());
                    intent.putExtra("sender_id", resultArrayList.get(position).getSender_id());
                    intent.putExtra("type", resultArrayList.get(position).getType());
                    context.startActivity(intent);
                } else if (resultArrayList.get(position).getType().equals("bank")) {
                    Intent intent = new Intent(context, ShareBankDetails_Activity.class);
                    intent.putExtra("name", resultArrayList.get(position).getSender_name());
                    intent.putExtra("mobile", resultArrayList.get(position).getSender_mobile());
                    intent.putExtra("sender_id", resultArrayList.get(position).getSender_id());
                    intent.putExtra("type", resultArrayList.get(position).getType());
                    context.startActivity(intent);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Dismiss Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setIcon(R.drawable.ic_alert_red_24dp);
                alertDialogBuilder.setMessage("Are you sure you want to dismiss this request?");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton(
                        "Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                new DismissRequest().execute(String.valueOf(position));
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
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class DismissRequest extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "dismissRequestedRecord");
            obj.addProperty("request_id", resultArrayList.get(Integer.parseInt(params[0])).getRequest_id());
            res = WebServiceCalls.APICall(ApplicationConstants.DISMISSREQUESTSAPI, obj.toString());
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
                        new ReceivedRequests_Fragment.GetRequestList().execute();
                        removeItem(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Request Dismissed Successfully");
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

    public void removeItem(int position) {
        resultArrayList.remove(position);
        notifyItemRemoved(position);
    }
}
