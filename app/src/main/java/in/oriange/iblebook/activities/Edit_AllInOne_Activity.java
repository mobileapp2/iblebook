package in.oriange.iblebook.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.My_AllInOne_Fragment;
import in.oriange.iblebook.fragments.Offline_AllInOne_Fragment;
import in.oriange.iblebook.models.AddressTypePojo;
import in.oriange.iblebook.models.AllInOneModel;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.MultipartUtility;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class Edit_AllInOne_Activity extends Activity {

    public final int CAMERA_REQUEST = 100;
    public final int GALLERY_REQUEST = 200;
    private Context context;
    private ProgressDialog pd;
    private LinearLayout ll_parent, ll_mobilelayout;
    private TextView tv_addresstype, tv_pickloc, tv_visitcard, tv_attachphoto, tv_bankattachfile, tv_panattachfile, tv_gstattachfile;
    private EditText edt_name, edt_alias, edt_address, edt_country, edt_state, edt_district, edt_pincode, edt_mobile1, edt_email,
            edt_landline, edt_contactperson, edt_contactpersonmobile, edt_website, edt_bankname, edt_bankalias, edt_bank_name,
            edt_ifsc, edt_account_no, edt_pan_no, edt_gst_no;
    private Button btn_save;
    private UserSessionManager session;
    private ImageView imv_add_mobno;

    private List<AddressTypePojo> addressTypeList;
    private File file, allInOneDocFolder;

    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private String photo, visiting_card, bank_document, pan_document, gst_documant, allinone_id, map_location_lattitude, map_location_logitude,
            name, user_id, STATUS, type_id, photoUrl;
    private AllInOneModel allInOneDetails;
    private Uri photoURI;
    private int j = 0;
    private List<LinearLayout> mobileDetailsLayouts;
    private List<String> mobileNoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_allinone);

        init();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
        setupToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Edit_AllInOne_Activity.this);
    }

    private void init() {
        context = Edit_AllInOne_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);

        ll_parent = findViewById(R.id.ll_parent);
        ll_mobilelayout = findViewById(R.id.ll_mobilelayout);

        tv_addresstype = findViewById(R.id.tv_addresstype);
        tv_pickloc = findViewById(R.id.tv_pickloc);
        tv_visitcard = findViewById(R.id.tv_visitcard);
        tv_attachphoto = findViewById(R.id.tv_attachphoto);
        tv_bankattachfile = findViewById(R.id.tv_bankattachfile);
        tv_panattachfile = findViewById(R.id.tv_panattachfile);
        tv_gstattachfile = findViewById(R.id.tv_gstattachfile);
        imv_add_mobno = findViewById(R.id.imv_add_mobno);

        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_address = findViewById(R.id.edt_address);
        edt_country = findViewById(R.id.edt_country);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_mobile1 = findViewById(R.id.edt_mobile1);
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
        edt_pan_no = findViewById(R.id.edt_pan_no);
        edt_gst_no = findViewById(R.id.edt_gst_no);

        btn_save = findViewById(R.id.btn_save);

        allInOneDocFolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "All In One Documents");
        if (!allInOneDocFolder.exists())
            allInOneDocFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        allInOneDetails = new AllInOneModel();
        addressTypeList = new ArrayList<AddressTypePojo>();
        mobileDetailsLayouts = new ArrayList<>();
        mobileNoList = new ArrayList<>();

    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
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
        type_id = allInOneDetails.getAddress_type_id();

        tv_addresstype.setText(allInOneDetails.getAddress_type());
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
        edt_pan_no.setText(allInOneDetails.getPan_number());
        edt_gst_no.setText(allInOneDetails.getGst_number());


        if (!map_location_lattitude.isEmpty() || !map_location_logitude.isEmpty()) {
            tv_pickloc.setText(map_location_lattitude + " , " + map_location_logitude);
        }

        if (visiting_card.equals("")) {

        } else {
            tv_visitcard.setText("Replace Visit Card Attached");
        }

        if (photo.equals("")) {

        } else {
            tv_attachphoto.setText("Replace Photo");
        }

        if (bank_document.equals("")) {

        } else {
            tv_bankattachfile.setText("Replace Bank Document");
        }

        if (pan_document.equals("")) {

        } else {
            tv_panattachfile.setText("Replace PAN Document");
        }

        if (gst_documant.equals("")) {

        } else {
            tv_gstattachfile.setText("Replace GST Document");
        }


        edt_mobile1.setText(mobileNoList.get(0));

//        if (mobileNoList.size() > 1) {
//            for (int i = 1; i < mobileNoList.size(); i++) {
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View rowView = inflater.inflate(R.layout.add_mobile, null);
//                mobileDetailsLayouts.add((LinearLayout) rowView);
//                ll_mobilelayout.addView(rowView, ll_mobilelayout.getChildCount());
//
//                ((EditText) mobileDetailsLayouts.get(i - 1).findViewById(R.id.edt_mobile)).setText(mobileNoList.get(i));
//            }
//        }

    }

    private void setEventHandler() {

        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_alias.setText(edt_name.getText().toString());
            }
        });

        tv_addresstype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressTypeList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetAddressType().execute();
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    addressTypeDialog(addressTypeList);
                }
            }
        });

        tv_visitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
                j = 1;
            }
        });

        tv_attachphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
                j = 2;
            }
        });

        tv_bankattachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
                j = 3;
            }
        });

        tv_panattachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
                j = 4;
            }
        });

        tv_gstattachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
                j = 5;
            }
        });

        tv_pickloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PickMapLoaction_Activity.class);
                startActivityForResult(intent, 10001);
            }
        });

        imv_add_mobno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.add_mobile, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 0);
                LinearLayout ll = (LinearLayout) rowView;
                mobileDetailsLayouts.add(ll);
                rowView.setLayoutParams(params);
                ll_mobilelayout.addView(rowView, ll_mobilelayout.getChildCount());
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    public void removeField(View view) {
        ll_mobilelayout.removeView((View) view.getParent());
        mobileDetailsLayouts.remove(view.getParent());
    }

    private void selectDocument() {

        final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Choose a Document"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a Photo")) {
                    file = new File(allInOneDocFolder, "doc_image.png");
                    photoURI = Uri.fromFile(file);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST);
                } else if (options[item].equals("Choose a Document")) {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .pickFile((Activity) context);
                }
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertD = builder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();

    }

    private void submitData() {
        if (tv_addresstype.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select Address Type");
            return;
        }

        if (edt_name.getText().toString().trim().equals("")) {
            edt_name.setError("Please Enter Name");
            edt_name.requestFocus();
            return;
        }
        if (edt_alias.getText().toString().trim().equals("")) {
            edt_alias.setError("Please Enter Alias Name");
            edt_alias.requestFocus();
            return;
        }
        if (edt_address.getText().toString().trim().equals("")) {
            edt_address.setError("Please Enter Address");
            edt_address.requestFocus();
            return;
        }
        if (edt_country.getText().toString().trim().equals("")) {
            edt_country.setError("Please Enter Country");
            edt_country.requestFocus();
            return;
        }
        if (edt_state.getText().toString().trim().equals("")) {
            edt_state.setError("Please Enter State");
            edt_state.requestFocus();
            return;
        }
        if (edt_district.getText().toString().trim().equals("")) {
            edt_district.setError("Please Enter District");
            edt_district.requestFocus();
            return;
        }
        if (!Utilities.isPinCode(edt_pincode)) {
            edt_pincode.setError("Please Enter Valid Pin Code");
            edt_pincode.requestFocus();
            return;
        }
        if (!edt_mobile1.getText().toString().equals("")) {
            if (!Utilities.isMobileNo(edt_mobile1)) {
                edt_mobile1.setError("Please Enter Valid Mobile Number");
                edt_mobile1.requestFocus();
                return;
            }
        }

//        for (int i = 0; i < mobileDetailsLayouts.size(); i++) {
//            if (!Utilities.isMobileNo((EditText) mobileDetailsLayouts.get(i).findViewById(R.id.edt_mobile))) {
//                Utilities.showSnackBar(ll_parent, "Please Enter Valid Mobile Number");
//                return;
//            }
//        }

        if (!edt_email.getText().toString().equals("")) {
            if (!Utilities.isEmailValid(edt_email)) {
                edt_email.setError("Please Enter Valid Email Address");
                edt_email.requestFocus();
                return;
            }
        }

        if (!edt_landline.getText().toString().equals("")) {
            if (!Utilities.isLandlineValid(edt_landline)) {
                edt_landline.setError("Please Enter Valid Landline Number");
                edt_landline.requestFocus();
                return;
            }
        }

        if (!edt_contactpersonmobile.getText().toString().equals("")) {
            if (!Utilities.isMobileNo(edt_contactpersonmobile)) {
                edt_contactpersonmobile.setError("Please Enter Valid Mobile Number");
                edt_contactpersonmobile.requestFocus();
                return;
            }
        }

        if (!edt_website.getText().toString().equals("")) {
            if (!Utilities.isWebsiteValid(edt_website)) {
                edt_website.setError("Please Enter Valid Website");
                edt_website.requestFocus();
                return;
            }
        }

        if (edt_bankname.getText().toString().trim().equals("")) {
            edt_bankname.setError("Please Enter Name");
            edt_bankname.requestFocus();
            return;
        }

        if (edt_bankalias.getText().toString().trim().equals("")) {
            edt_bankalias.setError("Please Enter Alias Name");
            edt_bankalias.requestFocus();
            return;
        }

        if (edt_bank_name.getText().toString().trim().equals("")) {
            edt_bank_name.setError("Please Enter Bank Name");
            edt_bank_name.requestFocus();
            return;
        }

        if (!edt_ifsc.getText().toString().trim().equals("")) {
            if (!Utilities.isIfscValid(edt_ifsc)) {
                edt_ifsc.setError("Please Enter Valid IFSC Number");
                edt_ifsc.requestFocus();
                return;
            }
        }

        if (edt_account_no.getText().toString().trim().equals("")) {
            edt_account_no.setError("Please Enter A/C No");
            edt_account_no.requestFocus();
            return;
        }


        if (!Utilities.isPanNum(edt_pan_no)) {
            edt_pan_no.setError("Please Enter Valid PAN Number");
            edt_pan_no.requestFocus();
            return;
        }

        if (!Utilities.isGSTValid(edt_gst_no)) {
            edt_gst_no.setError("Please Enter Valid GST Number");
            edt_gst_no.requestFocus();
            return;
        }


        if (Utilities.isNetworkAvailable(context)) {
            new UpdateAllInOneDetails().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Edit_AllInOne_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Edit_AllInOne_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                file = new File(filePath.get(0));
                new UploadFile().execute(file);
            }

            if (requestCode == 10001) {
                map_location_lattitude = data.getStringExtra("latitude");
                map_location_logitude = data.getStringExtra("longitude");
                tv_pickloc.setText(data.getStringExtra("latitude") + " , " + data.getStringExtra("longitude"));

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Autofill");
                builder1.setMessage("Auto fill address details ? ");
                //builder1.setIcon(R.drawable.icon_swap);
                builder1.setCancelable(false);
                builder1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        constantData.setAddressListPojo(null);
                        edt_address.setText("");
                        edt_country.setText("");
                        edt_state.setText("");
                        edt_district.setText("");
                        edt_pincode.setText("");
                        dialog.dismiss();
                    }
                });
                builder1.setPositiveButton("Autofill", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GetAddressListPojo address = (GetAddressListPojo) data.getSerializableExtra("mapAddressDetails");
                        edt_address.setText(address.getAddress_line_one());
                        edt_country.setText(address.getCountry());
                        edt_state.setText(address.getState());
                        edt_district.setText(address.getDistrict());
                        edt_pincode.setText(address.getPincode());
//                    constantData.setAddressListPojo(null);

                    }
                });
                AlertDialog alertD1 = builder1.create();
                alertD1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD1.show();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                savefile(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String filename = sourceFilename.substring(sourceFilename.lastIndexOf("/") + 1);
        String destinationFilename = Environment.getExternalStorageDirectory() + "/Address Book/"
                + "All In One Documents/" + filename;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(destinationFilename);
        new UploadFile().execute(file);
    }

    public class GetAddressType extends AsyncTask<String, Void, String> {

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
        protected String doInBackground(String... strings) {
            String res = "[]";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "getAllAddressType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.ADDRESSTYPEAPI, obj.toString());
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
                        addressTypeList = new ArrayList<AddressTypePojo>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                AddressTypePojo summary = new AddressTypePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                summary.setType_id(jsonObj.getString("type_id"));
                                summary.setType(jsonObj.getString("type"));
                                addressTypeList.add(summary);
                            }
                            addressTypeDialog(addressTypeList);
                        }
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addressTypeDialog(final List<AddressTypePojo> addressTypeList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Address Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < addressTypeList.size(); i++) {
            arrayAdapter.add(String.valueOf(addressTypeList.get(i).getType()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_addresstype.setText(addressTypeList.get(which).getType());
                type_id = addressTypeList.get(which).getType_id();
            }
        });
        builderSingle.show();
    }


    private class UploadFile extends AsyncTask<File, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(File... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.UPLOADFILEAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFile");
                multipart.addFormField("user_id", user_id);
                multipart.addFilePart("document", params[0]);

                List<String> response = multipart.finish();
                for (String line : response) {
                    res = res + line;
                }
                return res;
            } catch (IOException ex) {
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                pd.dismiss();
                if (result != null && result.length() > 0 && !result.equalsIgnoreCase("[]")) {
                    JSONObject mainObj = new JSONObject(result);
                    String type = mainObj.getString("type");
                    String message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("Success")) {
                        JSONObject Obj1 = mainObj.getJSONObject("result");
                        photoUrl = Obj1.getString("document_url");
                        if (j == 1) {
                            tv_visitcard.setText(photoUrl);
                            visiting_card = photoUrl;
                        } else if (j == 2) {
                            tv_attachphoto.setText(photoUrl);
                            photo = photoUrl;
                        } else if (j == 3) {
                            tv_bankattachfile.setText(photoUrl);
                            bank_document = photoUrl;
                        } else if (j == 4) {
                            tv_panattachfile.setText(photoUrl);
                            pan_document = photoUrl;
                        } else if (j == 5) {
                            tv_gstattachfile.setText(photoUrl);
                            gst_documant = photoUrl;
                        }

                    } else {
                        Utilities.showSnackBar(ll_parent, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdateAllInOneDetails extends AsyncTask<String, Void, String> {

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

            JsonArray array = new JsonArray();
            array.add(new JsonPrimitive(edt_mobile1.getText().toString().trim()));

//            for (int i = 0; i < mobileDetailsLayouts.size(); i++) {
//                if (!((EditText) mobileDetailsLayouts.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
//                    array.add(new JsonPrimitive(((EditText) mobileDetailsLayouts.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim()));
//                }
//            }

            obj.addProperty("type", "update");
            obj.addProperty("all_in_one_id", allinone_id);
            obj.addProperty("address_type_id", type_id);
            obj.addProperty("name", edt_name.getText().toString().trim());
            obj.addProperty("alias", edt_alias.getText().toString().trim());
            obj.addProperty("landline_number", edt_landline.getText().toString().trim());
            obj.addProperty("contact_person_name", edt_contactperson.getText().toString().trim());
            obj.addProperty("contact_person_mobile", edt_contactpersonmobile.getText().toString().trim());
            obj.addProperty("address_line_one", edt_address.getText().toString().trim());
            obj.addProperty("address_line_two", "");
            obj.addProperty("country", edt_country.getText().toString().trim());
            obj.addProperty("state", edt_state.getText().toString().trim());
            obj.addProperty("district", edt_district.getText().toString().trim());
            obj.addProperty("pincode", edt_pincode.getText().toString().trim());
            obj.addProperty("email_id", edt_email.getText().toString().trim());
            obj.addProperty("website", edt_website.getText().toString().trim());
            obj.addProperty("visiting_card", visiting_card);
            obj.addProperty("map_location_logitude", map_location_logitude);
            obj.addProperty("map_location_latitude", map_location_lattitude);
            obj.addProperty("photo", photo);
            obj.addProperty("account_holder_name", edt_bankname.getText().toString().trim());
            obj.addProperty("account_holder_alias", edt_bankalias.getText().toString().trim());
            obj.addProperty("bank_name", edt_bank_name.getText().toString().trim());
            obj.addProperty("ifsc_code", edt_ifsc.getText().toString().trim());
            obj.addProperty("account_number", edt_account_no.getText().toString().trim());
            obj.addProperty("bank_document", bank_document);
            obj.addProperty("pan_number", edt_pan_no.getText().toString().trim());
            obj.addProperty("gst_number", edt_gst_no.getText().toString().trim());
            obj.addProperty("pan_document", pan_document);
            obj.addProperty("gst_document", gst_documant);
            obj.addProperty("status", STATUS.toLowerCase());
            obj.addProperty("created_by", user_id);
            obj.addProperty("updated_by", user_id);
            obj.add("mobile_number", array);

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
                        builder.setMessage("Details Uploaded Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                new My_AllInOne_Fragment.GetAllInOneList().execute();
                                new Offline_AllInOne_Fragment.GetAllInOneList().execute();
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

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Edit All in One Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
