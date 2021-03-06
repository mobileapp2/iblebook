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
import in.oriange.iblebook.activities.Edit_PAN_Activity;
import in.oriange.iblebook.activities.View_PAN_Activity;
import in.oriange.iblebook.fragments.Offline_PAN_Fragment;
import in.oriange.iblebook.models.GetTaxListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetOfflinePANListAdapter extends RecyclerView.Adapter<GetOfflinePANListAdapter.MyViewHolder> {

    private static List<GetTaxListPojo> resultArrayList;
    private final UserSessionManager session;
    private Context context;
    private String name, STATUS;
    private DataBaseHelper dbHelper;

    public GetOfflinePANListAdapter(Context context, List<GetTaxListPojo> resultArrayList, String STATUS) {
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
        holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getName().charAt(0)));
        holder.tv_alias.setText(resultArrayList.get(position).getAlias());
        holder.tv_name.setText(resultArrayList.get(position).getName());
        holder.tv_gstno.setText(resultArrayList.get(position).getPan_number());

        holder.fl_mainframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, View_PAN_Activity.class);
                intent.putExtra("tax_id", resultArrayList.get(position).getTax_id());
                intent.putExtra("name", resultArrayList.get(position).getName());
                intent.putExtra("alias", resultArrayList.get(position).getAlias());
                intent.putExtra("pan_number", resultArrayList.get(position).getPan_number());
                intent.putExtra("pan_document", resultArrayList.get(position).getPan_document());
                intent.putExtra("created_by", resultArrayList.get(position).getCreated_by());
                intent.putExtra("updated_by", resultArrayList.get(position).getUpdated_by());
                intent.putExtra("STATUS", STATUS);
                context.startActivity(intent);
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
                                Intent intent = new Intent(context, Edit_PAN_Activity.class);
                                intent.putExtra("tax_id", resultArrayList.get(position).getTax_id());
                                intent.putExtra("name", resultArrayList.get(position).getName());
                                intent.putExtra("alias", resultArrayList.get(position).getAlias());
                                intent.putExtra("pan_number", resultArrayList.get(position).getPan_number());
                                intent.putExtra("pan_document", resultArrayList.get(position).getPan_document());
                                intent.putExtra("created_by", resultArrayList.get(position).getCreated_by());
                                intent.putExtra("updated_by", resultArrayList.get(position).getUpdated_by());
                                intent.putExtra("STATUS", STATUS);
                                context.startActivity(intent);
                                break;
                            case R.id.menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Are you sure you want to delete this item?");
                                builder.setTitle("Alert");
                                builder.setIcon(R.drawable.ic_alert_red_24dp);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        if (STATUS.equals("ONLINE")) {
//                                            new DeletePANDetails().execute(String.valueOf(position));
//                                        } else {
//                                            long result = dbHelper.deleteTaxDetailsFromDb(resultArrayList.get(position).getTax_id());
//                                            if (result != -1) {
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                                builder.setMessage("PAN Details Deleted Successfully");
//                                                builder.setIcon(R.drawable.ic_success_24dp);                        builder.setTitle("Success");
//                                                builder.setCancelable(false);
//                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int id) {
//                                                        Offline_PAN_Fragment.setDefault();
//                                                    }
//                                                });
//                                                AlertDialog alertD = builder.create();                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;                        alertD.show();
//                                            }
//                                        }
                                        new DeletePANDetails().execute(String.valueOf(position));
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
                                builder1.setMessage("Where to swap this PAN details?");
                                builder1.setIcon(R.drawable.icon_swap);
                                builder1.setCancelable(false);
                                builder1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                builder1.setNegativeButton("Personal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new MoveAddressDetails().execute(String.valueOf(position), "online");
                                    }
                                });
                                builder1.setPositiveButton("Received", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new MoveAddressDetails().execute(String.valueOf(position), "received");

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
        return resultArrayList.size();
    }

    private void setSelectionFilter(final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_sharepan, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_panno = promptView.findViewById(R.id.cb_panno);
        final CheckBox cb_file = promptView.findViewById(R.id.cb_file);

        if (resultArrayList.get(position).getName().equals("")) {
            cb_name.setVisibility(View.GONE);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getPan_number().equals("")) {
            cb_panno.setVisibility(View.GONE);
        } else {
            cb_panno.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getPan_document().equals("")) {
            cb_file.setVisibility(View.GONE);
        } else {
            cb_file.setVisibility(View.VISIBLE);
        }

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();
                if (cb_name.isChecked()) {
                    sb.append("Name - " + resultArrayList.get(position).getName() + "\n");
                }
                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + resultArrayList.get(position).getPan_number() + "\n");
                }
                String url = "";
                if (cb_file.isChecked()) {
                    url = resultArrayList.get(position).getPan_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("File - " + url + "\n");
                }

                if (!cb_name.isChecked() && !cb_panno.isChecked() && !cb_file.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedPANDetails", sb.toString());
                String finalDataShare = name + " shared PAN details with you " + "\n\n" + sb.toString() + "\n" + "via Iblebook \n" + "Click Here - " + ApplicationConstants.IBLEBOOK_PLAYSTORELINK;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, finalDataShare);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
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
                StringBuilder sb = new StringBuilder();
                if (cb_name.isChecked()) {
                    sb.append("Name - " + resultArrayList.get(position).getName() + "\n");
                }
                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + resultArrayList.get(position).getPan_number() + "\n");
                }
                String url = "";
                if (cb_file.isChecked()) {
                    url = resultArrayList.get(position).getPan_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("File - " + url + "\n");
                }

                if (!cb_name.isChecked() && !cb_panno.isChecked() && !cb_file.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedPANDetails", sb.toString());
                String finalDataShare = "PAN Details" + "\n" + sb.toString();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", finalDataShare);
                clipboard.setPrimaryClip(clip);
                Utilities.showMessageString(context, "Copied to clipboard");
            }
        });
        alertDialogBuilder.setCancelable(false);
        android.support.v7.app.AlertDialog alertD = alertDialogBuilder.create();
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

    public class DeletePANDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "DeleteData");
            obj.addProperty("tax_id", resultArrayList.get(Integer.parseInt(params[0])).getTax_id());
            res = WebServiceCalls.APICall(ApplicationConstants.TAXAPI, obj.toString());
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
                        builder.setMessage("PAN Details Deleted Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new Offline_PAN_Fragment.GetPANList().execute();
                                removeItem(position);
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    } else {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MoveAddressDetails extends AsyncTask<String, Void, String> {
        int position;
        String status;
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
            status = params[1];
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "pan");
            obj.addProperty("record_id", resultArrayList.get(Integer.parseInt(params[0])).getTax_id());
            obj.addProperty("status", status);
            res = WebServiceCalls.APICall(ApplicationConstants.MOVEAPI, obj.toString());
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
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setMessage("PAN details moved successfully.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    } else {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
