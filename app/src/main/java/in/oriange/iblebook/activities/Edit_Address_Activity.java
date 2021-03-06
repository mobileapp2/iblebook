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
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
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
import in.oriange.iblebook.fragments.My_Address_Fragment;
import in.oriange.iblebook.fragments.Offline_Address_Fragment;
import in.oriange.iblebook.fragments.Received_Address_Fragment;
import in.oriange.iblebook.models.AddressTypePojo;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.MultipartUtility;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class Edit_Address_Activity extends Activity {

    public final int CAMERA_REQUEST = 100;
    public final int GALLERY_REQUEST = 200;
    private Context context;
    private LinearLayout ll_parent, ll_mobilelayout;
    private TextView tv_addresstype, tv_visitcard, tv_attachphoto, tv_pickloc;
    private EditText edt_name, edt_alias, edt_address, edt_country, edt_state, edt_district, edt_pincode, edt_mobile1,
            edt_landline, edt_contactperson, edt_contactpersonmobile, edt_email, edt_website;
    private Button btn_save;
    private String user_id, visiting_card = "", photo = "", type_id, address_id, fileUrl = "",
            map_location_lattitude, map_location_logitude, name, STATUS;
    private File file, addressDocFolder;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}; // List of permissions required
    private ProgressDialog pd;
    private UserSessionManager session;
    private ImageView imv_add_mobno;

    private Uri photoURI;
    private int j = 0;
    private List<AddressTypePojo> addressTypeList;
    private List<LinearLayout> mobileDetailsLayouts;
    private List<String> mobileNoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        init();
        getSessionData();
        getIntentData();
        setEventHandler();
        setupToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Edit_Address_Activity.this);
    }

    private void init() {
        context = Edit_Address_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
        addressTypeList = new ArrayList<AddressTypePojo>();
        ll_parent = findViewById(R.id.ll_parent);
        tv_addresstype = findViewById(R.id.tv_addresstype);
        tv_visitcard = findViewById(R.id.tv_visitcard);
        tv_attachphoto = findViewById(R.id.tv_attachphoto);
        tv_pickloc = findViewById(R.id.tv_pickloc);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_address = findViewById(R.id.edt_address);
        edt_country = findViewById(R.id.edt_country);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_mobile1 = findViewById(R.id.edt_mobile1);
        edt_landline = findViewById(R.id.edt_landline);
        edt_contactperson = findViewById(R.id.edt_contactperson);
        edt_contactpersonmobile = findViewById(R.id.edt_contactpersonmobile);
        edt_email = findViewById(R.id.edt_email);
        edt_website = findViewById(R.id.edt_website);
        btn_save = findViewById(R.id.btn_save);

        imv_add_mobno = findViewById(R.id.imv_add_mobno);
        ll_mobilelayout = findViewById(R.id.ll_mobilelayout);

        addressDocFolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "Address Documents");
        if (!addressDocFolder.exists())
            addressDocFolder.mkdirs();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        addressTypeList = new ArrayList<>();
        mobileDetailsLayouts = new ArrayList<>();
        mobileNoList = new ArrayList<>();

        STATUS = getIntent().getStringExtra("STATUS");
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
        type_id = getIntent().getStringExtra("type_id");
        address_id = getIntent().getStringExtra("address_id");
        tv_addresstype.setText(getIntent().getStringExtra("type"));
        edt_name.setText(getIntent().getStringExtra("name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_address.setText(getIntent().getStringExtra("address_line_one"));
        edt_country.setText(getIntent().getStringExtra("country"));
        edt_state.setText(getIntent().getStringExtra("state"));
        edt_district.setText(getIntent().getStringExtra("district"));
        edt_pincode.setText(getIntent().getStringExtra("pincode"));
        edt_email.setText(getIntent().getStringExtra("email_id"));
        edt_mobile1.setText(getIntent().getStringExtra("mobile_number"));
        edt_website.setText(getIntent().getStringExtra("website"));
        visiting_card = getIntent().getStringExtra("visiting_card");
        photo = getIntent().getStringExtra("photo");
        map_location_lattitude = getIntent().getStringExtra("map_location_lattitude");
        map_location_logitude = getIntent().getStringExtra("map_location_logitude");

        edt_landline.setText(getIntent().getStringExtra("landline_number"));
        edt_contactperson.setText(getIntent().getStringExtra("contact_person_name"));
        edt_contactpersonmobile.setText(getIntent().getStringExtra("contact_person_mobile"));

        mobileNoList = Arrays.asList(getIntent().getStringExtra("mobile_number").split("\\s*,\\s*"));


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


        if (!map_location_lattitude.isEmpty() || !map_location_logitude.isEmpty()) {
            tv_pickloc.setText(map_location_lattitude + " , " + map_location_logitude);
        }
        user_id = getIntent().getStringExtra("created_by");
        STATUS = getIntent().getStringExtra("STATUS");


        if (!visiting_card.isEmpty()) {
            tv_visitcard.setText("Replace Visit Card Attached");
        }

        if (!photo.isEmpty()) {
            tv_attachphoto.setText("Replace Photo");
        }

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

        tv_pickloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PickMapLoaction_Activity.class);
                startActivityForResult(intent, 10001);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
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
                rowView.setLayoutParams(params);
                ll_mobilelayout.addView(rowView, ll_mobilelayout.getChildCount());
            }
        });
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
                    file = new File(addressDocFolder, "doc_image.png");
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


        if (Utilities.isNetworkAvailable(context)) {
            new UploadAddressDetails().execute();
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Edit_Address_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Edit_Address_Activity.this);
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
                builder1.setCancelable(false);
                builder1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                + "Address Documents/" + filename;

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

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Edit Address");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                        fileUrl = Obj1.getString("document_url");
                        if (j == 1) {
                            tv_visitcard.setText(fileUrl);
                            visiting_card = fileUrl;
                        } else if (j == 2) {
                            tv_attachphoto.setText(fileUrl);
                            photo = fileUrl;
                        }

                    } else {
                        Utilities.showSnackBar(ll_parent, message);
                    }
                } else {
//                    Utilities.showSnackBar(ll_parent, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UploadAddressDetails extends AsyncTask<String, Void, String> {

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
            obj.addProperty("address_id", address_id);
            obj.addProperty("type_id", type_id);
            obj.addProperty("name", edt_name.getText().toString().trim());
            obj.addProperty("alias", edt_alias.getText().toString().trim());
            obj.addProperty("address_line_one", edt_address.getText().toString().trim());
            obj.addProperty("address_line_two", "");
            obj.addProperty("country", edt_country.getText().toString().trim());
            obj.addProperty("state", edt_state.getText().toString().trim());
            obj.addProperty("district", edt_district.getText().toString().trim());
            obj.addProperty("pincode", edt_pincode.getText().toString().trim());
            obj.addProperty("email_id", edt_email.getText().toString().trim());
            obj.addProperty("website", edt_website.getText().toString().trim());
            obj.addProperty("landline_number", edt_landline.getText().toString().trim());
            obj.addProperty("contact_person_name", edt_contactperson.getText().toString().trim());
            obj.addProperty("contact_person_mobile", edt_contactpersonmobile.getText().toString().trim());
            obj.addProperty("visiting_card", visiting_card);
            obj.addProperty("map_location_logitude", map_location_logitude);
            obj.addProperty("map_location_lattitude", map_location_lattitude);
            obj.addProperty("photo", photo);
            obj.addProperty("status", STATUS.toLowerCase());
            obj.add("mobile_number", array);
            obj.addProperty("created_by", user_id);
            obj.addProperty("updated_by", user_id);
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
                        builder.setMessage("Address Details Uploaded Successfully");
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
