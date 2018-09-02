package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class EditProfile_Activity extends Activity {

    private Context context;
    private UserSessionManager session;
    private EditText edt_name, edt_aliasname, edt_mobile, edt_email, edt_password;
    private String user_id, photo, name, alias, country_code, mobile, email, password;
    private ProgressDialog pd;
    private LinearLayout ll_parent;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
        getSessionData();
        setDefaults();
        setEventHandler();
        setupToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(EditProfile_Activity.this);
    }

    private void init() {
        context = EditProfile_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);

        edt_name = findViewById(R.id.edt_name);
        edt_aliasname = findViewById(R.id.edt_aliasname);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_save = findViewById(R.id.btn_save);

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
        edt_name.setText(name);
        edt_aliasname.setText(alias);
        edt_mobile.setText(/*country_code + "" + */mobile);
        edt_email.setText(email);
    }

    private void setEventHandler() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().trim().equals("")) {
                    Utilities.showSnackBar(ll_parent, "Please Enter Name");
                    return;
                }
//        if (edt_aliasname.getText().toString().trim().equals("")) {
//            Utilities.showSnackBar(ll_parent, "Please Alias Enter Name");
//            return;
//        }
                if (!Utilities.isMobileNo(edt_mobile)) {
                    Utilities.showSnackBar(ll_parent, "Please Enter Valid Mobile Number");
                    return;
                }
                if (!edt_email.getText().toString().trim().equals("")) {
                    if (!Utilities.isEmailValid(edt_email)) {
                        Utilities.showSnackBar(ll_parent, "Please Enter Valid Email Address");
                        return;
                    }
                }


                if (Utilities.isNetworkAvailable(context)) {
                    new UpdateProfileData().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });

        edt_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordAlert();
            }
        });
    }

    private void changePasswordAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_changepassword, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Change Password");
        alertDialogBuilder.setView(promptView);

        final EditText edt_oldpassword = promptView.findViewById(R.id.edt_oldpassword);
        final EditText edt_enterpassword = promptView.findViewById(R.id.edt_enterpassword);
        final EditText edt_confirmpassword = promptView.findViewById(R.id.edt_confirmpassword);

        edt_oldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_oldpassword.getText().toString().trim().equals(password)) {
                    edt_oldpassword.setError("Enter correct password");
                } else {
                    edt_oldpassword.setError(null);
                }
            }
        });

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
                if (!edt_oldpassword.getText().toString().trim().equals(password)) {
                    Utilities.showMessageString(context, "Enter Correct Old Password");
                    return;
                }
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

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Edit Profile");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                obj.put("name", edt_name.getText().toString().trim());
                obj.put("alias", edt_aliasname.getText().toString().trim());
                obj.put("country_code", "+91");
                obj.put("mobile", edt_mobile.getText().toString().trim());
                obj.put("email", edt_email.getText().toString().trim());
                obj.put("password", password);
                obj.put("photo", photo);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Profile Updated Successfully");
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
                obj.put("type", "Update");
                obj.put("user_id", user_id);
                obj.put("name", edt_name.getText().toString().trim());
                obj.put("alias", edt_aliasname.getText().toString().trim());
                obj.put("country_code", "+91");
                obj.put("mobile", edt_mobile.getText().toString().trim());
                obj.put("email", edt_email.getText().toString().trim());
                obj.put("password", params[0]);
                obj.put("photo", photo);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Password Changed Successfully");
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
