package com.neteru.afrikett.ui.activities.showcase_activities.overview.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings.ShowcaseAdminSettingsActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings.ShowcaseInfoSettingsActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings.ShowcaseVisualSettingsActivity;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceGroup;

public class ShowcaseSettingsActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new showcasePreferenceFragment()).commit();

        String showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(showcasePrimaryColor));
        }

        if (getSupportActionBar() != null){
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.settings));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    public static class showcasePreferenceFragment extends PreferenceFragment
    {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.showcase_preferences);

            final String id = getActivity().getIntent().getStringExtra("showcaseId");
            final String color = getActivity().getIntent().getStringExtra("showcasePrimaryColor");

            findPreference("visual").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(getActivity(), ShowcaseVisualSettingsActivity.class)
                                        .putExtra("showcaseId", id)
                                        .putExtra("showcasePrimaryColor", color));
                getActivity().overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("info").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(getActivity(), ShowcaseInfoSettingsActivity.class)
                                        .putExtra("showcaseId", id)
                                        .putExtra("showcasePrimaryColor", color));
                getActivity().overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("admin").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(getActivity(), ShowcaseAdminSettingsActivity.class)
                                        .putExtra("showcaseId", id)
                                        .putExtra("showcasePrimaryColor", color));
                getActivity().overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            tintIcons(getPreferenceScreen(), Color.parseColor(getActivity().getIntent().getStringExtra("showcasePrimaryColor")));
        }

        private static void tintIcons(Preference preference, int color) {
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup group = ((PreferenceGroup) preference);
                for (int i = 0; i < group.getPreferenceCount(); i++) {
                    tintIcons(group.getPreference(i), color);
                }
            } else {
                Drawable icon = preference.getIcon();
                if (icon != null) {
                    icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        }

        @Override
        public void onAttach(Context ctx) {
            super.onAttach(ctx);
        }
    }

}
