package in.oriange.iblebook.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.My_Bank_Fragment;
import in.oriange.iblebook.fragments.Offline_Bank_Fragment;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.MultipartUtility;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;
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

public class Edit_Bank_Activity extends Activity {

    private Context context;
    private UserSessionManager session;
    private String user_id, document, bank_id, name;
    private EditText edt_name, edt_alias, edt_bank_name, edt_ifsc, edt_account_no;
    private TextView tv_attachfile;
    private Button btn_save;
    private LinearLayout ll_parent;

    public static final int CAMERA_REQUEST = 100;
    public static final int GALLERY_REQUEST = 200;
    public static final int DOCUMENT_REQUEST = 300;
    private File file, bankDocFolder, fileToBeUploaded;
    public Uri photoURI;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}; // List of permissions required
    private ProgressDialog pd;
    private int position;
    private String STATUS;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank);

        init();
        getSessionData();
        getIntentData();
        setEventHandler();
        setupToolbar();
    }

    private void init() {
        context = Edit_Bank_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
        dbHelper = new DataBaseHelper(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_bank_name = findViewById(R.id.edt_bank_name);
        edt_ifsc = findViewById(R.id.edt_ifsc);
        edt_account_no = findViewById(R.id.edt_account_no);
        tv_attachfile = findViewById(R.id.tv_attachfile);
        btn_save = findViewById(R.id.btn_save);


        bankDocFolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "Bank Documents");
        if (!bankDocFolder.exists())
            bankDocFolder.mkdirs();

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
        bank_id = getIntent().getStringExtra("bank_id");
        document = getIntent().getStringExtra("document");
        STATUS = getIntent().getStringExtra("STATUS");

        edt_name.setText(getIntent().getStringExtra("account_holder_name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_bank_name.setText(getIntent().getStringExtra("bank_name"));
        edt_ifsc.setText(getIntent().getStringExtra("ifsc_code"));
        edt_account_no.setText(getIntent().getStringExtra("account_no"));
    }

    private void setEventHandler() {
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
                    file = new File(bankDocFolder, "doc_image.png");
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
    }

    private void submitData() {
        if (edt_name.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Name");
            return;
        }
        if (edt_alias.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Alias Name");
            return;
        }
        if (edt_bank_name.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Bank Name");
            return;
        }
        if (!Utilities.isIfscValid(edt_ifsc)) {
            Utilities.showSnackBar(ll_parent, "Please Enter Valid IFSC Number");
            return;
        }
        if (edt_account_no.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Account Number");
            return;
        }
//        if (tv_attachfile.getText().toString().trim().equals("")) {
//            Utilities.showSnackBar(ll_parent, "Please Attach Document");
//            return;
//        }

        if (STATUS.equals("ONLINE")) {
            if (tv_attachfile.getText().toString().trim().equals("")) {
                if (Utilities.isNetworkAvailable(context)) {
                    new UpdateBankDetails().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    new UploadDocument().execute(fileToBeUploaded);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        } else if (STATUS.equals("OFFLINE")) {
            String path = "";
            if (tv_attachfile.getText().toString().trim().equals("")) {
                path = document;
            } else {
                path = fileToBeUploaded.getPath();
            }
            long result = dbHelper.updateBankDetailsInDb(
                    bank_id,
                    user_id,
                    edt_name.getText().toString().trim(),
                    edt_alias.getText().toString().trim(),
                    edt_bank_name.getText().toString().trim(),
                    edt_ifsc.getText().toString().trim(),
                    edt_account_no.getText().toString().trim(),
                    path,
                    "0");

            if (result != -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bank Details Updated Successfully");
                builder.setTitle("Success");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Offline_Bank_Fragment.setDefault();
                    }
                });
                builder.show();
            } else {
                Utilities.showSnackBar(ll_parent, "Bank Details Did Not Save Properly");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Edit_Bank_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Edit_Bank_Activity.this);
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
                + "Bank Documents/" + filename;

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
                        document = Obj1.getString("document_url");
                        new UpdateBankDetails().execute();
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

    public class UpdateBankDetails extends AsyncTask<String, Void, String> {

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
                obj.put("type", "UpdateBankDetails");
                obj.put("account_holder_name", edt_name.getText().toString().trim());
                obj.put("alias", edt_alias.getText().toString().trim());
                obj.put("bank_name", edt_bank_name.getText().toString().trim());
                obj.put("ifsc_code", edt_ifsc.getText().toString().trim());
                obj.put("account_no", edt_account_no.getText().toString().trim());
                obj.put("document", document);
                obj.put("created_by", user_id);
                obj.put("updated_by", user_id);
                obj.put("bank_id", bank_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                        builder.setMessage("Bank Details Updated Successfully");
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                new My_Bank_Fragment.GetBankList().execute();
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

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Edit Bank Detials");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
