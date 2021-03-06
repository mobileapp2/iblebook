package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.My_Address_Fragment;
import in.oriange.iblebook.fragments.Offline_Address_Fragment;
import in.oriange.iblebook.fragments.Received_Address_Fragment;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class View_Address_Activity extends Activity {
    private Context context;
    private ProgressDialog pd;
    private LinearLayout ll_parent, ll_mobilelayout;
    private EditText edt_addresstype, edt_name, edt_alias, edt_address, edt_country,
            edt_state, edt_district, edt_pincode, edt_mobile, edt_email, edt_landline, edt_contactperson, edt_contactpersonmobile, edt_website;
    private TextView edt_viewloc, edt_visitcard, edt_attachphoto;
    private String photo, visiting_card, address_id, map_location_lattitude, map_location_logitude, user_id, STATUS, name, type_id;
    private FloatingActionButton fab_share, fab_edit, fab_delete;
    private UserSessionManager session;

    private File downloadedDocsfolder, file;
    private List<String> mobileNoList;
    private List<LinearLayout> mobileDetailsLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);

        init();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
        setupToolbar();
    }

    private void init() {
        context = View_Address_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
        ll_parent = findViewById(R.id.ll_parent);
        ll_mobilelayout = findViewById(R.id.ll_mobilelayout);
        edt_addresstype = findViewById(R.id.edt_addresstype);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_address = findViewById(R.id.edt_address);
        edt_country = findViewById(R.id.edt_country);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_landline = findViewById(R.id.edt_landline);
        edt_contactperson = findViewById(R.id.edt_contactperson);
        edt_contactpersonmobile = findViewById(R.id.edt_contactpersonmobile);
        edt_website = findViewById(R.id.edt_website);
        edt_viewloc = findViewById(R.id.edt_viewloc);
        edt_attachphoto = findViewById(R.id.edt_attachphoto);
        edt_visitcard = findViewById(R.id.edt_visitcard);

        fab_share = findViewById(R.id.fab_share);
        fab_edit = findViewById(R.id.fab_edit);
        fab_delete = findViewById(R.id.fab_delete);

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "Address Documents");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        mobileNoList = new ArrayList<>();
        mobileDetailsLayouts = new ArrayList<>();
    }

    private void getSessionData() {
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

    private void getIntentData() {
        type_id = getIntent().getStringExtra("type_id");
        address_id = getIntent().getStringExtra("address_id");
        edt_addresstype.setText(getIntent().getStringExtra("type"));
        edt_name.setText(getIntent().getStringExtra("name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_address.setText(getIntent().getStringExtra("address_line_one"));
        edt_country.setText(getIntent().getStringExtra("country"));
        edt_state.setText(getIntent().getStringExtra("state"));
        edt_district.setText(getIntent().getStringExtra("district"));
        edt_pincode.setText(getIntent().getStringExtra("pincode"));
        edt_email.setText(getIntent().getStringExtra("email_id"));
        edt_website.setText(getIntent().getStringExtra("website"));
        edt_landline.setText(getIntent().getStringExtra("landline_number"));
        edt_contactperson.setText(getIntent().getStringExtra("contact_person_name"));
        edt_contactpersonmobile.setText(getIntent().getStringExtra("contact_person_mobile"));

        mobileNoList = Arrays.asList(getIntent().getStringExtra("mobile_number").split("\\s*,\\s*"));


        edt_mobile.setText(mobileNoList.get(0));

//        if (mobileNoList.size() > 1) {
//            for (int i = 1; i < mobileNoList.size(); i++) {
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View rowView = inflater.inflate(R.layout.view_mobile, null);
//                mobileDetailsLayouts.add((LinearLayout) rowView);
//                ll_mobilelayout.addView(rowView, ll_mobilelayout.getChildCount());
//
//                ((EditText) mobileDetailsLayouts.get(i - 1).findViewById(R.id.edt_mobile)).setText(mobileNoList.get(i));
//            }
//        }

        visiting_card = getIntent().getStringExtra("visiting_card");
        map_location_lattitude = getIntent().getStringExtra("map_location_lattitude");
        map_location_logitude = getIntent().getStringExtra("map_location_logitude");
        photo = getIntent().getStringExtra("photo");
        user_id = getIntent().getStringExtra("created_by");
        STATUS = getIntent().getStringExtra("STATUS");

    }

    private void setDefaults() {
        if (map_location_lattitude.equals("") && map_location_logitude.equals("")) {
            edt_viewloc.setText("Location Not Found");
            edt_viewloc.setFocusable(false);
            edt_viewloc.setClickable(false);
        }

        if (visiting_card.equals("")) {
            edt_visitcard.setText("No Visit Card Attached");
            edt_visitcard.setFocusable(false);
            edt_visitcard.setClickable(false);
        }

        if (photo.equals("")) {
            edt_attachphoto.setText("No Photo Attached");
            edt_attachphoto.setFocusable(false);
            edt_attachphoto.setClickable(false);
        }
    }

    private void setEventHandler() {
//        tv_website.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tv_website.getText().toString().trim()));
//                startActivity(browserIntent);
//            }
//        });
//
//        tv_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
//                        Uri.parse("mailto:" + tv_email.getText().toString()));
//                startActivity(emailIntent);
//            }
//        });
        edt_addresstype.setLongClickable(false);
        edt_name.setLongClickable(false);
        edt_alias.setLongClickable(false);
        edt_address.setLongClickable(false);
        edt_country.setLongClickable(false);
        edt_state.setLongClickable(false);
        edt_district.setLongClickable(false);
        edt_pincode.setLongClickable(false);
        edt_mobile.setLongClickable(false);
        edt_email.setLongClickable(false);
        edt_website.setLongClickable(false);

        edt_viewloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!map_location_lattitude.equals("") && !map_location_logitude.equals("")) {
                    Intent intent = new Intent(context, View_Location_Map_Activity.class);
                    intent.putExtra("map_location_lattitude", map_location_lattitude);
                    intent.putExtra("map_location_logitude", map_location_logitude);
                    startActivity(intent);
                }

            }
        });

        edt_visitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visiting_card.equals("")) {
                    new DownloadDocument().execute(visiting_card);
                }

            }
        });
        edt_attachphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photo.equals("")) {
                    new DownloadDocument().execute(photo);
                }
            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectionFilter();
            }
        });

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(context, Edit_Address_Activity.class);
                intent.putExtra("type_id", type_id);
                intent.putExtra("address_id", address_id);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("alias", getIntent().getStringExtra("alias"));
                intent.putExtra("address_line_one", getIntent().getStringExtra("address_line_one"));
                intent.putExtra("country", getIntent().getStringExtra("country"));
                intent.putExtra("state", getIntent().getStringExtra("state"));
                intent.putExtra("district", getIntent().getStringExtra("district"));
                intent.putExtra("pincode", getIntent().getStringExtra("pincode"));
                intent.putExtra("email_id", getIntent().getStringExtra("email_id"));
                intent.putExtra("website", getIntent().getStringExtra("website"));
                intent.putExtra("visiting_card", visiting_card);
                intent.putExtra("map_location_logitude", map_location_logitude);
                intent.putExtra("map_location_lattitude", map_location_lattitude);
                intent.putExtra("photo", photo);
                intent.putExtra("created_by", user_id);
                intent.putExtra("type", getIntent().getStringExtra("type"));
                intent.putExtra("mobile_number", getIntent().getStringExtra("mobile_number"));
                intent.putExtra("landline_number", getIntent().getStringExtra("landline_number"));
                intent.putExtra("contact_person_name", getIntent().getStringExtra("contact_person_name"));
                intent.putExtra("contact_person_mobile", getIntent().getStringExtra("contact_person_mobile"));
                intent.putExtra("STATUS", STATUS);
                context.startActivity(intent);
            }
        });

        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteAddressDetails().execute();
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
            }
        });
    }

    private void setSelectionFilter() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_shareaddress, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        final CheckBox cb_addresstype = promptView.findViewById(R.id.cb_addresstype);
        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_address = promptView.findViewById(R.id.cb_address);
        final CheckBox cb_mobile = promptView.findViewById(R.id.cb_mobile);
        final CheckBox cb_email = promptView.findViewById(R.id.cb_email);
        final CheckBox cb_landline = promptView.findViewById(R.id.cb_landline);
        final CheckBox cb_contactperson = promptView.findViewById(R.id.cb_contactperson);
        final CheckBox cb_website = promptView.findViewById(R.id.cb_website);
        final CheckBox cb_maplocation = promptView.findViewById(R.id.cb_maplocation);
        final CheckBox cb_visitcard = promptView.findViewById(R.id.cb_visitcard);
        final CheckBox cb_photo = promptView.findViewById(R.id.cb_photo);


        if (edt_addresstype.getText().toString().trim().equals("")) {
            cb_addresstype.setVisibility(View.GONE);
            cb_addresstype.setChecked(false);
        } else {
            cb_addresstype.setVisibility(View.VISIBLE);
        }

        if (edt_name.getText().toString().trim().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (edt_address.getText().toString().trim().equals("")) {
            cb_address.setVisibility(View.GONE);
            cb_address.setChecked(false);
        } else {
            cb_address.setVisibility(View.VISIBLE);
        }

        if (edt_mobile.getText().toString().trim().equals("")) {
            cb_mobile.setVisibility(View.GONE);
            cb_mobile.setChecked(false);
        } else {
            cb_mobile.setVisibility(View.VISIBLE);
        }

        if (edt_email.getText().toString().trim().equals("")) {
            cb_email.setVisibility(View.GONE);
            cb_email.setChecked(false);
        } else {
            cb_email.setVisibility(View.VISIBLE);
        }

        if (edt_landline.getText().toString().trim().equals("")) {
            cb_landline.setVisibility(View.GONE);
            cb_landline.setChecked(false);
        } else {
            cb_landline.setVisibility(View.VISIBLE);
        }

        if (edt_contactperson.getText().toString().trim().equals("") &&
                edt_contactpersonmobile.getText().toString().trim().equals("")) {
            cb_contactperson.setVisibility(View.GONE);
            cb_contactperson.setChecked(false);
        } else {
            cb_contactperson.setVisibility(View.VISIBLE);
        }

        if (edt_website.getText().toString().trim().equals("")) {
            cb_website.setVisibility(View.GONE);
            cb_website.setChecked(false);
        } else {
            cb_website.setVisibility(View.VISIBLE);
        }

        if (map_location_lattitude.equals("") && map_location_logitude.equals("")) {
            cb_maplocation.setVisibility(View.GONE);
            cb_maplocation.setChecked(false);
        } else {
            cb_maplocation.setVisibility(View.VISIBLE);
        }

        if (visiting_card.equals("")) {
            cb_visitcard.setVisibility(View.GONE);
            cb_visitcard.setChecked(false);
        } else {
            cb_visitcard.setVisibility(View.VISIBLE);
        }

        if (photo.equals("")) {
            cb_photo.setVisibility(View.GONE);
            cb_photo.setChecked(false);
        } else {
            cb_photo.setVisibility(View.VISIBLE);
        }

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();

                if (cb_addresstype.isChecked()) {
                    sb.append("Address Type - " + edt_addresstype.getText().toString().trim() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name - " + edt_name.getText().toString().trim() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address - " + edt_address.getText().toString().trim() + ", " +
                            edt_district.getText().toString().trim() + ", " +
                            edt_state.getText().toString().trim() + ", " +
                            edt_country.getText().toString().trim() + ", " +
                            edt_pincode.getText().toString().trim() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile No - " + edt_mobile.getText().toString().trim() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email - " + edt_email.getText().toString().trim() + "\n");
                }

                if (cb_landline.isChecked()) {
                    sb.append("Landline - " + edt_landline.getText().toString().trim() + "\n");
                }

                if (cb_contactperson.isChecked()) {
                    sb.append("Contact Person Details- " + edt_contactperson.getText().toString().trim() + ", " +
                            edt_contactpersonmobile.getText().toString().trim() + "\n");
                }
                if (cb_website.isChecked()) {
                    sb.append("Website - " + edt_website.getText().toString().trim() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + getIntent().getStringExtra("map_location_lattitude")
                            + "," + getIntent().getStringExtra("map_location_logitude") + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = visiting_card;
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visiting Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = photo;
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (!cb_addresstype.isChecked() && !cb_name.isChecked() && !cb_address.isChecked() && !cb_mobile.isChecked()
                        && !cb_email.isChecked() && !cb_website.isChecked() && !cb_maplocation.isChecked() && !cb_visitcard.isChecked()
                        && !cb_photo.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedAddressDetails", sb.toString());
                String finalDataShare = name + " shared address details with you " + "\n\n" + sb.toString() + "\n" + "via Iblebook \n" + "Click Here - " + ApplicationConstants.IBLEBOOK_PLAYSTORELINK;
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
                    sb.append("Address Type - " + edt_addresstype.getText().toString().trim() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name - " + edt_name.getText().toString().trim() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address - " + edt_address.getText().toString().trim() + ", " +
                            "Dist - " + edt_district.getText().toString().trim() + ", " +
                            edt_state.getText().toString().trim() + ", " +
                            edt_country.getText().toString().trim() + ", " +
                            "Pin Code - " + edt_pincode.getText().toString().trim() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile No - " + edt_mobile.getText().toString().trim() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email - " + edt_email.getText().toString().trim() + "\n");
                }

                if (cb_landline.isChecked()) {
                    sb.append("Landline - " + edt_landline.getText().toString().trim() + "\n");
                }

                if (cb_contactperson.isChecked()) {
                    sb.append("Contact Person Details- " + edt_contactperson.getText().toString().trim() + ", " +
                            edt_contactpersonmobile.getText().toString().trim() + "\n");
                }

                if (cb_website.isChecked()) {
                    sb.append("Website - " + edt_website.getText().toString().trim() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Location - " + "http://maps.google.com/maps?q=loc:"
                            + getIntent().getStringExtra("map_location_lattitude")
                            + "," + getIntent().getStringExtra("map_location_logitude") + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = visiting_card;
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visiting Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = photo;
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (!cb_addresstype.isChecked() && !cb_name.isChecked() && !cb_address.isChecked() && !cb_mobile.isChecked()
                        && !cb_email.isChecked() && !cb_website.isChecked() && !cb_maplocation.isChecked() && !cb_visitcard.isChecked()
                        && !cb_photo.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedAddressDetails", sb.toString());
                String finalDataShare = "Address details" + "\n" + sb.toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", finalDataShare);
                clipboard.setPrimaryClip(clip);
                Utilities.showMessageString(context, "Copied to clipboard");
            }
        });

        alertDialogBuilder.setCancelable(false);
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Address Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("Downloading Document");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedDocsfolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            mProgressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file);
                if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (downloadurl.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (downloadurl.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (downloadurl.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (downloadurl.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }

    public class DeleteAddressDetails extends AsyncTask<String, Void, String> {
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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "delete");
            obj.addProperty("address_id", address_id);
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

                        new My_Address_Fragment.GetAddressList().execute();
                        new Offline_Address_Fragment.GetAddressList().execute();
                        new Received_Address_Fragment.GetAddressList().execute();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Address Details Deleted Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
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
