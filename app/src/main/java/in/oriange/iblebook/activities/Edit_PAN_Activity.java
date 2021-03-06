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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import in.oriange.iblebook.fragments.My_PAN_Fragment;
import in.oriange.iblebook.fragments.Offline_PAN_Fragment;
import in.oriange.iblebook.fragments.Received_PAN_Fragment;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.MultipartUtility;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class Edit_PAN_Activity extends Activity {

    public static final int CAMERA_REQUEST = 100;
    public static final int GALLERY_REQUEST = 200;
    public static final int DOCUMENT_REQUEST = 300;
    public Uri photoURI;
    private Context context;
    private UserSessionManager session;
    private String user_id, pan_document, tax_id;
    private EditText edt_name, edt_alias, edt_pan_no;
    private TextView tv_attachfile;
    private Button btn_save;
    private LinearLayout ll_parent;
    private File file, panDocFolder, fileToBeUploaded;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}; // List of permissions required
    private ProgressDialog pd;
    private int position;
    private String STATUS;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pan);

        init();
        getSessionData();
        getIntentData();
        setEventHandler();
        setupToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Edit_PAN_Activity.this);
    }

    private void init() {
        context = Edit_PAN_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
        dbHelper = new DataBaseHelper(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_pan_no = findViewById(R.id.edt_pan_no);
        tv_attachfile = findViewById(R.id.tv_attachfile);
        btn_save = findViewById(R.id.btn_save);

        panDocFolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "PAN Documents");
        if (!panDocFolder.exists())
            panDocFolder.mkdirs();

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
        position = getIntent().getIntExtra("position", 0);
        user_id = getIntent().getStringExtra("created_by");
        tax_id = getIntent().getStringExtra("tax_id");
        pan_document = getIntent().getStringExtra("pan_document");
        STATUS = getIntent().getStringExtra("STATUS");

        edt_name.setText(getIntent().getStringExtra("name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_pan_no.setText(getIntent().getStringExtra("pan_number"));


        if (!pan_document.isEmpty()) {
            tv_attachfile.setText("Replace PAN Document");
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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        tv_attachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
            }
        });
    }

    private void selectDocument() {
        final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Choose a Document"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a Photo")) {
                    file = new File(panDocFolder, "doc_image.png");
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

        if (!Utilities.isPanNum(edt_pan_no)) {
            edt_pan_no.setError("Please Enter Valid PAN Number");
            edt_pan_no.requestFocus();
            return;
        }


        if (Utilities.isNetworkAvailable(context)) {

            if (!pan_document.equals("")) {
                new UpdatePANDetails().execute();
            } else {
                if (!tv_attachfile.getText().toString().isEmpty()) {
                    new UploadDocument().execute(fileToBeUploaded);
                } else {
                    new UpdatePANDetails().execute();
                }
            }

        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }

//        if (!pan_document.equals("")) {
//            if (Utilities.isNetworkAvailable(context)) {
//                new UpdatePANDetails().execute();
//            } else {
//                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
//            }
//        } else {
//            if (Utilities.isNetworkAvailable(context)) {
//                new UploadDocument().execute(fileToBeUploaded);
//            } else {
//                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
//            }
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            pan_document = "";
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Edit_PAN_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Edit_PAN_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                fileToBeUploaded = new File(filePath.get(0));
                tv_attachfile.setText(filePath.get(0));
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
                + "PAN Documents/" + filename;

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

        tv_attachfile.setText(destinationFilename);
        fileToBeUploaded = new File(destinationFilename);
    }

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Edit PAN Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class UploadDocument extends AsyncTask<File, Integer, String> {

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
                        pan_document = Obj1.getString("document_url");
                        new UpdatePANDetails().execute();
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

    public class UpdatePANDetails extends AsyncTask<String, Void, String> {

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
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "UpdateTaxDetails");
                obj.put("name", edt_name.getText().toString().trim());
                obj.put("alias", edt_alias.getText().toString().trim());
                obj.put("pan_number", edt_pan_no.getText().toString().trim());
                obj.put("pan_document", pan_document);
                obj.put("gst_number", "");
                obj.put("gst_document", "");
                obj.put("created_by", user_id);
                obj.put("updated_by", user_id);
                obj.put("tax_id", tax_id);
                obj.put("status", STATUS.toLowerCase());
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

                        new My_PAN_Fragment.GetPANList().execute();
                        new Offline_PAN_Fragment.GetPANList().execute();
                        new Received_PAN_Fragment.GetPANList().execute();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("PAN Details Updated Successfully");
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
