package in.oriange.iblebook.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.iblebook.R;
import in.oriange.iblebook.models.FAQImageList;
import in.oriange.iblebook.models.ParamsPojo;
import in.oriange.iblebook.utilities.ApplicationConstants;
import in.oriange.iblebook.utilities.PicassoImageLoadingService;
import in.oriange.iblebook.utilities.WebServiceCalls;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.event.OnSlideChangeListener;
import ss.com.bannerslider.indicators.IndicatorShape;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class FAQNew_Activity extends Activity {

    private Context context;
    private Slider banner_slider;
    private ArrayList<FAQImageList> imageList;
    private Button btn_next, btn_back;
    private ProgressDialog pd;

    int currentPosition;

    private TextView[] dots;
    private LinearLayout ll_dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqnew);

        init();
        setDefaults();
        setEventHandler();
//        setupToolbar();
    }

    private void init() {
        context = FAQNew_Activity.this;
        pd = new ProgressDialog(context);
        banner_slider = findViewById(R.id.banner_slider);
        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);
        ll_dots = findViewById(R.id.ll_dots);

        imageList = new ArrayList<>();

    }

    private void setDefaults() {
        new GetFAQImageList().execute();

    }

    private void setEventHandler() {
        banner_slider.setSlideChangeListener(new OnSlideChangeListener() {
            @Override
            public void onSlideChange(int position) {
                currentPosition = position;
                addBottomDots(currentPosition);
                if (currentPosition == imageList.size() - 1) {
                    btn_next.setText("FINISH");
                } else {
                    btn_next.setText("NEXT");
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPosition != imageList.size() - 1) {
                    currentPosition = currentPosition + 1;
                    banner_slider.setSelectedSlide(currentPosition);
                    addBottomDots(currentPosition);
                    if (currentPosition == imageList.size() - 1) {
                        btn_next.setText("FINISH");
                    }
                } else {
                    finish();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPosition != 0) {
                    currentPosition = currentPosition - 1;
                    banner_slider.setSelectedSlide(currentPosition);
                    addBottomDots(currentPosition);
                }
            }
        });
    }

    public class GetFAQImageList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getfaq"));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.FAQAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            try {
                if (!result.equals("")) {
                    JSONArray jsonarr = new JSONArray(result);
                    imageList = new ArrayList<>();
                    if (jsonarr.length() > 0) {
                        for (int i = 0; i < jsonarr.length(); i++) {
                            FAQImageList summary = new FAQImageList();
                            JSONObject jsonObj = jsonarr.getJSONObject(i);
                            summary.setFaqImageUrl("https://iblebook.com/admin/images/faq/" + jsonObj.getString("faqImageUrl"));
                            imageList.add(summary);
                        }

                        Slider.init(new PicassoImageLoadingService(FAQNew_Activity.this));
                        setupViews();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupViews() {
        banner_slider.setAdapter(new MainSliderAdapter());
        banner_slider.setIndicatorStyle(IndicatorShape.DASH);
        banner_slider.setSelectedSlide(0);
        banner_slider.hideIndicators();
        addBottomDots(0);

    }

    public class MainSliderAdapter extends SliderAdapter {

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        @Override
        public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
            viewHolder.bindImageSlide(imageList.get(position).getFaqImageUrl());
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[imageList.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(45);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
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
