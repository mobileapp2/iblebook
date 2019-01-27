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
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import in.oriange.iblebook.fragments.My_AllInOne_Fragment;
import in.oriange.iblebook.fragments.Offline_AllInOne_Fragment;
import in.oriange.iblebook.fragments.Received_AllInOne_Fragment;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class View_AllInOne_Activity extends Activity {
    private Context context;
    private ProgressDialog pd;
    private UserSessionManager session;

    private LinearLayout ll_parent, ll_mobilelayout;
    private TextView tv_viewloc, tv_visitcard, tv_attachphoto, tv_bankfile, tv_panfile, tv_gstfile;
    private EditText edt_addresstype, edt_name, edt_alias, edt_address, edt_country, edt_state, edt_district, edt_pincode, edt_mobile, edt_email,
            edt_landline, edt_contactperson, edt_contactpersonmobile, edt_website, edt_bankname, edt_bankalias, edt_bank_name, edt_ifsc, edt_account_no,
            edt_panno, edt_gstno;
    private FloatingActionButton fab_share, fab_edit, fab_delete;

    private String photo, visiting_card, bank_document, pan_document, gst_documant, allinone_id, map_location_lattitude, map_location_logitude,
            name, user_id, STATUS;

    private File downloadedDocsfolder, file;
    private AllInOneModel allInOneDetails;
    private List<String> mobileNoList;
    private List<LinearLayout> mobileDetailsLayouts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_allinone);

        init();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
        setupToolbar();
    }

    private void init() {
        context = View_AllInOne_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);

        ll_parent = findViewById(R.id.ll_parent);
        ll_mobilelayout = findViewById(R.id.ll_mobilelayout);

        tv_viewloc = findViewById(R.id.tv_viewloc);
        tv_visitcard = findViewById(R.id.tv_visitcard);
        tv_attachphoto = findViewById(R.id.tv_attachphoto);
        tv_bankfile = findViewById(R.id.tv_bankfile);
        tv_panfile = findViewById(R.id.tv_panfile);
        tv_gstfile = findViewById(R.id.tv_gstfile);

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
        edt_bankname = findViewById(R.id.edt_bankname);
        edt_bankalias = findViewById(R.id.edt_bankalias);
        edt_bank_name = findViewById(R.id.edt_bank_name);
        edt_ifsc = findViewById(R.id.edt_ifsc);
        edt_account_no = findViewById(R.id.edt_account_no);
        edt_panno = findViewById(R.id.edt_panno);
        edt_gstno = findViewById(R.id.edt_gstno);

        fab_share = findViewById(R.id.fab_share);
        fab_edit = findViewById(R.id.fab_edit);
        fab_delete = findViewById(R.id.fab_delete);

        allInOneDetails = new AllInOneModel();

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "All In One Documents");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        mobileDetailsLayouts = new ArrayList<>();
        mobileNoList = new ArrayList<>();
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
        allInOneDetails = (AllInOneModel) getIntent().getSerializableExtra("allInOneDetails");

        STATUS = getIntent().getStringExtra("STATUS");

    }

    private void setDefaults() {
        photo = allInOneDetails.getPhoto();
        visiting_card = allInOneDetails.getVisiting_card();
        bank_document = allInOneDetails.getBank_document();
        pan_document = allInOneDetails.getPan_document();
        gst_documant = allInOneDetails.getGst_document();
        allinone_id = allInOneDetails.getAll_in_one_id();
        map_location_lattitude = allInOneDetails.getMap_location_latitude();
        map_location_logitude = allInOneDetails.getMap_location_longitude();
        mobileNoList = Arrays.asList(allInOneDetails.getMobile_number().split("\\s*,\\s*"));

        edt_addresstype.setText(allInOneDetails.getAddress_type());
        edt_name.setText(allInOneDetails.getName());
        edt_alias.setText(allInOneDetails.getAlias());
        edt_address.setText(allInOneDetails.getAddress_line_one());
        edt_country.setText(allInOneDetails.getCountry());
        edt_state.setText(allInOneDetails.getState());
        edt_district.setText(allInOneDetails.getDistrict());
        edt_pincode.setText(allInOneDetails.getPincode());
        edt_email.setText(allInOneDetails.getEmail_id());
        edt_landline.setText(allInOneDetails.getLandline_number());
        edt_contactperson.setText(allInOneDetails.getContact_person_name());
        edt_contactpersonmobile.setText(allInOneDetails.getContact_person_mobile());
        edt_website.setText(allInOneDetails.getWebsite());
        edt_bankname.setText(allInOneDetails.getAccount_holder_name());
        edt_bankalias.setText(allInOneDetails.getAccount_holder_alias());
        edt_bank_name.setText(allInOneDetails.getBank_name());
        edt_ifsc.setText(allInOneDetails.getIfsc_code());
        edt_account_no.setText(allInOneDetails.getAccount_number());
        edt_panno.setText(allInOneDetails.getPan_number());
        edt_gstno.setText(allInOneDetails.getGst_number());


        if (map_location_lattitude.equals("") && map_location_logitude.equals("")) {
            tv_viewloc.setText("Location Not Found");
            tv_viewloc.setFocusable(false);
            tv_viewloc.setClickable(false);
        }

        if (visiting_card.equals("")) {
            tv_visitcard.setText("No Visit Card Attached");
            tv_visitcard.setFocusable(false);
            tv_visitcard.setClickable(false);
        }

        if (photo.equals("")) {
            tv_attachphoto.setText("No Photo Attached");
            tv_attachphoto.setFocusable(false);
            tv_attachphoto.setClickable(false);
        }

        if (bank_document.equals("")) {
            tv_bankfile.setText("No Document Attached");
            tv_bankfile.setFocusable(false);
            tv_bankfile.setClickable(false);
        }

        if (pan_document.equals("")) {
            tv_panfile.setText("No Document Attached");
            tv_panfile.setFocusable(false);
            tv_panfile.setClickable(false);
        }

        if (gst_documant.equals("")) {
            tv_gstfile.setText("No Document Attached");
            tv_gstfile.setFocusable(false);
            tv_gstfile.setClickable(false);
        }


        edt_mobile.setText(mobileNoList.get(0));

        if (mobileNoList.size() > 1) {
            for (int i = 1; i < mobileNoList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.view_mobile, null);
                mobileDetailsLayouts.add((LinearLayout) rowView);
                ll_mobilelayout.addView(rowView, ll_mobilelayout.getChildCount());

                ((EditText) mobileDetailsLayouts.get(i - 1).findViewById(R.id.edt_mobile)).setText(mobileNoList.get(i));
            }
        }

    }

    private void setEventHandler() {

        tv_viewloc.setOnClickListener(new View.OnClickListener() {
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

        tv_visitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visiting_card.equals("")) {
                    new DownloadDocument().execute(visiting_card);
                }

            }
        });

        tv_attachphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photo.equals("")) {
                    new DownloadDocument().execute(photo);
                }
            }
        });

        tv_bankfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photo.equals("")) {
                    new DownloadDocument().execute(bank_document);
                }
            }
        });

        tv_panfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photo.equals("")) {
                    new DownloadDocument().execute(pan_document);
                }
            }
        });

        tv_gstfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photo.equals("")) {
                    new DownloadDocument().execute(gst_documant);
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
                context.startActivity(new Intent(context, Edit_AllInOne_Activity.class)
                        .putExtra("allInOneDetails", allInOneDetails)
                        .putExtra("STATUS", STATUS));
                finish();
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
                        new DeleteAllInOneDetails().execute();
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

        final CheckBox cb_bank_name = promptView.findViewById(R.id.cb_bank_name);
        final CheckBox cb_bankname = promptView.findViewById(R.id.cb_bankname);
        final CheckBox cb_ifsccode = promptView.findViewById(R.id.cb_ifsccode);
        final CheckBox cb_accno = promptView.findViewById(R.id.cb_accno);
        final CheckBox cb_bankfile = promptView.findViewById(R.id.cb_bankfile);

        final CheckBox cb_panno = promptView.findViewById(R.id.cb_panno);
        final CheckBox cb_panfile = promptView.findViewById(R.id.cb_panfile);
        final CheckBox cb_gstno = promptView.findViewById(R.id.cb_gstno);
        final CheckBox cb_gstfile = promptView.findViewById(R.id.cb_gstfile);


        if (allInOneDetails.getAddress_type().trim().equals("")) {
            cb_addresstype.setVisibility(View.GONE);
            cb_addresstype.setChecked(false);
        } else {
            cb_addresstype.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getName().trim().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getAddress_line_one().trim().equals("")) {
            cb_address.setVisibility(View.GONE);
            cb_address.setChecked(false);
        } else {
            cb_address.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getMobile_number().trim().equals("")) {
            cb_mobile.setVisibility(View.GONE);
            cb_mobile.setChecked(false);
        } else {
            cb_mobile.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getLandline_number().trim().equals("")) {
            cb_landline.setVisibility(View.GONE);
            cb_landline.setChecked(false);
        } else {
            cb_landline.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getContact_person_name().trim().equals("") &&
                allInOneDetails.getContact_person_mobile().trim().equals("")) {
            cb_contactperson.setVisibility(View.GONE);
            cb_contactperson.setChecked(false);
        } else {
            cb_contactperson.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getEmail_id().trim().equals("")) {
            cb_email.setVisibility(View.GONE);
            cb_email.setChecked(false);
        } else {
            cb_email.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getWebsite().trim().equals("")) {
            cb_website.setVisibility(View.GONE);
            cb_website.setChecked(false);
        } else {
            cb_website.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getMap_location_longitude().trim().equals("")
                || allInOneDetails.getMap_location_latitude().trim().equals("")) {
            cb_maplocation.setVisibility(View.GONE);
            cb_maplocation.setChecked(false);
        } else {
            cb_maplocation.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getVisiting_card().trim().equals("")) {
            cb_visitcard.setVisibility(View.GONE);
            cb_visitcard.setChecked(false);
        } else {
            cb_visitcard.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getPhoto().trim().equals("")) {
            cb_photo.setVisibility(View.GONE);
            cb_photo.setChecked(false);
        } else {
            cb_photo.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getAccount_holder_name().equals("")) {
            cb_bank_name.setVisibility(View.GONE);
            cb_bank_name.setChecked(false);
        } else {
            cb_bank_name.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getBank_name().equals("")) {
            cb_bankname.setVisibility(View.GONE);
            cb_bankname.setChecked(false);
        } else {
            cb_bankname.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getIfsc_code().equals("")) {
            cb_ifsccode.setVisibility(View.GONE);
            cb_ifsccode.setChecked(false);
        } else {
            cb_ifsccode.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getAccount_number().equals("")) {
            cb_accno.setVisibility(View.GONE);
            cb_accno.setChecked(false);
        } else {
            cb_accno.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getBank_document().equals("")) {
            cb_bankfile.setVisibility(View.GONE);
            cb_bankfile.setChecked(false);
        } else {
            cb_bankfile.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getPan_number().equals("")) {
            cb_panno.setVisibility(View.GONE);
            cb_panno.setChecked(false);
        } else {
            cb_panno.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getPan_document().equals("")) {
            cb_panfile.setVisibility(View.GONE);
            cb_panfile.setChecked(false);
        } else {
            cb_panfile.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getGst_number().equals("")) {
            cb_gstno.setVisibility(View.GONE);
            cb_gstno.setChecked(false);
        } else {
            cb_gstno.setVisibility(View.VISIBLE);
        }

        if (allInOneDetails.getGst_document().equals("")) {
            cb_gstfile.setVisibility(View.GONE);
            cb_gstfile.setChecked(false);
        } else {
            cb_gstfile.setVisibility(View.VISIBLE);
        }


        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();

                if (cb_addresstype.isChecked()) {
                    sb.append("Address Type - " + allInOneDetails.getAddress_type() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name - " + allInOneDetails.getName() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address - " + allInOneDetails.getAddress_line_one() + ", " +
                            "Dist - " + allInOneDetails.getDistrict() + ", " +
                            allInOneDetails.getState() + ", " +
                            allInOneDetails.getCountry() + ", " +
                            "Pin Code - " + allInOneDetails.getPincode() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile No - " + allInOneDetails.getMobile_number() + "\n");
                }

                if (cb_landline.isChecked()) {
                    sb.append("Landline - " + allInOneDetails.getLandline_number() + "\n");
                }

                if (cb_contactperson.isChecked()) {
                    sb.append("Contact Person Details- " + allInOneDetails.getContact_person_name() + ", " +
                            allInOneDetails.getContact_person_mobile() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email - " + allInOneDetails.getEmail_id() + "\n");
                }

                if (cb_website.isChecked()) {
                    sb.append("Website - " + allInOneDetails.getWebsite() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + allInOneDetails.getMap_location_latitude()
                            + "," + allInOneDetails.getMap_location_longitude() + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getVisiting_card();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visiting Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getPhoto();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (cb_bank_name.isChecked()) {
                    sb.append("Name - " + allInOneDetails.getAccount_holder_name() + "\n");
                }

                if (cb_bankname.isChecked()) {
                    sb.append("Bank - " + allInOneDetails.getBank_name() + "\n");
                }

                if (cb_ifsccode.isChecked()) {
                    sb.append("IFSC Code - " + allInOneDetails.getIfsc_code() + "\n");
                }

                if (cb_accno.isChecked()) {
                    sb.append("A/C No. - " + allInOneDetails.getAccount_number() + "\n");
                }

                if (cb_bankfile.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getBank_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Bank Document - " + url + "\n");
                }

                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + allInOneDetails.getPan_number() + "\n");
                }

                if (cb_panfile.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getPan_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("PAN Document - " + url + "\n");
                }

                if (cb_gstno.isChecked()) {
                    sb.append("GST - " + allInOneDetails.getGst_number() + "\n");
                }

                if (cb_gstfile.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getGst_document();
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
                startActivity(Intent.createChooser(sharingIntent, "Choose from following"));


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
                    sb.append("Address Type - " + allInOneDetails.getAddress_type() + "\n");
                }

                if (cb_name.isChecked()) {
                    sb.append("Name - " + allInOneDetails.getName() + "\n");
                }

                if (cb_address.isChecked()) {
                    sb.append("Address - " + allInOneDetails.getAddress_line_one() + ", " +
                            "Dist - " + allInOneDetails.getDistrict() + ", " +
                            allInOneDetails.getState() + ", " +
                            allInOneDetails.getCountry() + ", " +
                            "Pin Code - " + allInOneDetails.getPincode() + "\n");
                }

                if (cb_mobile.isChecked()) {
                    sb.append("Mobile No - " + allInOneDetails.getMobile_number() + "\n");
                }

                if (cb_landline.isChecked()) {
                    sb.append("Landline - " + allInOneDetails.getLandline_number() + "\n");
                }

                if (cb_contactperson.isChecked()) {
                    sb.append("Contact Person Details- " + allInOneDetails.getContact_person_name() + ", " +
                            allInOneDetails.getContact_person_mobile() + "\n");
                }

                if (cb_email.isChecked()) {
                    sb.append("Email - " + allInOneDetails.getEmail_id() + "\n");
                }

                if (cb_website.isChecked()) {
                    sb.append("Website - " + allInOneDetails.getWebsite() + "\n");
                }

                if (cb_maplocation.isChecked()) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + allInOneDetails.getMap_location_latitude()
                            + "," + allInOneDetails.getMap_location_longitude() + "\n");
                }

                if (cb_visitcard.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getVisiting_card();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Visiting Card - " + url + "\n");
                }

                if (cb_photo.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getPhoto();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Photo - " + url + "\n");
                }

                if (cb_bank_name.isChecked()) {
                    sb.append("Name - " + allInOneDetails.getAccount_holder_name() + "\n");
                }

                if (cb_bankname.isChecked()) {
                    sb.append("Bank - " + allInOneDetails.getBank_name() + "\n");
                }

                if (cb_ifsccode.isChecked()) {
                    sb.append("IFSC Code - " + allInOneDetails.getIfsc_code() + "\n");
                }

                if (cb_accno.isChecked()) {
                    sb.append("A/C No. - " + allInOneDetails.getAccount_number() + "\n");
                }

                if (cb_bankfile.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getBank_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("Bank Document - " + url + "\n");
                }

                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + allInOneDetails.getPan_number() + "\n");
                }

                if (cb_panfile.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getPan_document();
                    url = url.replaceAll(" ", "%20");
                    sb.append("PAN Document - " + url + "\n");
                }

                if (cb_gstno.isChecked()) {
                    sb.append("GST - " + allInOneDetails.getGst_number() + "\n");
                }

                if (cb_gstfile.isChecked()) {
                    String url = "";
                    url = allInOneDetails.getGst_document();
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

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("View All In One Details");
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
            obj.addProperty("all_in_one_id", allinone_id);
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
                                new My_AllInOne_Fragment.GetAllInOneList().execute();
                                new Offline_AllInOne_Fragment.GetAllInOneList().execute();
                                new Received_AllInOne_Fragment.GetAllInOneList().execute();
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
