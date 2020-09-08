package com.neteru.afrikett.ui.activities.others_activities;

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

import androidx.core.content.ContextCompat;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.ui.activities.others_activities.help.PrivacyPolicyActivity;
import com.neteru.afrikett.ui.activities.others_activities.help.ReportProblemActivity;

public class HelpActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        findViewById(R.id.report_problem).setOnClickListener(v -> {

            startActivity(new Intent(HelpActivity.this, ReportProblemActivity.class));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        findViewById(R.id.contact_by_mail).setOnClickListener(v -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"+"afrikett-support@gmail.com"));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));

        });

        findViewById(R.id.privacy_policy).setOnClickListener(v -> {

            startActivity(new Intent(HelpActivity.this, PrivacyPolicyActivity.class));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

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
            Spannable title = new SpannableString(getString(R.string.help));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

            getSupportActionBar().setElevation(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skyblue));
        }
    }
}
