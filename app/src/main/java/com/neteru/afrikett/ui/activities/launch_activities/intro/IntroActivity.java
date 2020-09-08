package com.neteru.afrikett.ui.activities.launch_activities.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.ui.activities.launch_activities.start.phone.GetPhoneNumberActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.neteru.afrikett.core.utilities.AppUtilities.setFirstLaunchDone;

/**
 * Activité d'Intro
 */
public class IntroActivity extends AppIntro {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage_1, sliderPage_2, sliderPage_3, sliderPage_4;

        //Slider N°1
        sliderPage_1 = new SliderPage();
        sliderPage_1.setTitle(getString(R.string.slider_title_1));
        sliderPage_1.setDescription(getString(R.string.slider_summary_1));
        sliderPage_1.setImageDrawable(R.drawable.app_intro_1);
        sliderPage_1.setBgColor(getResources().getColor(R.color.skyblue));
        addSlide(AppIntroFragment.newInstance(sliderPage_1));

        //Slider N°2
        sliderPage_2 = new SliderPage();
        sliderPage_2.setTitle(getString(R.string.slider_title_2));
        sliderPage_2.setDescription(getString(R.string.slider_summary_2));
        sliderPage_2.setImageDrawable(R.drawable.app_intro_2);
        sliderPage_2.setBgColor(getResources().getColor(R.color.darkorange));
        addSlide(AppIntroFragment.newInstance(sliderPage_2));

        //Slider N°3
        sliderPage_3 = new SliderPage();
        sliderPage_3.setTitle(getString(R.string.slider_title_3));
        sliderPage_3.setDescription(getString(R.string.slider_summary_3));
        sliderPage_3.setImageDrawable(R.drawable.app_intro_3);
        sliderPage_3.setBgColor(getResources().getColor(R.color.skyblue));
        addSlide(AppIntroFragment.newInstance(sliderPage_3));

        //Slider N°4
        sliderPage_4 = new SliderPage();
        sliderPage_4.setTitle(getString(R.string.slider_title_4));
        sliderPage_4.setDescription(getString(R.string.slider_summary_4));
        sliderPage_4.setImageDrawable(R.drawable.app_intro_4);
        sliderPage_4.setBgColor(getResources().getColor(R.color.darkorange));
        addSlide(AppIntroFragment.newInstance(sliderPage_4));

        // Couleur du separateur
        setSeparatorColor(getResources().getColor(android.R.color.transparent));
        // Animation de transition
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Faire une action au click sur "Annuler"
        done();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Faire une action au click sur "Terminé"
        done();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Faire une action au changement de slides
    }

    /**
     * Continue vers la page de démarrage
     */
    public void done(){

        setFirstLaunchDone(this);
        startActivityForResult(new Intent(IntroActivity.this, GetPhoneNumberActivity.class), Constants.RANDOM_VALUE);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Non support de l'action Retour
        finish();
    }
}
