package com.neteru.afrikett.ui.activities.others_activities.settings.general;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.Constants;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setStringPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.validateMail;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EMAIL;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class ChangeEmailActivity extends AfrikettBaseActivity {
    private DatabaseReference databaseReference;
    private EditText emailEditText;
    private String currentEmail;
    private String newEmail;
    private String connectedWith;
    private FirebaseUser firebaseUser;
    private final static String TAG = "CHANGE_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        emailEditText = findViewById(R.id.email);

        currentEmail = EMPTY;
        connectedWith = getLocalUserData(this).getConnectedWith();
        String userId = getLocalUserData(this).getId();

        databaseReference = FirebaseDatabase.getInstance()
                                .getReference(DATABASE_ROOT)
                                .child("users")
                                .child(userId)
                                .child("email");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        currentEmail = dataSnapshot.getValue(String.class);
                        emailEditText.setText(currentEmail);
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
            Spannable title = new SpannableString(getString(R.string.email_address));
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

                if (dataChange()) updateData();

            }else {
                Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData(){
        newEmail = emailEditText.getText().toString();

        if (!newEmail.isEmpty() && validateMail(newEmail)){

            if (connectedWith.equals(EMAIL)) {

                if (firebaseUser.getEmail() == null) return;

                @SuppressLint("InflateParams")
                View getPassword = LayoutInflater.from(this).inflate(R.layout.layout_get_password, null);

                TextInputEditText passwordEditText = getPassword.findViewById(R.id.mdp);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(getString(R.string.password))
                        .setView(getPassword)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, (dialog, which) -> {

                            if (passwordEditText.getText() == null) return;

                            AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), passwordEditText.getText().toString().trim());

                            firebaseUser
                                    .reauthenticate(credential)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()){

                                            firebaseUser
                                                    .updateEmail(newEmail.trim())
                                                    .addOnCompleteListener(newTask -> {

                                                        if (!newTask.isSuccessful()) {

                                                            if (newTask.getException() == null) return;

                                                            try {
                                                                throw newTask.getException();
                                                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                                                Log.d(TAG, "onComplete: malformed_email");
                                                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                                                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                                                Log.d(TAG, "onComplete: exist_email");
                                                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.exist_email), Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                Log.d(TAG, "onComplete: " + e.getMessage());
                                                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                                                            }

                                                            return;
                                                        }

                                                        updateBaseEmail();
                                                    });
                                        }else {

                                            if (task.getException() == null) return;

                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthInvalidUserException invalidUser) {

                                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.no_account_found), Toast.LENGTH_SHORT).show();

                                            } catch (FirebaseAuthInvalidCredentialsException credentialsException) {

                                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                            } catch (Exception e) {

                                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                    });
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

            }else {
                updateBaseEmail();
            }

        }else {
            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBaseEmail(){

        databaseReference
                .setValue(newEmail.trim())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        Toast.makeText(ChangeEmailActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    setStringPreference(ChangeEmailActivity.this, Constants.USER_PREFS, "email", newEmail.trim());

                    Toast.makeText(ChangeEmailActivity.this, getString(R.string.email_updated), Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {

                        finish();
                        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                    }, Toast.LENGTH_SHORT);

                });
    }

    private boolean dataChange(){
        return !currentEmail.equals(emailEditText.getText().toString());
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
