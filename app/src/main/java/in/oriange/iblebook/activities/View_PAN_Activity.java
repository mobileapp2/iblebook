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

import in.oriange.iblebook.R;
import in.oriange.iblebook.fragments.My_PAN_Fragment;
import in.oriange.iblebook.fragments.Offline_PAN_Fragment;
import in.oriange.iblebook.fragments.Received_PAN_Fragment;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.DataBaseHelper;
import in.oriange.iblebook.utilities.UserSessionManager;
import in.oriange.iblebook.utilities.WebServiceCalls;

public class View_PAN_Activity extends Activity {

    private Context context;
    private String name, user_id, pan_document, tax_id, STATUS;
    private EditText edt_name, edt_alias, edt_pan_no;
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
        setContentView(R.layout.activity_view_pan);

        init();
        getSessionData();
        getIntentData();
        setDefaults();
        setEventHandler();
        setupToolbar();

    }

    private void init() {
        context = View_PAN_Activity.this;
        session = new UserSessionManager(context);
        dbHelper = new DataBaseHelper(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_pan_no = findViewById(R.id.edt_pan_no);
        tv_attachfile = findViewById(R.id.tv_attachfile);

        fab_share = findViewById(R.id.fab_share);
        fab_edit = findViewById(R.id.fab_edit);
        fab_delete = findViewById(R.id.fab_delete);

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "PAN Documents");
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
        tax_id = getIntent().getStringExtra("tax_id");
        pan_document = getIntent().getStringExtra("pan_document");
        STATUS = getIntent().getStringExtra("STATUS");

        edt_name.setText(getIntent().getStringExtra("name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_pan_no.setText(getIntent().getStringExtra("pan_number"));
    }

    private void setDefaults() {
        if (pan_document.equals("")) {
            tv_attachfile.setText("No Document Attached");
            tv_attachfile.setFocusable(false);
            tv_attachfile.setClickable(false);
        }
    }

    private void setEventHandler() {
        edt_name.setLongClickable(false);
        edt_alias.setLongClickable(false);
        edt_pan_no.setLongClickable(false);

        tv_attachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (STATUS.equals("OFFLINE")) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    File file = new File(pan_document);
//                    Uri uri = Uri.parse("file://" + file);
//                    if (pan_document.toString().contains(".doc") || pan_document.toString().contains(".docx")) {
//                        // Word document
//                        intent.setDataAndType(uri, "application/msword");
//                    } else if (pan_document.toString().contains(".pdf")) {
//                        // PDF file
//                        intent.setDataAndType(uri, "application/pdf");
//                    } else if (pan_document.toString().contains(".ppt") || pan_document.toString().contains(".pptx")) {
//                        // Powerpoint file
//                        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//                    } else if (pan_document.toString().contains(".xls") || pan_document.toString().contains(".xlsx")) {
//                        // Excel file
//                        intent.setDataAndType(uri, "application/vnd.ms-excel");
//                    } else if (pan_document.toString().contains(".zip") || pan_document.toString().contains(".rar")) {
//                        // WAV audio file
//                        intent.setDataAndType(uri, "application/x-wav");
//                    } else if (pan_document.toString().contains(".rtf")) {
//                        // RTF file
//                        intent.setDataAndType(uri, "application/rtf");
//                    } else if (pan_document.toString().contains(".wav") || pan_document.toString().contains(".mp3")) {
//                        // WAV audio file
//                        intent.setDataAndType(uri, "audio/x-wav");
//                    } else if (pan_document.toString().contains(".gif")) {
//                        // GIF file
//                        intent.setDataAndType(uri, "image/gif");
//                    } else if (pan_document.toString().contains(".jpg") || pan_document.toString().contains(".jpeg") || pan_document.toString().contains(".png")) {
//                        // JPG file
//                        intent.setDataAndType(uri, "image/jpeg");
//                    } else if (pan_document.toString().contains(".txt")) {
//                        // Text file
//                        intent.setDataAndType(uri, "text/plain");
//                    } else if (pan_document.toString().contains(".3gp") || pan_document.toString().contains(".mpg") || pan_document.toString().contains(".mpeg") || pan_document.toString().contains(".mpe") || pan_document.toString().contains(".mp4") || pan_document.toString().contains(".avi")) {
//                        // Video files
//                        intent.setDataAndType(uri, "video/*");
//                    } else {
//                        intent.setDataAndType(uri, "*/*");
//                    }
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//
//                } else {
                new DownloadDocument().execute(pan_document);
//                }
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
                Intent intent = new Intent(context, Edit_PAN_Activity.class);
                intent.putExtra("tax_id", tax_id);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("alias", getIntent().getStringExtra("alias"));
                intent.putExtra("pan_number", getIntent().getStringExtra("pan_number"));
                intent.putExtra("pan_document", pan_document);
                intent.putExtra("created_by", user_id);
                intent.putExtra("updated_by", user_id);
                intent.putExtra("STATUS", STATUS);
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
//                        if (STATUS.equals("ONLINE")) {
//                            new DeletePANDetails().execute();
//                        } else {
//                            long result = dbHelper.deleteTaxDetailsFromDb(tax_id);
//                            if (result != -1) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                builder.setMessage("PAN Details Deleted Successfully");
//                                builder.setTitle("Success");
//                                builder.setCancelable(false);
//                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        finish();
//                                        Offline_PAN_Fragment.setDefault();
//                                    }
//                                });
//                                builder.show();
//                            }
//                        }
                        new DeletePANDetails().execute();
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
        View promptView = layoutInflater.inflate(R.layout.prompt_sharepan, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Share Filter");

        final CheckBox cb_name = promptView.findViewById(R.id.cb_name);
        final CheckBox cb_panno = promptView.findViewById(R.id.cb_panno);
        final CheckBox cb_file = promptView.findViewById(R.id.cb_file);

        if (edt_name.getText().toString().trim().equals("")) {
            cb_name.setVisibility(View.GONE);
            cb_name.setChecked(false);
        } else {
            cb_name.setVisibility(View.VISIBLE);
        }

        if (edt_pan_no.getText().toString().trim().equals("")) {
            cb_panno.setVisibility(View.GONE);
            cb_panno.setChecked(false);
        } else {
            cb_panno.setVisibility(View.VISIBLE);
        }

        if (pan_document.equals("")) {
            cb_file.setVisibility(View.GONE);
            cb_file.setChecked(false);
        } else {
            cb_file.setVisibility(View.VISIBLE);
        }

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialod, int id) {
                StringBuilder sb = new StringBuilder();
                if (cb_name.isChecked()) {
                    sb.append("Name - " + edt_name.getText().toString().trim() + "\n");
                }
                if (cb_panno.isChecked()) {
                    sb.append("PAN - " + edt_pan_no.getText().toString().trim() + "\n");
                }
                if (cb_file.isChecked()) {
                    pan_document = pan_document.replaceAll(" ", "%20");
                    sb.append("File - " + pan_document + "\n");
                }

                if (!cb_name.isChecked() && !cb_panno.isChecked() && !cb_file.isChecked()) {
                    Toast.makeText(context, "None of the above was selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("SharedPANDetails", sb.toString());
                String finalDataShare = name + " shares PAN details with you " + "\n" + sb.toString();
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
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();

    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("PAN Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog mProgressDialog;

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

    public class DeletePANDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "DeleteData");
            obj.addProperty("tax_id", tax_id);
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
                        builder.setMessage("PAN Details Deleted Successfully");
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
