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
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.My_Address_Fragment;
import in.oriange.iblebook.fragments.Offline_Address_Fragment;
import in.oriange.iblebook.fragments.Received_Address_Fragment;
import in.oriange.iblebook.models.AddressTypePojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.ConstantData;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.MultipartUtility;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class Edit_Address_Activity extends Activity {

    public static final int CAMERA_REQUEST = 100;
    public static final int GALLERY_REQUEST = 200;
    public Uri photoURI;
    int j = 0;
    List<AddressTypePojo> addressTypeList;
    private Context context;
    private LinearLayout ll_parent;
    private TextView tv_addresstype, tv_visitcard, tv_attachphoto, tv_pickloc;
    private EditText edt_name, edt_alias, edt_address, edt_country,
            edt_state, edt_district, edt_pincode, edt_mobile1, edt_email, edt_website;
    private Button btn_save;
    private String user_id, visitCardUrl = "", photoUrl = "", type_id, address_id,
            map_location_lattitude, map_location_logitude, name;
    private File file, addressDocFolder, visitCardToBeUploaded, photoToBeUploaded;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}; // List of permissions required
    private ProgressDialog pd;
    private UserSessionManager session;
    private ImageView imv_add_mobno;
    private LinearLayout ll_mobilelayout;
    private String STATUS, latitude = "", longitude = "";
    private DataBaseHelper dbHelper;
    private ConstantData constantData;

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
    protected void onResume() {
        super.onResume();

        if (constantData.getLatitude().equals("") || constantData.getLongitude().equals("")) {
            tv_pickloc.setText("");
        } else {
            tv_pickloc.setText(constantData.getLatitude() + " , " + constantData.getLongitude());
        }

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
        constantData = ConstantData.getInstance();
        dbHelper = new DataBaseHelper(context);
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
        edt_email = findViewById(R.id.edt_email);
        edt_website = findViewById(R.id.edt_website);
        btn_save = findViewById(R.id.btn_save);

        constantData.setLatitude("");
        constantData.setLongitude("");

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
        visitCardUrl = getIntent().getStringExtra("visiting_card");
        map_location_lattitude = getIntent().getStringExtra("map_location_lattitude");
        map_location_logitude = getIntent().getStringExtra("map_location_logitude");

        constantData.setLatitude(map_location_lattitude);
        constantData.setLongitude(map_location_logitude);

        tv_pickloc.setText(constantData.getLatitude() + " , " + constantData.getLongitude());
        photoUrl = getIntent().getStringExtra("photo");
        user_id = getIntent().getStringExtra("created_by");

        STATUS = getIntent().getStringExtra("STATUS");

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
                selectDocument(1);
                j = 1;
            }
        });

        tv_attachphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(2);
                j = 2;
            }
        });

        tv_pickloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PickMapLoaction_Activity.class));
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
                View rowView = inflater.inflate(R.layout.edit_addmobile, null);
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

    private void selectDocument(int i) {

        if (i == 1) {
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
            builder.show();
        } else if (i == 2) {
            final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
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
                    }
                }
            });
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void submitData() {
        if (tv_addresstype.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select Address Type");
            return;
        }
        if (edt_name.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Name");
            return;
        }
        if (edt_alias.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Alias Name");
            return;
        }
        if (edt_address.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Address");
            return;
        }
        if (edt_country.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Country");
            return;
        }
        if (edt_state.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter State");
            return;
        }
        if (edt_district.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter District");
            return;
        }
        if (!Utilities.isPinCode(edt_pincode)) {
            Utilities.showSnackBar(ll_parent, "Please Enter Valid Pin Code");
            return;
        }
        if (!edt_mobile1.getText().toString().equals("")) {
            if (!Utilities.isMobileNo(edt_mobile1)) {
                Utilities.showSnackBar(ll_parent, "Please Enter Valid Mobile Number");
                return;
            }
        }
        if (!edt_email.getText().toString().equals("")) {
            if (!Utilities.isEmailValid(edt_email)) {
                Utilities.showSnackBar(ll_parent, "Please Enter Valid Email Address");
                return;
            }
        }
//        if (edt_website.getText().toString().trim().equals("")) {
//            Utilities.showSnackBar(ll_parent, "Please Enter Website");
//            return;
//        }
//        if (tv_pickloc.getText().toString().trim().equals("")) {
//            Utilities.showSnackBar(ll_parent, "Please Pick Location");
//            return;
//        }
//        if (tv_visitcard.getText().toString().trim().equals("")) {
//            Utilities.showSnackBar(ll_parent, "Please Attach Visiting Card");
//            return;
//        }
//        if (tv_attachphoto.getText().toString().trim().equals("")) {
//            Utilities.showSnackBar(ll_parent, "Please Attach Photo");
//            return;
//        }

        if (!tv_visitcard.getText().toString().equals("") && !tv_attachphoto.getText().toString().equals("")) {
            if (Utilities.isNetworkAvailable(context)) {
                new UploadVisitCard().execute(visitCardToBeUploaded);
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            }
        } else if (!tv_visitcard.getText().toString().equals("") && tv_attachphoto.getText().toString().equals("")) {
            if (Utilities.isNetworkAvailable(context)) {
                new UploadVisitCard().execute(visitCardToBeUploaded);
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            }
        } else if (tv_visitcard.getText().toString().equals("") && !tv_attachphoto.getText().toString().equals("")) {
            if (Utilities.isNetworkAvailable(context)) {
                new UploadPhoto().execute(photoToBeUploaded);
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            }
        } else if (tv_visitcard.getText().toString().equals("") && tv_attachphoto.getText().toString().equals("")) {
            if (Utilities.isNetworkAvailable(context)) {
                new UploadAddressDetails().execute();
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                if (j == 1) {
                    visitCardToBeUploaded = new File(filePath.get(0));
                    tv_visitcard.setText(filePath.get(0));
                } else if (j == 2) {
                    photoToBeUploaded = new File(filePath.get(0));
                    tv_attachphoto.setText(filePath.get(0));
                }
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
        if (j == 1) {
            tv_visitcard.setText(destinationFilename);
            visitCardToBeUploaded = new File(destinationFilename);
        } else if (j == 2) {
            tv_attachphoto.setText(destinationFilename);
            photoToBeUploaded = new File(destinationFilename);
        }
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

    private class UploadVisitCard extends AsyncTask<File, Integer, String> {

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
                        visitCardUrl = Obj1.getString("document_url");

                        if (!tv_visitcard.getText().toString().equals("") && !tv_attachphoto.getText().toString().equals("")) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new UploadPhoto().execute(photoToBeUploaded);
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else if (!tv_visitcard.getText().toString().equals("") && tv_attachphoto.getText().toString().equals("")) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new UploadAddressDetails().execute();
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else if (tv_visitcard.getText().toString().equals("") && !tv_attachphoto.getText().toString().equals("")) {

                        } else if (tv_visitcard.getText().toString().equals("") && tv_attachphoto.getText().toString().equals("")) {

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

    private class UploadPhoto extends AsyncTask<File, Integer, String> {

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

                        if (!tv_visitcard.getText().toString().equals("") && !tv_attachphoto.getText().toString().equals("")) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new UploadAddressDetails().execute();
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else if (!tv_visitcard.getText().toString().equals("") && tv_attachphoto.getText().toString().equals("")) {

                        } else if (tv_visitcard.getText().toString().equals("") && !tv_attachphoto.getText().toString().equals("")) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new UploadAddressDetails().execute();
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else if (tv_visitcard.getText().toString().equals("") && tv_attachphoto.getText().toString().equals("")) {

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
            obj.addProperty("visiting_card", visitCardUrl);
            obj.addProperty("map_location_logitude", constantData.getLongitude());
            obj.addProperty("map_location_lattitude", constantData.getLatitude());
            obj.addProperty("photo", photoUrl);
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
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
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
