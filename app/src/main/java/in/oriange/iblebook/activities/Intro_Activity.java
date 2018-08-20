package in.oriange.iblebook.activities;

import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import in.oriange.iblebook.R;
import in.oriange.iblebook.utilities.UserSessionManager;

public class Intro_Activity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        UserSessionManager session = new UserSessionManager(Intro_Activity.this);
        session.updateAppOpen("1");

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryLight)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.img_office)
                .title("Organize your time with us")
                .description("Would you try?")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryLight)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.img_office)
                .title("Organize your time with us")
                .description("Would you try?")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryLight)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.img_office)
                .title("Organize your time with us")
                .description("Would you try?")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        finish();
//        startActivity(new Intent(this, Login_Activity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}