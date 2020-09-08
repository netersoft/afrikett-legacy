package com.neteru.afrikett.ui.activities.others_activities.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.ui.activities.launch_activities.splash.SplashActivity;
import com.neteru.afrikett.ui.activities.others_activities.AboutActivity;
import com.neteru.afrikett.ui.activities.others_activities.help.PrivacyPolicyActivity;
import com.neteru.afrikett.ui.activities.others_activities.help.ReportProblemActivity;
import com.neteru.afrikett.ui.activities.others_activities.settings.general.ChangeEmailActivity;
import com.neteru.afrikett.ui.activities.others_activities.settings.general.ChangePasswordActivity;
import com.neteru.afrikett.ui.activities.others_activities.settings.general.ChangePhoneNumberActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.PHONE;

public class GeneralSettingsActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new userPreferenceFragment()).commit();

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skyblue));
        }
    }

    public static class userPreferenceFragment extends PreferenceFragment
    {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.user_preferences);

            Activity activity = getActivity();
            String connectedWith = getLocalUserData(activity).getConnectedWith();

            if (connectedWith.equals(PHONE)){
                findPreference("mdp").setVisible(false);
                findPreference("phone").setVisible(false);
            }

            findPreference("email").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(activity, ChangeEmailActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("phone").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(activity, ChangePhoneNumberActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("mdp").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(activity, ChangePasswordActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("disconnect").setOnPreferenceClickListener(preference -> {

                new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.disconnection))
                        .setMessage(activity.getString(R.string.are_u_sure_to_disconnect))
                        .setPositiveButton(R.string.yes, (dialog, which) -> {

                            FirebaseAuth.getInstance().signOut();
                            setLocalUserData(activity, new User());

                            startActivityForResult(new Intent(activity, SplashActivity.class), 0);
                            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

                return false;
            });

            findPreference("invite").setOnPreferenceClickListener(preference -> {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_message)+"https://play.google.com/store/apps/details?id="+activity.getPackageName()+"\n\n");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_a_friend)));

                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("report_problem").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(activity, ReportProblemActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("contact_us").setOnPreferenceClickListener(preference -> {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+"afrikett-support@gmail.com"));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));

                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("privacy_policy").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(activity, PrivacyPolicyActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

            findPreference("app_info").setOnPreferenceClickListener(preference -> {

                startActivity(new Intent(activity, AboutActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                return false;
            });

        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }
    }
}
