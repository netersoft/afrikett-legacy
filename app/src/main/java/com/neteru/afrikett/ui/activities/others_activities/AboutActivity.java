package com.neteru.afrikett.ui.activities.others_activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Timing;

public class AboutActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView copyright = findViewById(R.id.copyright);

        String copyrightStr = getString(R.string.app_copyright, Timing.getCurrentDate("yyyy"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            copyright.setText(Html.fromHtml(copyrightStr, Html.FROM_HTML_MODE_COMPACT));
        }else{
            copyright.setText(Html.fromHtml(copyrightStr));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.skyblue));
        }
    }
}
