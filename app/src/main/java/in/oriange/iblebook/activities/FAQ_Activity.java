package in.oriange.iblebook.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import in.oriange.iblebook.R;

public class FAQ_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        setupToolbar();
        TextView textFAQ = findViewById(R.id.text_faq);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textFAQ.setText(Html.fromHtml("<p><span style=\"font-size: 12pt;\"><strong>How to set my own address?</strong></span></p>\n" +
                    "<p><br />Go to Address tab &ndash; You will be under My Address<br />Click on the + sign to add<br />You will find options to inputs as your name, address etc. Fill in the essential details.<br />To use current GPS location select <strong>Detect my Current Location or Set the exact GPS location</strong> by using Google maps. It will facilitate navigation. Note that <strong>Pin code is compulsory</strong>.<br />For Eg. To fill the Home Address you can choose individual name &amp; for office or factory address you can write trade Name, Short Name. It is short name displayed of the address.<br />GPS Location feature is available <br />Features like add visiting card and add photo are available and can be added. <br />Click Save the details and Your address will be displayed on my address tab</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>How to add PAN/GST/Bank Details?</strong></span></p>\n" +
                    "<p>Go to the required tab<br />Click on the + sign to add your PAN/GST/Bank <br />Fill the required details and save</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>How to send Address|PAN|GST|Bank details?</strong></span></p>\n" +
                    "<p>Go to my Address|PAN|GST|Bank<br />Click on three dots and select share option <br />Select the required field to be shared and click OK <br />Now can now send it by SMS|Whats App|Email</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>How to request Address|PAN|GST/Bank from others?</strong></span></p>\n" +
                    "<p>Go to Contact <br />Select the contact name from whom you want to receive the said information, <br />Fill the required details and click ok. <br />If the selected contact is in Iblebook then request will be shown in the request section and if he is not an iblebook user then he will receive request by sms.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What if the person is not in your contact list?</strong></span></p>\n" +
                    "<p>Go to contact click <br />Click on +sign to add<br />Fill the required mobile number details and click OK.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What to do after request for Address/PAN/GST/Bank details is received via iblebook?</strong></span></p>\n" +
                    "<p>Go to requests<br />Select the received request and click on Share or Dismiss. <br />If you select share, it will go to the respective section and you will select the respective details to be shared by clicking ok. <br />The requested details can be shared via Whatsapp, SMS or email.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What to do after Address/PAN/GST/Bank details are received via iblebook?</strong></span></p>\n" +
                    "<p>Go to request <br />In Notifications section, click details received and click accept or reject. <br />If you accept then the details are automatically saved to receive section.For Eg. If you have received address it will be saved to Address in Received section.<br />Select the details received. <br />You can either &ndash; Select the request click on share or dismiss request and select the required details to be send and click to save.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What if the details are received other than iblebook?</strong></span></p>\n" +
                    "<p>Go to Address|PAN|GST/Bank section <br />Click on +sign to add<br />Fill the required details and click save. <br />Your details are saved in Address|PAN|GST|Bank Section</p>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            textFAQ.setText(Html.fromHtml("<p><span style=\"font-size: 12pt;\"><strong>How to set my own address?</strong></span></p>\n" +
                    "<p><br />Go to Address tab &ndash; You will be under My Address<br />Click on the + sign to add<br />You will find options to inputs as your name, address etc. Fill in the essential details.<br />To use current GPS location select <strong>Detect my Current Location or Set the exact GPS location</strong> by using Google maps. It will facilitate navigation. Note that <strong>Pin code is compulsory</strong>.<br />For Eg. To fill the Home Address you can choose individual name &amp; for office or factory address you can write trade Name, Short Name. It is short name displayed of the address.<br />GPS Location feature is available <br />Features like add visiting card and add photo are available and can be added. <br />Click Save the details and Your address will be displayed on my address tab</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>How to add PAN/GST/Bank Details?</strong></span></p>\n" +
                    "<p>Go to the required tab<br />Click on the + sign to add your PAN/GST/Bank <br />Fill the required details and save</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>How to send Address|PAN|GST|Bank details?</strong></span></p>\n" +
                    "<p>Go to my Address|PAN|GST|Bank<br />Click on three dots and select share option <br />Select the required field to be shared and click OK <br />Now can now send it by SMS|Whats App|Email</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>How to request Address|PAN|GST/Bank from others?</strong></span></p>\n" +
                    "<p>Go to Contact <br />Select the contact name from whom you want to receive the said information, <br />Fill the required details and click ok. <br />If the selected contact is in Iblebook then request will be shown in the request section and if he is not an iblebook user then he will receive request by sms.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What if the person is not in your contact list?</strong></span></p>\n" +
                    "<p>Go to contact click <br />Click on +sign to add<br />Fill the required mobile number details and click OK.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What to do after request for Address/PAN/GST/Bank details is received via iblebook?</strong></span></p>\n" +
                    "<p>Go to requests<br />Select the received request and click on Share or Dismiss. <br />If you select share, it will go to the respective section and you will select the respective details to be shared by clicking ok. <br />The requested details can be shared via Whatsapp, SMS or email.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What to do after Address/PAN/GST/Bank details are received via iblebook?</strong></span></p>\n" +
                    "<p>Go to request <br />In Notifications section, click details received and click accept or reject. <br />If you accept then the details are automatically saved to receive section.For Eg. If you have received address it will be saved to Address in Received section.<br />Select the details received. <br />You can either &ndash; Select the request click on share or dismiss request and select the required details to be send and click to save.</p>\n" +
                    "<p><br /><br /></p>\n" +
                    "<p><span style=\"font-size: 12pt;\"><strong>What if the details are received other than iblebook?</strong></span></p>\n" +
                    "<p>Go to Address|PAN|GST/Bank section <br />Click on +sign to add<br />Fill the required details and click save. <br />Your details are saved in Address|PAN|GST|Bank Section</p>"));
        }
    }

    protected void setupToolbar() {
        getSupportActionBar().hide();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("FAQ");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
