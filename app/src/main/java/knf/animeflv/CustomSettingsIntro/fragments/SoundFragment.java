package knf.animeflv.CustomSettingsIntro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import agency.tango.materialintroscreen.SlideFragment;
import butterknife.ButterKnife;
import knf.animeflv.R;

public class SoundFragment extends SlideFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sound_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public int backgroundColor() {
        return super.backgroundColor();
    }

    @Override
    public int buttonsColor() {
        return super.buttonsColor();
    }

    @Override
    public boolean canMoveFurther() {
        return super.canMoveFurther();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return super.cantMoveFurtherErrorMessage();
    }
}
