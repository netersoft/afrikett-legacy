package com.neteru.afrikett.ui.activities.others_activities.settings.general;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.ui.activities.launch_activities.start.email.ForgotPasswordActivity;

public class ChangePasswordActivity extends AfrikettBaseActivity {
    private TextInputEditText currentMdp;
    private TextInputEditText confirmMdp;
    private TextInputEditText newMdp;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        TextView forgotPasswordIndication = findViewById(R.id.forgot_password_indication);
        setTextViewHTML(forgotPasswordIndication, getString(R.string.forgot_password_indication_txt));

        newMdp = findViewById(R.id.new_mdp);
        currentMdp = findViewById(R.id.current_mdp);
        confirmMdp = findViewById(R.id.new_mdp_confirm);

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
            Spannable title = new SpannableString(getString(R.string.password));
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

                if (currentMdp.getText() == null || newMdp.getText() == null || confirmMdp.getText() == null) return false;

                String newMdpStr = newMdp.getText().toString().trim();
                String confirmMdpStr = confirmMdp.getText().toString().trim();

                if (newMdpStr.isEmpty() || confirmMdpStr.isEmpty()) return false;


                if (!newMdpStr.equals(confirmMdpStr)){

                    Toast.makeText(this, getString(R.string.non_compliant_mdp), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (firebaseUser.getEmail() == null) return false;

                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentMdp.getText().toString().trim());

                firebaseUser
                        .reauthenticate(credential)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()){

                                firebaseUser
                                        .updatePassword(newMdpStr)
                                        .addOnCompleteListener(newTask -> {

                                            if (newTask.isSuccessful()) {

                                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_updated), Toast.LENGTH_SHORT).show();

                                                new Handler().postDelayed(() -> {

                                                    finish();
                                                    overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                                                }, Toast.LENGTH_SHORT);

                                            }else {

                                                if (newTask.getException() == null) return;

                                                try {
                                                    throw newTask.getException();
                                                } catch (FirebaseAuthWeakPasswordException weakPassword) {

                                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.weak_password), Toast.LENGTH_SHORT).show();

                                                } catch (FirebaseAuthInvalidUserException invalidUser) {

                                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.no_account_found), Toast.LENGTH_SHORT).show();

                                                } catch (FirebaseAuthRecentLoginRequiredException loginRequired) {

                                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.login_required), Toast.LENGTH_SHORT).show();

                                                } catch (Exception e) {

                                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                                }

                                            }

                                        });

                            }else {

                                if (task.getException() == null) return;

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException invalidUser) {

                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.no_account_found), Toast.LENGTH_SHORT).show();

                                } catch (FirebaseAuthInvalidCredentialsException credentialsException) {

                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {

                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                }

                            }

                        });

            }else {
                Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Chargement du Html avec des liens clickables
     * @param text / TextView
     * @param html / Chaîne en Html
     */
    private void setTextViewHTML(TextView text, String html) {
        // Sequence HTML
        CharSequence sequence;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        }else{
            sequence = Html.fromHtml(html);
        }

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        // Extraction des liens
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);

        // Activation des liens
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Branchement des écouteurs de click
     * @param strBuilder / Chaîne de caractère formatée
     * @param span / Url
     */
    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {

                if ("#1".equals(span.getURL())) {// Ouverture de l'activité de gestion des mots de passe oubliés
                    startActivity(new Intent(ChangePasswordActivity.this, ForgotPasswordActivity.class));
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                }

            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }
}
