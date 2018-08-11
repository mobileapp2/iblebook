package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;
import in.oriange.iblebook.utilities.PermissionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login_Activity extends Activity {

    private Context context;
    private LinearLayout ll_parent;
    private EditText edt_username, edt_password;
    private TextInputLayout input_username, input_password;
    private TextView tv_forgotpass, tv_register;
    private Button btn_login;
    private int i = 0;
    private UserSessionManager session;


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
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        tv_forgotpass = findViewById(R.id.tv_forgotpass);
        tv_register = findViewById(R.id.tv_register);
        btn_login = findViewById(R.id.btn_login);
    }

    private void setEventHandler() {
        tv_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showSnackBar(ll_parent, "Comming Soon");
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Register_Activity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();

            }
        });

//        edt_username.addTextChangedListener(new MyTextWatcher(edt_username));
//        edt_password.addTextChangedListener(new MyTextWatcher(edt_password));
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
                                finish();
                                startActivity(new Intent(context, MainDrawer_Activity.class));
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

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edt_username:
                    input_username.setError(null);
                    break;
                case R.id.edt_password:
                    input_password.setError(null);
                    break;
            }
        }
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
}
