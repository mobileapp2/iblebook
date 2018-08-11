package in.oriange.iblebook.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.Edit_Bank_Activity;
import in.oriange.iblebook.activities.View_Bank_Activity;
import in.oriange.iblebook.fragments.My_Bank_Fragment;
import in.oriange.iblebook.fragments.Offline_Bank_Fragment;
import in.oriange.iblebook.models.GetBankListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.WebServiceCalls;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GetBankListAdapter extends RecyclerView.Adapter<GetBankListAdapter.MyViewHolder> {

    private final UserSessionManager session;
    private Context context;
    private static List<GetBankListPojo> resultArrayList;
    private String name, NETSTAT;
    private DataBaseHelper dbHelper;

    public GetBankListAdapter(Context context, List<GetBankListPojo> resultArrayList, String NETSTAT) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.NETSTAT = NETSTAT;
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
        View view = inflater.inflate(R.layout.list_row_bank, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getBank_name().charAt(0)));
        holder.tv_bankname.setText(resultArrayList.get(position).getBank_name());
        holder.tv_accountno.setText(resultArrayList.get(position).getAccount_no());

        holder.fl_mainframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, View_Bank_Activity.class);
                intent.putExtra("position", position);
                intent.putExtra("bank_id", resultArrayList.get(position).getBank_id());
                intent.putExtra("account_holder_name", resultArrayList.get(position).getAccount_holder_name());
                intent.putExtra("alias", resultArrayList.get(position).getAlias());
                intent.putExtra("bank_name", resultArrayList.get(position).getBank_name());
                intent.putExtra("ifsc_code", resultArrayList.get(position).getIfsc_code());
                intent.putExtra("account_no", resultArrayList.get(position).getAccount_no());
                intent.putExtra("document", resultArrayList.get(position).getDocument());
                intent.putExtra("created_by", resultArrayList.get(position).getCreated_by());
                intent.putExtra("updated_by", resultArrayList.get(position).getUpdated_by());
                intent.putExtra("NETSTAT", NETSTAT);
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
                                Intent intent = new Intent(context, Edit_Bank_Activity.class);
                                intent.putExtra("position", position);
                                intent.putExtra("bank_id", resultArrayList.get(position).getBank_id());
                                intent.putExtra("account_holder_name", resultArrayList.get(position).getAccount_holder_name());
                                intent.putExtra("alias", resultArrayList.get(position).getAlias());
                                intent.putExtra("bank_name", resultArrayList.get(position).getBank_name());
                                intent.putExtra("ifsc_code", resultArrayList.get(position).getIfsc_code());
                                intent.putExtra("account_no", resultArrayList.get(position).getAccount_no());
                                intent.putExtra("document", resultArrayList.get(position).getDocument());
                                intent.putExtra("created_by", resultArrayList.get(position).getCreated_by());
                                intent.putExtra("updated_by", resultArrayList.get(position).getUpdated_by());
                                intent.putExtra("NETSTAT", NETSTAT);
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
                                        if (NETSTAT.equals("ONLINE")) {
                                            new DeleteBankDetails().execute(String.valueOf(position));
                                        } else {
                                            long result = dbHelper.deleteBankDetailsFromDb(resultArrayList.get(position).getBank_id());
                                            if (result != -1) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setMessage("Bank Details Deleted Successfully");
                                                builder.setTitle("Success");
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Offline_Bank_Fragment.setDefault();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout fl_mainframe;
        private TextView tv_initletter, tv_bankname, tv_accountno;
        private ImageView imv_more;

        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_bankname = (TextView) view.findViewById(R.id.tv_bankname);
            tv_accountno = (TextView) view.findViewById(R.id.tv_accountno);
            imv_more = view.findViewById(R.id.imv_more);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

    private void setSelectionFilter(final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_sharebank, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        TextView tv_name = promptView.findViewById(R.id.tv_name);
        TextView tv_bankname = promptView.findViewById(R.id.tv_bankname);
        TextView tv_ifsccode = promptView.findViewById(R.id.tv_ifsccode);
        TextView tv_accno = promptView.findViewById(R.id.tv_accno);
        TextView tv_file = promptView.findViewById(R.id.tv_file);

        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_bankname = promptView.findViewById(R.id.cb_bankname);
        final CheckBox cb_ifsccode = promptView.findViewById(R.id.cb_ifsccode);
        final CheckBox cb_accno = promptView.findViewById(R.id.cb_accno);
        final CheckBox cb_file = promptView.findViewById(R.id.cb_file);

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_name.isChecked())
                    cb_name.setChecked(true);
                else
                    cb_name.setChecked(false);
            }
        });
        tv_bankname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_bankname.isChecked())
                    cb_bankname.setChecked(true);
                else
                    cb_bankname.setChecked(false);
            }
        });
        tv_ifsccode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_ifsccode.isChecked())
                    cb_ifsccode.setChecked(true);
                else
                    cb_ifsccode.setChecked(false);
            }
        });
        tv_accno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_accno.isChecked())
                    cb_accno.setChecked(true);
                else
                    cb_accno.setChecked(false);
            }
        });
        tv_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_file.isChecked())
                    cb_file.setChecked(true);
                else
                    cb_file.setChecked(false);
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();
                if (cb_name.isChecked()) {
                    sb.append("Name of Account Holder - " + resultArrayList.get(position).getAccount_holder_name() + "\n");
                }
                if (cb_bankname.isChecked()) {
                    sb.append("Bank Name - " + resultArrayList.get(position).getBank_name() + "\n");
                }
                if (cb_ifsccode.isChecked()) {
                    sb.append("IFSC Code - " + resultArrayList.get(position).getIfsc_code() + "\n");
                }
                if (cb_accno.isChecked()) {
                    sb.append("Account Number - " + resultArrayList.get(position).getAccount_no() + "\n");
                }
                String url = "";
                if (cb_file.isChecked()) {
                    url = resultArrayList.get(position).getDocument();
                    url = url.replaceAll(" ", "%20");
                    sb.append("File Url - " + url + "\n");
                }

                if (!cb_name.isChecked() && !cb_bankname.isChecked() && !cb_ifsccode.isChecked()
                        && !cb_accno.isChecked() && !cb_file.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedBankDetails", sb.toString());
                String finalDataShare = name + " shares bank details with you " + "\n" + sb.toString();
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

        alertDialogBuilder.setCancelable(false);
        android.support.v7.app.AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public class DeleteBankDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("bank_id", resultArrayList.get(Integer.parseInt(params[0])).getBank_id());
            res = WebServiceCalls.APICall(ApplicationConstants.BANKAPI, obj.toString());
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
                        builder.setMessage("Bank Details Deleted Successfully");
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new My_Bank_Fragment.GetBankList().execute();
                                removeItem(position);
                            }
                        });
                        builder.show();
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
