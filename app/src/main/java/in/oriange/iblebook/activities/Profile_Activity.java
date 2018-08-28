package in.oriange.iblebook.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
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
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.MultipartUtility;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.iblebook.utilities.PermissionUtil.doesAppNeedPermissions;

public class Profile_Activity extends Activity {

    private Context context;
    public static final int CAMERA_REQUEST = 100;
    private UserSessionManager session;
    private EditText edt_name, edt_aliasname, edt_mobile, edt_email;
    private String user_id, photo, name, alias, country_code, mobile, email, photo_url, password;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}; // List of permissions required
    private ProgressDialog pd;
    private ImageView imv_profile, img_finish;
    private CoordinatorLayout ll_parent;
    public static final int GALLERY_REQUEST = 200;
    public Uri photoURI;
    File file, profilPicFolder;
    private FloatingActionButton fab_edt_profilepic, fab_edtprofie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        getSessionData();
        setDefaults();
        setEventHandler();
//        setupToolbar();
    }

    private void init() {
        context = Profile_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
        fab_edt_profilepic = findViewById(R.id.fab_edt_profilepic);
        fab_edtprofie = findViewById(R.id.fab_edtprofie);
        ll_parent = findViewById(R.id.ll_parent);
        imv_profile = findViewById(R.id.imv_profile);
        img_finish = findViewById(R.id.img_finish);
//        tv_name = findViewById(R.id.tv_bankname);
        edt_name = findViewById(R.id.edt_name);
        edt_aliasname = findViewById(R.id.edt_aliasname);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);


        profilPicFolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "Profile Pic");
        if (!profilPicFolder.exists())
            profilPicFolder.mkdirs();

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
            photo = json.getString("photo");
            name = json.getString("name");
            alias = json.getString("alias");
            country_code = json.getString("country_code");
            mobile = json.getString("mobile");
            password = json.getString("password");
            email = json.getString("email");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDefaults() {
        Picasso.with(context)
                .load(photo)
                .placeholder(R.drawable.icon_userprofile)
                .into(imv_profile);

        edt_name.setText(name);
        edt_aliasname.setText(alias);
        edt_mobile.setText(/*country_code + "" + */mobile);
        edt_email.setText(email);

    }

    private void setEventHandler() {
//        imv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LayoutInflater layoutInflater = LayoutInflater.from(context);
//                View promptView = layoutInflater.inflate(R.layout.prompt_profile_pic, null);
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//                alertDialogBuilder.setView(promptView);
//                CircleImageView imv_labphoto = promptView.findViewById(R.id.imv_profile);
//                Picasso.with(context)
//                        .load(photo)
//                        .placeholder(R.drawable.icon_userprofile)
//                        .into(imv_labphoto);
//                AlertDialog alertD = alertDialogBuilder.create();
//                alertD.show();
//
//            }
//        });

        img_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab_edt_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    if (doesAppNeedPermissions()) {
                        askPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }

            }
        });

        fab_edtprofie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProfile_Activity.class);
                startActivity(intent);
                finish();
            }
        });


        edt_name.setLongClickable(false);
        edt_aliasname.setLongClickable(false);
        edt_mobile.setLongClickable(false);
        edt_email.setLongClickable(false);
    }

    private void selectImage() {
        final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a Photo")) {
                    file = new File(profilPicFolder, "doc_image.png");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Profile_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Profile_Activity.this);
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

    void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String destinationFilename = Environment.getExternalStorageDirectory() + "/Address Book/"
                + "/Profile Pic/" + File.separatorChar + "uplimg.png";

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

        File imageFile = new File(destinationFilename);
        new UploadProfilePic().execute(imageFile);
//        doc_image_uri = Uri.fromFile(imageFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Alert");
                    builder.setMessage("Please provide permission for Camera and Gallery");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }

        }
    }

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Profile");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class UploadProfilePic extends AsyncTask<File, Integer, String> {

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
                        photo_url = Obj1.getString("document_url");
                        photo = photo_url;
                        Picasso.with(context)
                                .load(photo_url)
                                .placeholder(R.drawable.icon_userprofile)
                                .into(imv_profile);
                        if (Utilities.isNetworkAvailable(context)) {
                            new UpdateProfileData().execute();
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }

                    } else {
                        Utilities.showSnackBar(ll_parent, message);
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdateProfileData extends AsyncTask<String, Void, String> {

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
                obj.put("type", "Update");
                obj.put("user_id", user_id);
                obj.put("name", name);
                obj.put("alias", alias);
                obj.put("country_code", "+91");
                obj.put("mobile", mobile);
                obj.put("email", email);
                obj.put("password", password);
                obj.put("photo", photo_url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.USERAPI, obj.toString());
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        Utilities.showAlertDialog(context, "Success", "Profile Pic Update Successfully", true);
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.updateSession(jsonarr.toString());
                            }
                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showSnackBar(ll_parent, message);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
