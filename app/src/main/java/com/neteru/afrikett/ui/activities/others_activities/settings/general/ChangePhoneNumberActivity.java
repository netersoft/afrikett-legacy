package com.neteru.afrikett.ui.activities.others_activities.settings.general;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.Constants;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setStringPreference;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class ChangePhoneNumberActivity extends AfrikettBaseActivity {
    private DatabaseReference databaseReference;
    private IntlPhoneInput phoneInput;
    private String currentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number);

        phoneInput = findViewById(R.id.phone_input);

        currentPhoneNumber = EMPTY;
        String userId = getLocalUserData(this).getId();

        databaseReference = FirebaseDatabase.getInstance()
                                            .getReference(DATABASE_ROOT)
                                            .child("users")
                                            .child(userId);

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) return;

                        currentPhoneNumber = user.getNumber();

                        phoneInput.setNumber(user.getNationalNumber());
                        phoneInput.setEmptyDefault(user.getCountry().split("-")[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
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
            Spannable title = new SpannableString(getString(R.string.phone));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

            getSupportActionBar().setElevation(0);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skyblue));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.validate_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Click sur le bouton de mise à jour des données
        if (item.getItemId() == R.id.action_validate){

            if (Connectivity.getInstance(this).isOnline()){

                if (dataChange()){

                    updatePhoneNumberValues();

                }

            }else {
                Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePhoneNumberValues(){

        databaseReference
                .child("nationalNumber")
                .setValue(String.valueOf(phoneInput.getPhoneNumber().getNationalNumber()))
                .addOnSuccessListener(aVoid -> {

                    setStringPreference(ChangePhoneNumberActivity.this, Constants.USER_PREFS, "nationalNumber", String.valueOf(phoneInput.getPhoneNumber().getNationalNumber()));
                    databaseReference
                            .child("countryCode")
                            .setValue(String.valueOf(phoneInput.getPhoneNumber().getCountryCode()))
                            .addOnSuccessListener(bVoid -> {

                                setStringPreference(ChangePhoneNumberActivity.this, Constants.USER_PREFS, "countryCode", String.valueOf(phoneInput.getPhoneNumber().getCountryCode()));
                                databaseReference
                                        .child("number")
                                        .setValue(String.valueOf(phoneInput.getNumber()))
                                        .addOnSuccessListener(cVoid -> {

                                            setStringPreference(ChangePhoneNumberActivity.this, Constants.USER_PREFS, "number", String.valueOf(phoneInput.getNumber()));
                                            databaseReference
                                                    .child("country")
                                                    .setValue(phoneInput.getSelectedCountry().getIso() + "-" + phoneInput.getSelectedCountry().getName())
                                                    .addOnSuccessListener(dVoid -> {

                                                        setStringPreference(ChangePhoneNumberActivity.this, Constants.USER_PREFS, "country", phoneInput.getSelectedCountry().getIso() + "-" + phoneInput.getSelectedCountry().getName());

                                                        Toast.makeText(ChangePhoneNumberActivity.this, getString(R.string.phone_number_updated), Toast.LENGTH_SHORT).show();

                                                        new Handler().postDelayed(() -> {

                                                            finish();
                                                            overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                                                        }, Toast.LENGTH_SHORT);

                                                    });
                                        });
                            });
                });
    }

    private boolean dataChange(){
        return !currentPhoneNumber.equals(phoneInput.getNumber());
    }

    /**
     * Dialogue d'annulation des modifications
     */
    private void showCancelModificationsDialog(){

        // Dialogue de confirmation de l'opération
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.email_address))
                .setMessage(getString(R.string.cancel_modifications_dialog_msg))
                .setPositiveButton(R.string.delete, (dialog, which) -> {

                    finish();
                    overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    @Override
    public void onBackPressed() {

        // Si les données ont été modifiées
        if (dataChange()){
            showCancelModificationsDialog();
        }else { // Sinon
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        // Si les données ont été modifiées
        if (dataChange()){
            showCancelModificationsDialog();
        }else { // Sinon
            return super.onSupportNavigateUp();
        }

        return false;
    }
}
