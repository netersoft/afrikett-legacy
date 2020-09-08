package com.neteru.afrikett.ui.activities.showcase_activities.registration;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.hideKeyboard;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.SHORT_DELAY;

/**
 * Showcase Registration : Nom, Description
 */
public class FirstStepActivity extends AfrikettBaseActivity {
    private TextView descriptionLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_step);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        final EditText name = findViewById(R.id.name),
                 description = findViewById(R.id.description);
        descriptionLength = findViewById(R.id.description_length);

        findViewById(R.id.next).setOnClickListener(view -> {

            if (!name.getText().toString().trim().isEmpty() && !description.getText().toString().trim().isEmpty()) {

                hideKeyboard(FirstStepActivity.this);

                new Handler().postDelayed(() -> {

                    startActivity(new Intent(FirstStepActivity.this, SecondStepActivity.class)
                            .putExtra("name", name.getText().toString())
                            .putExtra("description", description.getText().toString()));
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                }, SHORT_DELAY / 3);

            }else {

                Toast.makeText(FirstStepActivity.this, getString(R.string.missing_info), Toast.LENGTH_SHORT).show();

            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newLength = s.length() + "/260";
                descriptionLength.setText(newLength);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        }
    }
}
