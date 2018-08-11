package in.oriange.iblebook.activities;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.My_Bank_Fragment;
import in.oriange.iblebook.fragments.Offline_Bank_Fragment;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.WebServiceCalls;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class View_Bank_Activity extends Activity {
    private Context context;
    private String user_id, document, bank_id, name, NETSTAT;
    private EditText edt_name, edt_alias, edt_bank_name, edt_ifsc, edt_account_no;
    private TextView tv_attachfile;
    private LinearLayout ll_parent;
    private File downloadedDocsfolder, file;
    private FloatingActionButton fab_share, fab_edit, fab_delete;
    private UserSessionManager session;
    private int position;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bank);

        init();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
        setupToolbar();
    }

    private void init() {
        context = View_Bank_Activity.this;
        session = new UserSessionManager(context);
        dbHelper = new DataBaseHelper(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_bank_name = findViewById(R.id.edt_bank_name);
        edt_ifsc = findViewById(R.id.edt_ifsc);
        edt_account_no = findViewById(R.id.edt_account_no);
        tv_attachfile = findViewById(R.id.tv_attachfile);

        fab_share = findViewById(R.id.fab_share);
        fab_edit = findViewById(R.id.fab_edit);
        fab_delete = findViewById(R.id.fab_delete);

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "Bank Documents");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

    }

    private void getSessionData() {
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            name = json.getString("name");
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
        NETSTAT = getIntent().getStringExtra("NETSTAT");

        edt_name.setText(getIntent().getStringExtra("account_holder_name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_bank_name.setText(getIntent().getStringExtra("bank_name"));
        edt_ifsc.setText(getIntent().getStringExtra("ifsc_code"));
        edt_account_no.setText(getIntent().getStringExtra("account_no"));
    }

    private void setDefaults() {
        if (NETSTAT.equals("OFFLINE")) {
            tv_attachfile.setText("View Document");
        }
    }

    private void setEventHandler() {
        edt_name.setLongClickable(false);
        edt_alias.setLongClickable(false);
        edt_bank_name.setLongClickable(false);
        edt_ifsc.setLongClickable(false);
        edt_account_no.setLongClickable(false);

        tv_attachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NETSTAT.equals("OFFLINE")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(document);
                    Uri uri = Uri.parse("file://" + file);
                    if (document.toString().contains(".doc") || document.toString().contains(".docx")) {
                        // Word document
                        intent.setDataAndType(uri, "application/msword");
                    } else if (document.toString().contains(".pdf")) {
                        // PDF file
                        intent.setDataAndType(uri, "application/pdf");
                    } else if (document.toString().contains(".ppt") || document.toString().contains(".pptx")) {
                        // Powerpoint file
                        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    } else if (document.toString().contains(".xls") || document.toString().contains(".xlsx")) {
                        // Excel file
                        intent.setDataAndType(uri, "application/vnd.ms-excel");
                    } else if (document.toString().contains(".zip") || document.toString().contains(".rar")) {
                        // WAV audio file
                        intent.setDataAndType(uri, "application/x-wav");
                    } else if (document.toString().contains(".rtf")) {
                        // RTF file
                        intent.setDataAndType(uri, "application/rtf");
                    } else if (document.toString().contains(".wav") || document.toString().contains(".mp3")) {
                        // WAV audio file
                        intent.setDataAndType(uri, "audio/x-wav");
                    } else if (document.toString().contains(".gif")) {
                        // GIF file
                        intent.setDataAndType(uri, "image/gif");
                    } else if (document.toString().contains(".jpg") || document.toString().contains(".jpeg") || document.toString().contains(".png")) {
                        // JPG file
                        intent.setDataAndType(uri, "image/jpeg");
                    } else if (document.toString().contains(".txt")) {
                        // Text file
                        intent.setDataAndType(uri, "text/plain");
                    } else if (document.toString().contains(".3gp") || document.toString().contains(".mpg") || document.toString().contains(".mpeg") || document.toString().contains(".mpe") || document.toString().contains(".mp4") || document.toString().contains(".avi")) {
                        // Video files
                        intent.setDataAndType(uri, "video/*");
                    } else {
                        intent.setDataAndType(uri, "*/*");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } else {
                    new DownloadDocument().execute(document);
                }
            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectionFilter();
            }
        });

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(context, Edit_Bank_Activity.class);
                intent.putExtra("position", position);
                intent.putExtra("bank_id", bank_id);
                intent.putExtra("account_holder_name", getIntent().getStringExtra("account_holder_name"));
                intent.putExtra("alias", getIntent().getStringExtra("alias"));
                intent.putExtra("bank_name", getIntent().getStringExtra("bank_name"));
                intent.putExtra("ifsc_code", getIntent().getStringExtra("ifsc_code"));
                intent.putExtra("account_no", getIntent().getStringExtra("account_no"));
                intent.putExtra("document", document);
                intent.putExtra("created_by", user_id);
                intent.putExtra("updated_by", user_id);
                intent.putExtra("NETSTAT", NETSTAT);
                context.startActivity(intent);
            }
        });

        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (NETSTAT.equals("ONLINE")) {
                            new DeleteBankDetails().execute();
                        } else {
                            long result = dbHelper.deleteBankDetailsFromDb(bank_id);
                            if (result != -1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Bank Details Deleted Successfully");
                                builder.setTitle("Success");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Offline_Bank_Fragment.setDefault();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
    }

    private void setSelectionFilter() {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompt_sharebank, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        TextView tv_name = promptView.findViewById(R.id.tv_name);
        TextView tv_bankname = promptView.findViewById(R.id.tv_bankname);
        TextView tv_ifsccode = promptView.findViewById(R.id.tv_ifsccode);
        TextView tv_accno = promptView.findViewById(R.id.tv_accno);
        TextView tv_file = promptView.findViewById(R.id.tv_file);

        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_bankname = promptView.findViewById(R.id.cb_bankname);
        final CheckBox cb_ifsccode = promptView.findViewById(R.id.cb_ifsccode);
        final CheckBox cb_accno = promptView.findViewById(R.id.cb_accno);
        final CheckBox cb_file = promptView.findViewById(R.id.cb_file);

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_name.isChecked())
                    cb_name.setChecked(true);
                else
                    cb_name.setChecked(false);
            }
        });
        tv_bankname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_bankname.isChecked())
                    cb_bankname.setChecked(true);
                else
                    cb_bankname.setChecked(false);
            }
        });
        tv_ifsccode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_ifsccode.isChecked())
                    cb_ifsccode.setChecked(true);
                else
                    cb_ifsccode.setChecked(false);
            }
        });
        tv_accno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_accno.isChecked())
                    cb_accno.setChecked(true);
                else
                    cb_accno.setChecked(false);
            }
        });
        tv_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_file.isChecked())
                    cb_file.setChecked(true);
                else
                    cb_file.setChecked(false);
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();
                if (cb_name.isChecked()) {
                    sb.append("Name of Account Holder - " + edt_name.getText().toString().trim() + "\n");
                }
                if (cb_bankname.isChecked()) {
                    sb.append("Bank Name - " + edt_bank_name.getText().toString().trim() + "\n");
                }
                if (cb_ifsccode.isChecked()) {
                    sb.append("IFSC Code - " + edt_ifsc.getText().toString().trim() + "\n");
                }
                if (cb_accno.isChecked()) {
                    sb.append("Account Number - " + edt_account_no.getText().toString().trim() + "\n");
                }
                if (cb_file.isChecked()) {
                    document = document.replaceAll(" ", "%20");
                    sb.append("File Url - " + document + "\n");
                }

                if (!cb_name.isChecked() && !cb_bankname.isChecked() && !cb_ifsccode.isChecked()
                        && !cb_accno.isChecked() && !cb_file.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedBankDetails", sb.toString());
                String finalDataShare = name + " shares bank details with you " + "\n" + sb.toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, finalDataShare);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int view) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.setCancelable(false);
        android.support.v7.app.AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();

    }

    public class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        private ProgressDialog mProgressDialog;
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("Downloading Document");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedDocsfolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            mProgressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file);
                if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (downloadurl.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (downloadurl.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (downloadurl.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (downloadurl.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }

    public class DeleteBankDetails extends AsyncTask<String, Void, String> {
        int position;
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
            position = Integer.parseInt(params[0]);
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "DeleteData");
            obj.addProperty("bank_id", bank_id);
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
                        builder.setMessage("Bank Details Deleted Successfully");
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

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Bank Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
