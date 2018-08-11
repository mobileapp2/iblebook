package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.oriange.iblebook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class View_Address_Activity extends Activity {
    private Context context;
    //    private TextView tv_website, tv_email, tv_viewonmap;
    private LinearLayout ll_parent;
    private TextView tv_visitcard, tv_attachphoto;
    private EditText edt_addresstype, edt_name, edt_alias, edt_address, edt_country,
            edt_state, edt_district, edt_pincode, edt_mobile, edt_email, edt_website;
    private ProgressDialog pd;
    private String photo, visiting_card;
    private File downloadedDocsfolder, file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);

        init();
        getIntentData();
        setEventHandler();
        setupToolbar();
    }

    private void init() {
        context = View_Address_Activity.this;
//        tv_website = findViewById(R.id.tv_website);
//        tv_email = findViewById(R.id.tv_email);
//        tv_viewonmap = findViewById(R.id.tv_viewonmap);
        pd = new ProgressDialog(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_addresstype = findViewById(R.id.edt_addresstype);
        tv_visitcard = findViewById(R.id.tv_visitcard);
        tv_attachphoto = findViewById(R.id.tv_attachphoto);
        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_address = findViewById(R.id.edt_address);
        edt_country = findViewById(R.id.edt_country);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_website = findViewById(R.id.edt_website);

        downloadedDocsfolder = new File(Environment.getExternalStorageDirectory() + "/Address Book/" + "Address Documents");
        if (!downloadedDocsfolder.exists())
            downloadedDocsfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    private void getIntentData() {
        photo = getIntent().getStringExtra("photo");
        visiting_card = getIntent().getStringExtra("visiting_card");
        edt_name.setText(getIntent().getStringExtra("name"));
        edt_alias.setText(getIntent().getStringExtra("alias"));
        edt_address.setText(getIntent().getStringExtra("address_line_one"));
        edt_country.setText(getIntent().getStringExtra("country"));
        edt_state.setText(getIntent().getStringExtra("state"));
        edt_district.setText(getIntent().getStringExtra("district"));
        edt_pincode.setText(getIntent().getStringExtra("pincode"));
//        edt_mobile.setText(getIntent().getStringExtra(""));
        edt_mobile.setText("8149115089");
        edt_email.setText(getIntent().getStringExtra("email_id"));
        edt_website.setText(getIntent().getStringExtra("website"));
    }

    private void setEventHandler() {
//        tv_website.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tv_website.getText().toString().trim()));
//                startActivity(browserIntent);
//            }
//        });
//
//        tv_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
//                        Uri.parse("mailto:" + tv_email.getText().toString()));
//                startActivity(emailIntent);
//            }
//        });
//        tv_viewonmap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, View_Location_Map_Activity.class));
//            }
//        });

        edt_addresstype.setLongClickable(false);
        edt_name.setLongClickable(false);
        edt_alias.setLongClickable(false);
        edt_address.setLongClickable(false);
        edt_country.setLongClickable(false);
        edt_state.setLongClickable(false);
        edt_district.setLongClickable(false);
        edt_pincode.setLongClickable(false);
        edt_mobile.setLongClickable(false);
        edt_email.setLongClickable(false);
        edt_website.setLongClickable(false);

        tv_visitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadDocument().execute(visiting_card);
            }
        });
        tv_attachphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadDocument().execute(photo);
            }
        });
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

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Address Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
