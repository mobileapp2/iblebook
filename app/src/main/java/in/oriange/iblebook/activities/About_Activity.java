package in.oriange.iblebook.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import in.oriange.iblebook.R;

public class About_Activity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView msgText = findViewById(R.id.text_msg);
        TextView textContact = findViewById(R.id.text_contact);
        TextView textMobile = findViewById(R.id.text_mobile);
        setupToolbar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textMobile.setText(Html.fromHtml("&nbsp; 9373210481", Html.FROM_HTML_MODE_COMPACT));
            textContact.setText(Html.fromHtml("<h2>Contact Us :</h2><br><p>302-B,Sai Sadan,\n" +
                    "76-78, Modi Street, Fort,\n" +
                    "Mumbai-400001</p>", Html.FROM_HTML_MODE_COMPACT));
            msgText.setText(Html.fromHtml("<p>IBLE BOOK is a one stop secure solution to all problems related to saving, sharing and requesting Address, Taxation & Bank information.Using IBLE BOOK will help you deal with copious amounts of data thus enabling you to function more efficiently and save valuable time!</p>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            textMobile.setText(Html.fromHtml("     9373210481"));
            textContact.setText(Html.fromHtml("<h2>Contact Us :</h2><br><p>302-B,Sai Sadan,\n" +
                    "76-78, Modi Street, Fort,\n" +
                    "Mumbai-400001</p>"));
            msgText.setText(Html.fromHtml("<p>IBLE BOOK is a one stop secure solution to all problems related to saving, sharing and requesting Address, Taxation & Bank information.Using IBLE BOOK will help you deal with copious amounts of data thus enabling you to function more efficiently and save valuable time!</p>"));
        }
    }

    protected void setupToolbar() {
        getSupportActionBar().hide();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("About Us");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
