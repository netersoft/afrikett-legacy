package com.neteru.afrikett.core.utilities;

import android.os.Bundle;

import com.neteru.afrikett.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité Customisée
 */
public abstract class AfrikettBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

        return true;
    }

}
