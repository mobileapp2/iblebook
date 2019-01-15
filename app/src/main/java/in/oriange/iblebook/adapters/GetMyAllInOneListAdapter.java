package in.oriange.iblebook.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetMyAllInOneListAdapter extends RecyclerView.Adapter<GetMyAllInOneListAdapter.MyViewHolder> {

    private static List<AllInOneModel> resultArrayList;
    private final UserSessionManager session;
    private Context context;
    private String name, STATUS;
    private DataBaseHelper dbHelper;

    public GetMyAllInOneListAdapter(Context context, List<AllInOneModel> resultArrayList, String STATUS) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.STATUS = STATUS;
        session = new UserSessionManager(context);
        dbHelper = new DataBaseHelper(context);
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
        View view = inflater.inflate(R.layout.list_row_gst, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_alias.setText(resultArrayList.get(position).getAddress_type());

        holder.fl_mainframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.imv_more.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.imv_more);
                popup.inflate(R.menu.list_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_share:
                                setSelectionFilter(position);
                                break;
                            case R.id.menu_edit:

                                break;
                            case R.id.menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Are you sure you want to delete this item?");
                                builder.setTitle("Alert");
                                builder.setIcon(R.drawable.ic_alert_red_24dp);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        new DeleteAddressDetails().execute(String.valueOf(position));
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertD = builder.create();
                                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                                alertD.show();
                                break;
                            case R.id.menu_move:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle("Swap");
                                builder1.setMessage("Where to icon_swap this address?");
                                builder1.setIcon(R.drawable.icon_swap);
                                builder1.setCancelable(false);
                                builder1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                builder1.setPositiveButton("Received", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        new MoveAddressDetails().execute(String.valueOf(position), "received");

                                    }
                                });
                                builder1.setNegativeButton("Offline", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        new MoveAddressDetails().execute(String.valueOf(position), "offline");
                                    }
                                });
                                AlertDialog alertD1 = builder1.create();
                                alertD1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                                alertD1.show();
                                break;


                        }
                        return false;
                    }
                });

                MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), holder.imv_more);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList == null ? 0 : resultArrayList.size();
    }

    private void setSelectionFilter(final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_shareallinone, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");



        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int view) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNeutralButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void removeItem(int position) {
        resultArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_initletter, tv_alias, tv_name, tv_gstno;
        private FrameLayout fl_mainframe;
        private ImageView imv_more;

        public MyViewHolder(View view) {
            super(view);
            tv_initletter = view.findViewById(R.id.tv_initletter);
            tv_alias = view.findViewById(R.id.tv_alias);
            tv_name = view.findViewById(R.id.tv_name);
            tv_gstno = view.findViewById(R.id.tv_gstno);
            imv_more = view.findViewById(R.id.imv_more);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

//    public class DeleteAddressDetails extends AsyncTask<String, Void, String> {
//        int position;
//        ProgressDialog pd;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(context);
//            pd.setMessage("Please wait ...");
//            pd.setCancelable(false);
//            pd.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            position = Integer.parseInt(params[0]);
//            String res = "[]";
//            JsonObject obj = new JsonObject();
//            obj.addProperty("type", "delete");
//            obj.addProperty("address_id", resultArrayList.get(Integer.parseInt(params[0])).getAddress_id());
//            res = WebServiceCalls.APICall(ApplicationConstants.ADDRESSAPI, obj.toString());
//            return res;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            String type = "", message = "";
//            try {
//                pd.dismiss();
//                if (!result.equals("")) {
//                    JSONObject mainObj = new JSONObject(result);
//                    type = mainObj.getString("type");
//                    message = mainObj.getString("message");
//                    if (type.equalsIgnoreCase("success")) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setIcon(R.drawable.ic_success_24dp);
//                        builder.setTitle("Success");
//                        builder.setMessage("Address Details Deleted Successfully");
//                        builder.setCancelable(false);
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                new My_Address_Fragment.GetAddressList().execute();
//                                removeItem(position);
//                            }
//                        });
//                        AlertDialog alertD = builder.create();
//                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
//                        alertD.show();
//                    } else {
//
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public class MoveAddressDetails extends AsyncTask<String, Void, String> {
//        int position;
//        String status;
//        ProgressDialog pd;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(context);
//            pd.setMessage("Please wait ...");
//            pd.setCancelable(false);
//            pd.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            position = Integer.parseInt(params[0]);
//            status = params[1];
//            String res = "[]";
//            JsonObject obj = new JsonObject();
//            obj.addProperty("type", "address");
//            obj.addProperty("record_id", resultArrayList.get(Integer.parseInt(params[0])).getAddress_id());
//            obj.addProperty("status", status);
//            res = WebServiceCalls.APICall(ApplicationConstants.MOVEAPI, obj.toString());
//            return res;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            String type = "", message = "";
//            try {
//                pd.dismiss();
//                if (!result.equals("")) {
//                    JSONObject mainObj = new JSONObject(result);
//                    type = mainObj.getString("type");
//                    message = mainObj.getString("message");
//                    if (type.equalsIgnoreCase("success")) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setIcon(R.drawable.ic_success_24dp);
//                        builder.setTitle("Success");
//                        builder.setMessage("Address moved successfully.");
//                        builder.setCancelable(false);
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                            }
//                        });
//                        AlertDialog alertD = builder.create();
//                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
//                        alertD.show();
//                    } else {
//
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
