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

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.Edit_Address_Activity;
import in.oriange.iblebook.activities.View_Address_Activity;
import in.oriange.iblebook.fragments.My_Address_Fragment;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetMyAddressListAdapter extends RecyclerView.Adapter<GetMyAddressListAdapter.MyViewHolder> {

    private static List<GetAddressListPojo> resultArrayList;
    private final UserSessionManager session;
    private Context context;
    private String name, STATUS;
    private DataBaseHelper dbHelper;

    public GetMyAddressListAdapter(Context context, List<GetAddressListPojo> resultArrayList, String STATUS) {
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
        View view = inflater.inflate(R.layout.list_row_address, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_initletter.setText(String.valueOf(resultArrayList.get(position).getName().charAt(0)));
        holder.tv_addresstype.setText(resultArrayList.get(position).getType());
        holder.tv_name.setText(resultArrayList.get(position).getName());

        holder.fl_mainframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, View_Address_Activity.class);
                intent.putExtra("position", position);
                intent.putExtra("address_id", resultArrayList.get(position).getAddress_id());
                intent.putExtra("type_id", resultArrayList.get(position).getType_id());
                intent.putExtra("name", resultArrayList.get(position).getName());
                intent.putExtra("alias", resultArrayList.get(position).getAlias());
                intent.putExtra("address_line_one", resultArrayList.get(position).getAddress_line_one());
                intent.putExtra("address_line_two", resultArrayList.get(position).getAddress_line_two());
                intent.putExtra("country", resultArrayList.get(position).getCountry());
                intent.putExtra("state", resultArrayList.get(position).getState());
                intent.putExtra("district", resultArrayList.get(position).getDistrict());
                intent.putExtra("pincode", resultArrayList.get(position).getPincode());
                intent.putExtra("email_id", resultArrayList.get(position).getEmail_id());
                intent.putExtra("website", resultArrayList.get(position).getWebsite());
                intent.putExtra("visiting_card", resultArrayList.get(position).getVisiting_card());
                intent.putExtra("map_location_logitude", resultArrayList.get(position).getMap_location_logitude());
                intent.putExtra("map_location_lattitude", resultArrayList.get(position).getMap_location_lattitude());
                intent.putExtra("photo", resultArrayList.get(position).getPhoto());
                intent.putExtra("status", resultArrayList.get(position).getStatus());
                intent.putExtra("created_by", resultArrayList.get(position).getCreated_by());
                intent.putExtra("updated_by", resultArrayList.get(position).getUpdated_by());
                intent.putExtra("type", resultArrayList.get(position).getType());
                intent.putExtra("mobile_number", resultArrayList.get(position).getMobile_number());
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
                                Intent intent = new Intent(context, Edit_Address_Activity.class);
                                intent.putExtra("address_id", resultArrayList.get(position).getAddress_id());
                                intent.putExtra("name", resultArrayList.get(position).getName());
                                intent.putExtra("alias", resultArrayList.get(position).getAlias());
                                intent.putExtra("address_line_one", resultArrayList.get(position).getAddress_line_one());
                                intent.putExtra("address_line_two", resultArrayList.get(position).getAddress_line_two());
                                intent.putExtra("country", resultArrayList.get(position).getCountry());
                                intent.putExtra("state", resultArrayList.get(position).getState());
                                intent.putExtra("district", resultArrayList.get(position).getDistrict());
                                intent.putExtra("pincode", resultArrayList.get(position).getPincode());
                                intent.putExtra("email_id", resultArrayList.get(position).getEmail_id());
                                intent.putExtra("website", resultArrayList.get(position).getWebsite());
                                intent.putExtra("visiting_card", resultArrayList.get(position).getVisiting_card());
                                intent.putExtra("map_location_lattitude", resultArrayList.get(position).getMap_location_lattitude());
                                intent.putExtra("map_location_logitude", resultArrayList.get(position).getMap_location_logitude());
                                intent.putExtra("photo", resultArrayList.get(position).getPhoto());
                                intent.putExtra("created_by", resultArrayList.get(position).getCreated_by());
                                intent.putExtra("type", resultArrayList.get(position).getType());
                                intent.putExtra("mobile_number", resultArrayList.get(position).getMobile_number());
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
                                        new DeleteAddressDetails().execute(String.valueOf(position));
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

    private void setSelectionFilter(final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_shareaddress, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        TextView tv_addresstype = promptView.findViewById(R.id.tv_addresstype);
        TextView tv_name = promptView.findViewById(R.id.tv_name);
        TextView tv_address = promptView.findViewById(R.id.tv_address);
        TextView tv_country = promptView.findViewById(R.id.tv_country);
        TextView tv_state = promptView.findViewById(R.id.tv_state);
        TextView tv_district = promptView.findViewById(R.id.tv_district);
        TextView tv_pincode = promptView.findViewById(R.id.tv_pincode);
        TextView tv_mobile = promptView.findViewById(R.id.tv_mobile);
        TextView tv_email = promptView.findViewById(R.id.tv_email);
        TextView tv_website = promptView.findViewById(R.id.tv_website);
        TextView tv_maplocation = promptView.findViewById(R.id.tv_maplocation);
        TextView tv_visitcard = promptView.findViewById(R.id.tv_visitcard);
        TextView tv_photo = promptView.findViewById(R.id.tv_photo);

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

        tv_addresstype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_addresstype.isChecked())
                    cb_addresstype.setChecked(true);
                else
                    cb_addresstype.setChecked(false);
            }
        });
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_name.isChecked())
                    cb_name.setChecked(true);
                else
                    cb_name.setChecked(false);
            }
        });
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_address.isChecked())
                    cb_address.setChecked(true);
                else
                    cb_address.setChecked(false);
            }
        });
        tv_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_country.isChecked())
                    cb_country.setChecked(true);
                else
                    cb_country.setChecked(false);
            }
        });
        tv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_state.isChecked())
                    cb_state.setChecked(true);
                else
                    cb_state.setChecked(false);
            }
        });
        tv_district.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_district.isChecked())
                    cb_district.setChecked(true);
                else
                    cb_district.setChecked(false);
            }
        });
        tv_pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_pincode.isChecked())
                    cb_pincode.setChecked(true);
                else
                    cb_pincode.setChecked(false);
            }
        });
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_mobile.isChecked())
                    cb_mobile.setChecked(true);
                else
                    cb_mobile.setChecked(false);
            }
        });
        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_email.isChecked())
                    cb_email.setChecked(true);
                else
                    cb_email.setChecked(false);
            }
        });
        tv_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_website.isChecked())
                    cb_website.setChecked(true);
                else
                    cb_website.setChecked(false);
            }
        });
        tv_maplocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_maplocation.isChecked())
                    cb_maplocation.setChecked(true);
                else
                    cb_maplocation.setChecked(false);
            }
        });
        tv_visitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_visitcard.isChecked())
                    cb_visitcard.setChecked(true);
                else
                    cb_visitcard.setChecked(false);
            }
        });
        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_photo.isChecked())
                    cb_photo.setChecked(true);
                else
                    cb_photo.setChecked(false);
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();

                if (cb_addresstype.isChecked()) {
                    sb.append("Address Type- " + resultArrayList.get(position).getType() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name- " + resultArrayList.get(position).getName() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address- " + resultArrayList.get(position).getAddress_line_one() + "\n");
                }

                if (cb_country.isChecked()) {
                    sb.append("Country- " + resultArrayList.get(position).getCountry() + "\n");
                }

                if (cb_state.isChecked()) {
                    sb.append("State- " + resultArrayList.get(position).getState() + "\n");
                }

                if (cb_district.isChecked()) {
                    sb.append("District- " + resultArrayList.get(position).getDistrict() + "\n");
                }

                if (cb_pincode.isChecked()) {
                    sb.append("Pin Code- " + resultArrayList.get(position).getPincode() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile Number- " + resultArrayList.get(position).getMobile_number() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email Address- " + resultArrayList.get(position).getEmail_id() + "\n");
                }

                if (cb_website.isChecked()) {
                    sb.append("Website- " + resultArrayList.get(position).getWebsite() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Map Location- " + resultArrayList.get(position).getMap_location_lattitude()
                            + ", " + resultArrayList.get(position).getMap_location_logitude() + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getVisiting_card();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visit Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getPhoto();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (!cb_addresstype.isChecked() && !cb_name.isChecked() && !cb_address.isChecked() && !cb_country.isChecked() &&
                        !cb_state.isChecked() && !cb_district.isChecked() && !cb_pincode.isChecked() && !cb_mobile.isChecked()
                        && !cb_email.isChecked() && !cb_website.isChecked() && !cb_maplocation.isChecked() && !cb_visitcard.isChecked()
                        && !cb_photo.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedAddressDetails", sb.toString());
                String finalDataShare = name + " shares Address details with you " + "\n" + sb.toString();
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
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void removeItem(int position) {
        resultArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_initletter, tv_addresstype, tv_name;
        private FrameLayout fl_mainframe;
        private ImageView imv_more;

        public MyViewHolder(View view) {
            super(view);
            tv_initletter = (TextView) view.findViewById(R.id.tv_initletter);
            tv_addresstype = (TextView) view.findViewById(R.id.tv_addresstype);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            imv_more = view.findViewById(R.id.imv_more);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

    public class DeleteAddressDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "delete");
            obj.addProperty("address_id", resultArrayList.get(Integer.parseInt(params[0])).getAddress_id());
            res = WebServiceCalls.APICall(ApplicationConstants.ADDRESSAPI, obj.toString());
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
                        builder.setMessage("Address Details Deleted Successfully");
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new My_Address_Fragment.GetAddressList().execute();
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
}
