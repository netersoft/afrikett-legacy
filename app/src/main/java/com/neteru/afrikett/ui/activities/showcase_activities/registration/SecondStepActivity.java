package com.neteru.afrikett.ui.activities.showcase_activities.registration;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.RecursiveRadioGroup;

import static com.neteru.afrikett.core.utilities.Constants.AGRO;
import static com.neteru.afrikett.core.utilities.Constants.CATERING;
import static com.neteru.afrikett.core.utilities.Constants.COMMUNICATION;
import static com.neteru.afrikett.core.utilities.Constants.EDUCATION_AND_TRAINING;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.ENTERTAINMENT;
import static com.neteru.afrikett.core.utilities.Constants.FASHION_AND_CLOTHING;
import static com.neteru.afrikett.core.utilities.Constants.FINANCE_AND_BANKING;
import static com.neteru.afrikett.core.utilities.Constants.HEALTH;
import static com.neteru.afrikett.core.utilities.Constants.HOTEL_BUSINESS;
import static com.neteru.afrikett.core.utilities.Constants.INFORMATION_SCIENCE;
import static com.neteru.afrikett.core.utilities.Constants.OTHER;
import static com.neteru.afrikett.core.utilities.Constants.PROFESSIONAL_SERVICES;
import static com.neteru.afrikett.core.utilities.Constants.PUBLIC_SERVICES;
import static com.neteru.afrikett.core.utilities.Constants.RETAIL_SALE;
import static com.neteru.afrikett.core.utilities.Constants.TRADE_AND_DISTRIBUTION;
import static com.neteru.afrikett.core.utilities.Constants.TRANSPORTS_AND_LOGISTICS;

/**
 * Showcase Registration : Domaine d'activité
 */
public class SecondStepActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_step);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        final RecursiveRadioGroup field = findViewById(R.id.field);

        final String name = getIntent().getStringExtra("name"),
                description = getIntent().getStringExtra("description");

        findViewById(R.id.next).setOnClickListener(view -> {

            if (field.getCheckedItemId() != -1) {
                int value = 0;

                switch (field.getCheckedItemId()) {
                    case R.id.rd_1_1:
                        value = AGRO;
                        break;
                    case R.id.rd_1_2:
                        value = INFORMATION_SCIENCE;
                        break;
                    case R.id.rd_1_3:
                        value = FASHION_AND_CLOTHING;
                        break;
                    case R.id.rd_1_4:
                        value = COMMUNICATION;
                        break;
                    case R.id.rd_1_5:
                        value = ENTERTAINMENT;
                        break;
                    case R.id.rd_1_6:
                        value = EDUCATION_AND_TRAINING;
                        break;
                    case R.id.rd_1_7:
                        value = FINANCE_AND_BANKING;
                        break;
                    case R.id.rd_1_8:
                        value = TRANSPORTS_AND_LOGISTICS;
                        break;

                    case R.id.rd_2_1:
                        value = TRADE_AND_DISTRIBUTION;
                        break;
                    case R.id.rd_2_2:
                        value = PUBLIC_SERVICES;
                        break;
                    case R.id.rd_2_3:
                        value = HOTEL_BUSINESS;
                        break;
                    case R.id.rd_2_4:
                        value = HEALTH;
                        break;
                    case R.id.rd_2_5:
                        value = PROFESSIONAL_SERVICES;
                        break;
                    case R.id.rd_2_6:
                        value = RETAIL_SALE;
                        break;
                    case R.id.rd_2_7:
                        value = CATERING;
                        break;
                    case R.id.rd_2_8:
                        value = OTHER;
                        break;
                }

                startActivity(new Intent(SecondStepActivity.this, ThirdStepActivity.class)
                        .putExtra("name", name)
                        .putExtra("description", description)
                        .putExtra("field", value));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }else {

                Toast.makeText(SecondStepActivity.this, getString(R.string.choose_activity_domain), Toast.LENGTH_SHORT).show();

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        }
    }
}
