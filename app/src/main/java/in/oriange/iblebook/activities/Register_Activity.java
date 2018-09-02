package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.Utilities;
import in.oriange.iblebook.utilities.WebServiceCalls;

import static in.oriange.iblebook.utilities.Utilities.hideSoftKeyboard;

public class Register_Activity extends Activity {
    private Context context;
    private LinearLayout ll_parent;
    private EditText edt_name, edt_aliasname, edt_mobile, edt_email, edt_password, edt_conformpassword;
    private TextInputLayout input_name, input_mobile, input_email, input_password, input_conformpassword;
    private TextView tv_alreadyregister;
    private Button btn_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        setEventHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(Register_Activity.this);
    }

    private void init() {
        context = Register_Activity.this;

        ll_parent = findViewById(R.id.ll_parent);

        edt_name = findViewById(R.id.edt_name);
        edt_aliasname = findViewById(R.id.edt_aliasname);
        edt_email = findViewById(R.id.edt_email);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_password = findViewById(R.id.edt_password);
        edt_conformpassword = findViewById(R.id.edt_conformpassword);

        input_name = findViewById(R.id.input_name);
        input_mobile = findViewById(R.id.input_mobile);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_conformpassword = findViewById(R.id.input_conformpassword);

        tv_alreadyregister = findViewById(R.id.tv_alreadyregister);
        btn_Register = findViewById(R.id.btn_Register);
    }

    private void setEventHandler() {
        tv_alreadyregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();

            }
        });

//        edt_name.addTextChangedListener(new MyTextWatcher(edt_name));
//        edt_mobile.addTextChangedListener(new MyTextWatcher(edt_mobile));
//        edt_email.addTextChangedListener(new MyTextWatcher(edt_email));
//        edt_password.addTextChangedListener(new MyTextWatcher(edt_password));
//        edt_conformpassword.addTextChangedListener(new MyTextWatcher(edt_conformpassword));
    }

    private void submitData() {
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
        if (edt_password.getText().toString().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Password");
            return;
        }
        if (edt_conformpassword.getText().toString().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Confirm Password");
            return;
        }
        if (!edt_password.getText().toString().equals(edt_conformpassword.getText().toString())) {
            Utilities.showSnackBar(ll_parent, "Passwords Did Not Match");
            return;
        }

        if (Utilities.isNetworkAvailable(context)) {
            new SendOTP().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
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
                    if (Utilities.isNetworkAvailable(context)) {
                        new RegisterNewUser().execute();
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
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
                obj.put("type", "sendOTP");
                obj.put("mobile", edt_mobile.getText().toString().trim());
                obj.put("email", edt_email.getText().toString().trim());
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

    public class RegisterNewUser extends AsyncTask<String, Void, String> {

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
                obj.put("type", "AddUser");
                obj.put("name", edt_name.getText().toString().trim());
                obj.put("alias", edt_aliasname.getText().toString().trim());
                obj.put("country_code", "+91");
                obj.put("mobile", edt_mobile.getText().toString().trim());
                obj.put("email", edt_email.getText().toString().trim());
                obj.put("password", edt_password.getText().toString().trim());
                obj.put("photo", "photo.png");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(message);
                        builder.setTitle("Success");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
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
