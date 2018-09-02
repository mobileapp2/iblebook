package in.oriange.iblebook.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import agency.tango.materialintroscreen.SlideFragment;
import in.oriange.iblebook.R;

public class IntroScreen4_Fragment extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro_screen4, container, false);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.intro_backgroung4;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimaryDark;
    }
}
