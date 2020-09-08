package com.neteru.afrikett.ui.activities.showcase_activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.ui.activities.showcase_activities.registration.FirstStepActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInSlowAnimation;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

/**
 * Showcase Registration : Intro
 */
public class AddActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final TextView title = findViewById(R.id.title),
                 summary = findViewById(R.id.summary);

        new Handler().postDelayed(() -> {

            title.setVisibility(View.VISIBLE);
            title.startAnimation(getFadeInSlowAnimation(AddActivity.this));

            new Handler().postDelayed(() -> {

                summary.setVisibility(View.VISIBLE);
                summary.startAnimation(getFadeInSlowAnimation(AddActivity.this));
                Typeface rmedium = Typeface.createFromAsset(getAssets(),"fonts/rmedium.ttf");
                summary.setTypeface(rmedium);

            }, Constants.SHORT_DELAY );

        }, Constants.SHORT_DELAY / 2);

        findViewById(R.id.begin).setOnClickListener(view -> {

            startActivity(new Intent(AddActivity.this, FirstStepActivity.class));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        }
    }
}

// No Comment.
