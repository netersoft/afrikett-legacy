package com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings.sub_settings;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import java.util.Calendar;

import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class ShowcaseOpeningHoursActivity extends AfrikettBaseActivity {

    private RadioGroup optionsGroup;

    private AppCompatCheckBox[] checkBoxes;
    private EditText[] startHours;
    private EditText[] endHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_opening_hours);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            Spannable title = new SpannableString(getString(R.string.opening_hours));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }

        AppCompatCheckBox monday = findViewById(R.id.monday);
        AppCompatCheckBox tuesday = findViewById(R.id.tuesday);
        AppCompatCheckBox wednesday = findViewById(R.id.wednesday);
        AppCompatCheckBox thursday = findViewById(R.id.thursday);
        AppCompatCheckBox friday = findViewById(R.id.friday);
        AppCompatCheckBox saturday = findViewById(R.id.saturday);
        AppCompatCheckBox sunday = findViewById(R.id.sunday);

        EditText monday_start_hour = findViewById(R.id.monday_start_hour);
        EditText tuesday_start_hour = findViewById(R.id.tuesday_start_hour);
        EditText wednesday_start_hour = findViewById(R.id.wednesday_start_hour);
        EditText thursday_start_hour = findViewById(R.id.thursday_start_hour);
        EditText friday_start_hour = findViewById(R.id.friday_start_hour);
        EditText saturday_start_hour = findViewById(R.id.saturday_start_hour);
        EditText sunday_start_hour = findViewById(R.id.sunday_start_hour);

        EditText monday_end_hour = findViewById(R.id.monday_end_hour);
        EditText tuesday_end_hour = findViewById(R.id.tuesday_end_hour);
        EditText wednesday_end_hour = findViewById(R.id.wednesday_end_hour);
        EditText thursday_end_hour = findViewById(R.id.thursday_end_hour);
        EditText friday_end_hour = findViewById(R.id.friday_end_hour);
        EditText saturday_end_hour = findViewById(R.id.saturday_end_hour);
        EditText sunday_end_hour = findViewById(R.id.sunday_end_hour);

        checkBoxes = new AppCompatCheckBox[]{monday, tuesday, wednesday, thursday, friday, saturday, sunday};
        startHours = new EditText[]{monday_start_hour, tuesday_start_hour, wednesday_start_hour, thursday_start_hour, friday_start_hour, saturday_start_hour, sunday_start_hour};
        endHours = new EditText[]{monday_end_hour, tuesday_end_hour, wednesday_end_hour, thursday_end_hour, friday_end_hour, saturday_end_hour, sunday_end_hour};

        View.OnClickListener onClickListener = v -> {

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(ShowcaseOpeningHoursActivity.this, R.style.TimePickerStyle, (timePicker, selectedHour, selectedMinute) -> {
                String newHour =  (String.valueOf(selectedHour).length() == 1 ? "0" + selectedHour : selectedHour)
                                    + ":" +
                                  (String.valueOf(selectedMinute).length() == 1 ? "0" + selectedMinute : selectedMinute);

                EditText editText = (EditText) v;
                editText.setText(newHour);
            }, hour, minute, true);
            mTimePicker.setTitle(getString(R.string.hour));
            mTimePicker.show();

        };

        for (int i = 0; i < checkBoxes.length; i++){

            final int cursor = i;

            checkBoxes[cursor].setOnCheckedChangeListener((buttonView, isChecked) -> {

                startHours[cursor].setText(EMPTY);
                endHours[cursor].setText(EMPTY);
                startHours[cursor].setEnabled(isChecked);
                endHours[cursor].setEnabled(isChecked);

            });

            startHours[cursor].setOnClickListener(onClickListener);
            endHours[cursor].setOnClickListener(onClickListener);
        }

        setCheckboxes(false);

        optionsGroup = findViewById(R.id.header_group);

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId){
                case R.id.option_1:

                    setCheckboxes(false);
                    break;

                case R.id.option_2:

                    setCheckboxes(false);
                    break;

                case R.id.option_3:

                    setCheckboxes(true);
                    break;
            }
        });

    }

    private void setCheckboxes(boolean state){

        for (CheckBox checkBox: checkBoxes){
            checkBox.setEnabled(state);
        }

    }

    private void checkChanges(){

        if (optionsGroup.getCheckedRadioButtonId() != -1){

            // Dialogue de confirmation de l'opération
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.opening_hours))
                    .setMessage(getString(R.string.cancel_modifications_dialog_msg))
                    .setPositiveButton(R.string.delete, (dialog, which) -> {

                        finish();
                        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();

        }else {

            finish();
            overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.validate_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_validate){

            StringBuilder builder = new StringBuilder();

            switch (optionsGroup.getCheckedRadioButtonId()){
                case R.id.option_1:

                    builder.append("<h5>").append(getString(R.string.always_open)).append("</h5>");
                    break;

                case R.id.option_2:

                    builder.append("<h5>").append(getString(R.string.no_schedule_available)).append("</h5>");
                    break;

                case R.id.option_3:

                    builder.append("<h5>").append(getString(R.string.open_at_certain_hours)).append("</h5><br>");

                    for (int i = 0; i <checkBoxes.length; i++){

                        if (checkBoxes[i].isChecked()){
                            builder.append("<b>").append(checkBoxes[i].getText().toString()).append("</b>").append("\t\t");

                            builder.append(startHours[i].getText().toString());
                            builder.append("<b>").append(getString(R.string.dash)).append("</b>");
                            builder.append(endHours[i].getText().toString()).append("<br>");
                        }

                    }

                    break;
            }

            if (builder.length() > 0){
                setResult(RESULT_OK, getIntent().putExtra("data", builder.toString()));

                finish();
                overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        checkChanges();

    }

    @Override
    public boolean onSupportNavigateUp() {

        checkChanges();

        return false;
    }
}
