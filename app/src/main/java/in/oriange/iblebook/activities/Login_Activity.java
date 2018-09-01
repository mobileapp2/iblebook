package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.PermissionUtil;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class Login_Activity extends Activity {

    private Context context;
    private LinearLayout ll_parent;
    private EditText edt_username, edt_password;
    private TextView tv_forgotpass, tv_register;
    private Button btn_login;
    private int i = 0;
    private UserSessionManager session;
    String user_id, mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = Login_Activity.this;
        session = new UserSessionManager(context);


        String openedNo = session.isThisFirstOpen().get(
                ApplicationConstants.KEY_APPOPENEDFORFIRST);
        if (openedNo == null) {
            startActivity(new Intent(context, Intro_Activity.class));
        }

        init();
        setEventHandler();
        checkPermissions();
    }

    private void init() {
        ll_parent = findViewById(R.id.ll_parent);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        tv_forgotpass = findViewById(R.id.tv_forgotpass);
        tv_register = findViewById(R.id.tv_register);
        btn_login = findViewById(R.id.btn_login);
    }

    private void setEventHandler() {
        tv_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.prompt_forgotpassword, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Enter Registered Mobile");
                alertDialogBuilder.setView(promptView);

                final EditText edt_entermobile = promptView.findViewById(R.id.edt_entermobile);

                alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Utilities.isMobileNo(edt_entermobile)) {
                            Utilities.showMessageString(context, "Please Enter Valid Mobile Number");
                            return;
                        }

                        mobileNo = edt_entermobile.getText().toString().trim();
                        if (Utilities.isNetworkAvailable(context)) {
                            new SendOTP().execute();
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertD = alertDialogBuilder.create();
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Register_Activity.class));
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();

            }
        });
    }

    private void createDialogForOTP(final String otp) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_enterotp, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Enter OTP");
        alertDialogBuilder.setView(promptView);

        final EditText edt_enterotp = promptView.findViewById(R.id.edt_enterotp);

        alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (otp.equals(edt_enterotp.getText().toString().trim())) {
                    createDialogForPassword();
                } else {
                    Utilities.showMessageString(context, "Please Enter Correct OTP");
                    createDialogForOTP(otp);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertDialogBuilder.setNeutralButton("Resend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context)) {
                    new SendOTP().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    private void createDialogForPassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_changepassword, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Change Password");
        alertDialogBuilder.setView(promptView);

        final EditText edt_oldpassword = promptView.findViewById(R.id.edt_oldpassword);
        final EditText edt_enterpassword = promptView.findViewById(R.id.edt_enterpassword);
        final EditText edt_confirmpassword = promptView.findViewById(R.id.edt_confirmpassword);

        edt_oldpassword.setVisibility(View.GONE);

        edt_confirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_confirmpassword.getText().toString().trim().equals(edt_enterpassword.getText().toString().trim())) {
                    edt_confirmpassword.setError("Passwords does not match");
                } else {
                    edt_confirmpassword.setError(null);
                }
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!edt_confirmpassword.getText().toString().trim().equals(edt_enterpassword.getText().toString().trim())) {
                    Utilities.showMessageString(context, "Passwords does not match");
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new ChangePassword().execute(edt_confirmpassword.getText().toString().trim());
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }

            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class SendOTP extends AsyncTask<String, Void, String> {

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
                obj.put("type", "ForgetPassword");
                obj.put("mobile", mobileNo);
                obj.put("email", "");
                obj.put("otp_type", "send");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.SENDOTPAPI, obj.toString());
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
                        String OTP = mainObj.getString("otp");
                        user_id = mainObj.getString("user_id");
                        createDialogForOTP(OTP);
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ChangePassword extends AsyncTask<String, Void, String> {

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
                obj.put("type", "UpdateUserPassword");
                obj.put("password", params[0]);
                obj.put("user_id", user_id);
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
                        Utilities.showAlertDialog(context, "Success", "Password Changed Successfully", true);
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showSnackBar(ll_parent, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void submitData() {
        if (edt_username.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Username");
            return;
        }
        if (edt_password.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Password");
            return;
        }

        if (Utilities.isNetworkAvailable(context)) {
            new LoginUser().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }

    private void saveRegistrationID() {
        String user_id = "", regToken = "";
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));

            for (int j = 0; j < user_info.length(); j++) {
                JSONObject json = user_info.getJSONObject(j);
                user_id = json.getString("user_id");
            }

            regToken = session.getAndroidToken().get(ApplicationConstants.KEY_ANDROIDTOKETID);

            if (regToken != null && !regToken.isEmpty() && !regToken.equals("null") && !regToken.equals(""))
                new SendRegistrationToken().execute(user_id, regToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String totalRAMSize() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        double totalRAM = memoryInfo.totalMem / 1048576.0;
        return String.valueOf(totalRAM);
    }

    private void checkPermissions() {
        if (!PermissionUtil.askPermissions(this)) {
            // permision not required or already given
//            startService(new Intent(context, ChecklistSyncServiceHLL.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.PERMISSION_ALL: {

                if (grantResults.length > 0) {

                    List<Integer> indexesOfPermissionsNeededToShow = new ArrayList<>();

                    for (int i = 0; i < permissions.length; ++i) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            indexesOfPermissionsNeededToShow.add(i);
                        }
                    }

                    int size = indexesOfPermissionsNeededToShow.size();
                    if (size != 0) {
                        int i = 0;
                        boolean isPermissionGranted = true;

                        while (i < size && isPermissionGranted) {
                            isPermissionGranted = grantResults[indexesOfPermissionsNeededToShow.get(i)]
                                    == PackageManager.PERMISSION_GRANTED;
                            i++;
                        }

                        if (!isPermissionGranted) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Permissions mandatory")
                                    .setMessage("All the permissions are required for this app")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkPermissions();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        }
    }

    public class LoginUser extends AsyncTask<String, Void, String> {

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
                obj.put("type", "Login");
                obj.put("Username", edt_username.getText().toString());
                obj.put("password", edt_password.getText().toString());
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
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());
                                saveRegistrationID();
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

    public class SendRegistrationToken extends AsyncTask<String, Integer, String> {
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
            String s = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("device_type", "Android");
                obj.put("device_id", params[1]);
                obj.put("ram", totalRAMSize());
                obj.put("processor", Build.CPU_ABI);
                obj.put("device_os", Build.VERSION.RELEASE);
                obj.put("location", "0.0, 0.0");
                obj.put("device_model", Build.MODEL);
                obj.put("manufacturer", Build.MANUFACTURER);
                obj.put("user_id", params[0]);
                obj.put("type", "registerDevice");
                s = obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = WebServiceCalls.APICall(ApplicationConstants.DEVICEREGAPI, s);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result != null && result.length() > 0 && !result.equalsIgnoreCase("[]")) {
                try {
                    int c = 0;
                    JSONObject obj1 = new JSONObject(result);
                    String success = obj1.getString("success");
                    String message = obj1.getString("message");
                    if (success.equalsIgnoreCase("1")) {
                        startActivity(new Intent(context, MainDrawer_Activity.class));
                        finish();
                    } else {
                        Utilities.showAlertDialog(context, "Server Not Responding", "Please Try After Sometime", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
