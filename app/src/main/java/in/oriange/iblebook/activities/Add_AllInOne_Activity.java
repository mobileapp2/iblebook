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
import in.oriange.iblebook.models.AddressTypePojo;
import in.oriange.iblebook.models.GetAddressListPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class Add_AllInOne_Activity extends Activity {


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
    private String STATUS, latitude = "", longitude = "";

    private List<AddressTypePojo> addressTypeList;
    private File file, allInOneDocFolder, visitCardToBeUploaded, photoToBeUploaded;

    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private String user_id, visitCardUrl = "", photoUrl = "", type_id;
    public Uri photoURI;
    private int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allinone);

        init();
        getSessionData();
        setEventHandler();
        setupToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Add_AllInOne_Activity.this);
    }

    private void init() {
        context = Add_AllInOne_Activity.this;
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

        STATUS = getIntent().getStringExtra("STATUS");

        addressTypeList = new ArrayList<AddressTypePojo>();

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

        tv_bankattachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(3);
                j = 3;
            }
        });

        tv_panattachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(4);
                j = 4;
            }
        });

        tv_gstattachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(5);
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
    }

    private void submitData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Add_AllInOne_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Add_AllInOne_Activity.this);
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

            if (requestCode == 10001) {
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
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
        if (j == 1) {
            tv_visitcard.setText(destinationFilename);
            visitCardToBeUploaded = new File(destinationFilename);
        } else if (j == 2) {
            tv_attachphoto.setText(destinationFilename);
            photoToBeUploaded = new File(destinationFilename);
        }
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

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("All in One");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
