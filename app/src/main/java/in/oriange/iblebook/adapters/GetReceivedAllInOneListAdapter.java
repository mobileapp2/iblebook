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
import in.oriange.iblebook.activities.Edit_AllInOne_Activity;
import in.oriange.iblebook.activities.View_AllInOne_Activity;
import in.oriange.iblebook.fragments.Received_AllInOne_Fragment;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class GetReceivedAllInOneListAdapter extends RecyclerView.Adapter<GetReceivedAllInOneListAdapter.MyViewHolder> {

    private static List<AllInOneModel> resultArrayList;
    private final UserSessionManager session;
    private Context context;
    private String name, STATUS;

    public GetReceivedAllInOneListAdapter(Context context, List<AllInOneModel> resultArrayList, String STATUS) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.STATUS = STATUS;
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
        View view = inflater.inflate(R.layout.list_row_allinone, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final AllInOneModel allInOneDetails = resultArrayList.get(position);

        holder.tv_name.setText(allInOneDetails.getName());
        holder.tv_alias.setText(allInOneDetails.getAlias());
        holder.tv_ifscaccno.setText(allInOneDetails.getAccount_number() + " | " + allInOneDetails.getIfsc_code());
        holder.tv_pangst.setText(allInOneDetails.getPan_number() + " | " + allInOneDetails.getGst_number());

        holder.fl_mainframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, View_AllInOne_Activity.class)
                        .putExtra("allInOneDetails", allInOneDetails)
                        .putExtra("STATUS", STATUS));
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
                                context.startActivity(new Intent(context, Edit_AllInOne_Activity.class)
                                        .putExtra("allInOneDetails", allInOneDetails)
                                        .putExtra("STATUS", STATUS));

                                break;
                            case R.id.menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Are you sure you want to delete this item?");
                                builder.setTitle("Alert");
                                builder.setIcon(R.drawable.ic_alert_red_24dp);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new DeleteAllInOneDetails().execute(String.valueOf(position));
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
                                builder1.setMessage("Where to swap this address?");
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
                                        Utilities.showMessageString(context, "Coming Soon");

                                    }
                                });
                                builder1.setNegativeButton("Offline", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        new MoveAddressDetails().execute(String.valueOf(position), "offline");
                                        Utilities.showMessageString(context, "Coming Soon");
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

        if (resultArrayList == null) {
            return 0;
        } else {
            return resultArrayList.size();
        }
    }

    private void setSelectionFilter(final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_shareallinone, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

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


        if (resultArrayList.get(position).getAddress_type().trim().equals("")) {
            cb_addresstype.setVisibility(View.GONE);
            cb_addresstype.setChecked(false);
        } else {
            cb_addresstype.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getName().trim().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getAddress_line_one().trim().equals("")) {
            cb_address.setVisibility(View.GONE);
            cb_address.setChecked(false);
        } else {
            cb_address.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getMobile_number().trim().equals("")) {
            cb_mobile.setVisibility(View.GONE);
            cb_mobile.setChecked(false);
        } else {
            cb_mobile.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getEmail_id().trim().equals("")) {
            cb_email.setVisibility(View.GONE);
            cb_email.setChecked(false);
        } else {
            cb_email.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getLandline_number().trim().equals("")) {
            cb_landline.setVisibility(View.GONE);
            cb_landline.setChecked(false);
        } else {
            cb_landline.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getContact_person_name().trim().equals("") &&
                resultArrayList.get(position).getContact_person_mobile().trim().equals("")) {
            cb_contactperson.setVisibility(View.GONE);
            cb_contactperson.setChecked(false);
        } else {
            cb_contactperson.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getWebsite().trim().equals("")) {
            cb_website.setVisibility(View.GONE);
            cb_website.setChecked(false);
        } else {
            cb_website.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getMap_location_longitude().trim().equals("")
                || resultArrayList.get(position).getMap_location_latitude().trim().equals("")) {
            cb_maplocation.setVisibility(View.GONE);
            cb_maplocation.setChecked(false);
        } else {
            cb_maplocation.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getVisiting_card().trim().equals("")) {
            cb_visitcard.setVisibility(View.GONE);
            cb_visitcard.setChecked(false);
        } else {
            cb_visitcard.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getPhoto().trim().equals("")) {
            cb_photo.setVisibility(View.GONE);
            cb_photo.setChecked(false);
        } else {
            cb_photo.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getAccount_holder_name().equals("")) {
            cb_accountholdername.setVisibility(View.GONE);
            cb_accountholdername.setChecked(false);
        } else {
            cb_accountholdername.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getBank_name().equals("")) {
            cb_bankname.setVisibility(View.GONE);
            cb_bankname.setChecked(false);
        } else {
            cb_bankname.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getIfsc_code().equals("")) {
            cb_ifsccode.setVisibility(View.GONE);
            cb_ifsccode.setChecked(false);
        } else {
            cb_ifsccode.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getAccount_number().equals("")) {
            cb_accno.setVisibility(View.GONE);
            cb_accno.setChecked(false);
        } else {
            cb_accno.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getBank_document().equals("")) {
            cb_bankfile.setVisibility(View.GONE);
            cb_bankfile.setChecked(false);
        } else {
            cb_bankfile.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getPan_number().equals("")) {
            cb_panno.setVisibility(View.GONE);
            cb_panno.setChecked(false);
        } else {
            cb_panno.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getPan_document().equals("")) {
            cb_panfile.setVisibility(View.GONE);
            cb_panfile.setChecked(false);
        } else {
            cb_panfile.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getGst_number().equals("")) {
            cb_gstno.setVisibility(View.GONE);
            cb_gstno.setChecked(false);
        } else {
            cb_gstno.setVisibility(View.VISIBLE);
        }

        if (resultArrayList.get(position).getGst_document().equals("")) {
            cb_gstfile.setVisibility(View.GONE);
            cb_gstfile.setChecked(false);
        } else {
            cb_gstfile.setVisibility(View.VISIBLE);
        }


        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();

                if (cb_addresstype.isChecked()) {
                    sb.append("Address Type - " + resultArrayList.get(position).getAddress_type() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name - " + resultArrayList.get(position).getName() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address - " + resultArrayList.get(position).getAddress_line_one() + ", " +
                            "Dist - " + resultArrayList.get(position).getDistrict() + ", " +
                            resultArrayList.get(position).getState() + ", " +
                            resultArrayList.get(position).getCountry() + ", " +
                            "Pin Code - " + resultArrayList.get(position).getPincode() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile No - " + resultArrayList.get(position).getMobile_number() + "\n");
                }

                if (cb_landline.isChecked()) {
                    sb.append("Landline - " + resultArrayList.get(position).getLandline_number() + "\n");
                }

                if (cb_contactperson.isChecked()) {
                    sb.append("Contact Person Details- " + resultArrayList.get(position).getContact_person_name() + ", " +
                            resultArrayList.get(position).getContact_person_mobile() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email - " + resultArrayList.get(position).getEmail_id() + "\n");
                }

                if (cb_website.isChecked()) {
                    sb.append("Website - " + resultArrayList.get(position).getWebsite() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + resultArrayList.get(position).getMap_location_latitude()
                            + "," + resultArrayList.get(position).getMap_location_longitude() + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getVisiting_card();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visiting Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getPhoto();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (cb_accountholdername.isChecked()) {
                    sb.append("Name - " + resultArrayList.get(position).getAccount_holder_name() + "\n");
                }

                if (cb_bankname.isChecked()) {
                    sb.append("Bank - " + resultArrayList.get(position).getBank_name() + "\n");
                }

                if (cb_ifsccode.isChecked()) {
                    sb.append("IFSC Code - " + resultArrayList.get(position).getIfsc_code() + "\n");
                }

                if (cb_accno.isChecked()) {
                    sb.append("A/C No. - " + resultArrayList.get(position).getAccount_number() + "\n");
                }

                if (cb_bankfile.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getBank_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Bank Document - " + url + "\n");
                }

                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + resultArrayList.get(position).getPan_number() + "\n");
                }

                if (cb_panfile.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getPan_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("PAN Document - " + url + "\n");
                }

                if (cb_gstno.isChecked()) {
                    sb.append("GST - " + resultArrayList.get(position).getGst_number() + "\n");
                }

                if (cb_gstfile.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getGst_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("GST Document - " + url + "\n");
                }

                if (sb.toString().isEmpty()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }


                Log.i("SharedAllInOneDetails", sb.toString());
                String finalDataShare = name + " shared details with you " + "\n\n" + sb.toString() + "\n" + "via Iblebook \n" + "Click Here - " + ApplicationConstants.IBLEBOOK_PLAYSTORELINK;
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

                if (cb_addresstype.isChecked()) {
                    sb.append("Address Type - " + resultArrayList.get(position).getAddress_type() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name - " + resultArrayList.get(position).getName() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address - " + resultArrayList.get(position).getAddress_line_one() + ", " +
                            "Dist - " + resultArrayList.get(position).getDistrict() + ", " +
                            resultArrayList.get(position).getState() + ", " +
                            resultArrayList.get(position).getCountry() + ", " +
                            "Pin Code - " + resultArrayList.get(position).getPincode() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile No - " + resultArrayList.get(position).getMobile_number() + "\n");
                }

                if (cb_landline.isChecked()) {
                    sb.append("Landline - " + resultArrayList.get(position).getLandline_number() + "\n");
                }

                if (cb_contactperson.isChecked()) {
                    sb.append("Contact Person Details- " + resultArrayList.get(position).getContact_person_name() + ", " +
                            resultArrayList.get(position).getContact_person_mobile() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email - " + resultArrayList.get(position).getEmail_id() + "\n");
                }

                if (cb_website.isChecked()) {
                    sb.append("Website - " + resultArrayList.get(position).getWebsite() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + resultArrayList.get(position).getMap_location_latitude()
                            + "," + resultArrayList.get(position).getMap_location_longitude() + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getVisiting_card();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visiting Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getPhoto();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (cb_accountholdername.isChecked()) {
                    sb.append("Name - " + resultArrayList.get(position).getAccount_holder_name() + "\n");
                }

                if (cb_bankname.isChecked()) {
                    sb.append("Bank - " + resultArrayList.get(position).getBank_name() + "\n");
                }

                if (cb_ifsccode.isChecked()) {
                    sb.append("IFSC Code - " + resultArrayList.get(position).getIfsc_code() + "\n");
                }

                if (cb_accno.isChecked()) {
                    sb.append("A/C No. - " + resultArrayList.get(position).getAccount_number() + "\n");
                }

                if (cb_bankfile.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getBank_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Bank Document - " + url + "\n");
                }

                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + resultArrayList.get(position).getPan_number() + "\n");
                }

                if (cb_panfile.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getPan_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("PAN Document - " + url + "\n");
                }

                if (cb_gstno.isChecked()) {
                    sb.append("GST - " + resultArrayList.get(position).getGst_number() + "\n");
                }

                if (cb_gstfile.isChecked()) {
                    String url = "";
                    url = resultArrayList.get(position).getGst_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("GST Document - " + url + "\n");
                }

                if (sb.toString().isEmpty()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }


                Log.i("SharedAllInOneDetails", sb.toString());
                String finalDataShare = "All in One Details" + "\n" + sb.toString();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", finalDataShare);
                clipboard.setPrimaryClip(clip);
                Utilities.showMessageString(context, "Copied to clipboard");
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

        TextView tv_name, tv_alias, tv_ifscaccno, tv_pangst;
        private FrameLayout fl_mainframe;
        private ImageView imv_more;

        public MyViewHolder(View view) {
            super(view);
            tv_alias = view.findViewById(R.id.tv_alias);
            tv_name = view.findViewById(R.id.tv_name);
            tv_ifscaccno = view.findViewById(R.id.tv_ifscaccno);
            tv_pangst = view.findViewById(R.id.tv_pangst);
            imv_more = view.findViewById(R.id.imv_more);
            fl_mainframe = view.findViewById(R.id.fl_mainframe);
        }
    }

    public class DeleteAllInOneDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("all_in_one_id", resultArrayList.get(Integer.parseInt(params[0])).getAll_in_one_id());
            res = WebServiceCalls.APICall(ApplicationConstants.ALLINONEAPI, obj.toString());
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
                        builder.setMessage("Details Deleted Successfully");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new Received_AllInOne_Fragment.GetAllInOneList().execute();
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
