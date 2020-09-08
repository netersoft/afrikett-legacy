package com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ContactChooserActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setTextViewDrawableColor;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;

public class ShowcaseAdminSettingsActivity extends AfrikettBaseActivity {
    private String userId;
    private String showcaseId;
    private DatabaseReference databaseReference;
    private ImageView adminBoxImg;
    private TextView adminBoxName;
    private TextView deleteBox;
    private TextView headerInfo;
    private boolean showMenu = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_admin_settings);

        userId = getLocalUserData(this).getId();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        showcaseId = getIntent().getStringExtra("showcaseId");
        String showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");

        headerInfo = findViewById(R.id.header_info);
        adminBoxImg = findViewById(R.id.admin_box_img);
        adminBoxName = findViewById(R.id.admin_box_name);

        deleteBox = findViewById(R.id.delete_box);
        deleteBox.setOnClickListener(v -> {

        });
        setTextViewDrawableColor(deleteBox, getResources().getColor(R.color.skyblue));

        getShowcaseData();

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
            Spannable title = new SpannableString(getString(R.string.administration));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    private void getShowcaseData() {

        databaseReference
                .child("showcases")
                .child(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null){
                            return;
                        }

                        List<String> owners = showcase.getOwners();

                        if (owners.size() > 0){
                            if (owners.get(0).equals(userId)){

                                headerInfo.setVisibility(View.VISIBLE);
                                deleteBox.setVisibility(View.VISIBLE);
                                showMenu = true;

                            }else {

                                headerInfo.setVisibility(View.GONE);
                                deleteBox.setVisibility(View.GONE);
                                showMenu = false;

                            }

                            invalidateOptionsMenu();
                            loadMainAdminData(owners.get(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadMainAdminData(String mainAdminId) {

        databaseReference
                .child("users")
                .child(mainAdminId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        User user = dataSnapshot.getValue(User.class);

                        if (user == null){ return; }

                        adminBoxName.setText(user.getName());

                        if (user.getProfileUrl().equals(DEFAULT)) {

                            adminBoxImg.setImageDrawable(TextDrawable.builder()
                                    .buildRound(getFirstLetters(user.getName()),
                                            COLORS[getDigitFromString(user.getName())]));

                        }else {

                            Glide
                                    .with(ShowcaseAdminSettingsActivity.this)
                                    .load(user.getProfileUrl())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(adminBoxImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_settings_menu, menu);

        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(showMenu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add_admin){

            startActivityForResult(new Intent(ShowcaseAdminSettingsActivity.this, ContactChooserActivity.class), RANDOM_VALUE);
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RANDOM_VALUE && resultCode == RESULT_OK && data != null){
            String selectedContact = data.getStringExtra("selectedContact");

            Toast.makeText(this, selectedContact, Toast.LENGTH_SHORT).show();
        }
    }
}
