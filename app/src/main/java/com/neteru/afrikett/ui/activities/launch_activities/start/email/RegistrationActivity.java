package com.neteru.afrikett.ui.activities.launch_activities.start.email;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.ui.activities.main_activities.HomeActivity;
import com.tooltip.Tooltip;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.regex.Pattern;

import static com.neteru.afrikett.core.utilities.AppUtilities.capitalize;
import static com.neteru.afrikett.core.utilities.AppUtilities.setLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.validateMail;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EMAIL;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class RegistrationActivity extends AfrikettBaseActivity {

    // Bulle d'information
    private Tooltip phoneInfoTooltip;

    // Champs de texte
    private EditText usernameEditText, emailEditText, passwordEditText, phoneEditText;

    // Entrée numéro de téléphone
    private IntlPhoneInput phoneInput;

    // Bouton d'inscription
    private Button registrationBut;

    // Instance d'authentification
    private FirebaseAuth firebaseAuth;

    // Dialogue de chargement
    private LoadingDialog loadingDialog;

    // Reference base de données
    private DatabaseReference databaseReference;

    // TAG
    private final static String TAG = "EMAIL_REGISTRATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationBut = findViewById(R.id.register);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        phoneInput = findViewById(R.id.phone_input);
        phoneEditText = phoneInput.findViewById(R.id.intl_phone_edit__phone);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("users");
        loadingDialog = new LoadingDialog(this);

        // Observateur du champ de nom
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    registrationBut.setEnabled(false);
                }else {

                    if (emailEditText.getText().toString().length() > 0
                            && passwordEditText.getText().toString().length() > 0
                            && phoneEditText.getText().toString().length() > 0) {

                        registrationBut.setEnabled(true);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Observateur du champ d'adresse email
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    registrationBut.setEnabled(false);
                }else {

                    if (usernameEditText.getText().toString().length() > 0
                            && passwordEditText.getText().toString().length() > 0
                            && phoneEditText.getText().toString().length() > 0) {

                        registrationBut.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Observateur du champ de mot de passe
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    registrationBut.setEnabled(false);
                }else {

                    if (usernameEditText.getText().toString().length() > 0
                            && emailEditText.getText().toString().length() > 0
                            && phoneEditText.getText().toString().length() > 0) {

                        registrationBut.setEnabled(true);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Observateur du champ de texte du numéro de téléphone
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){

                    setEditTextColor(R.color.white);
                    registrationBut.setEnabled(false);

                }else {

                    if (usernameEditText.getText().toString().length() > 0
                            && passwordEditText.getText().toString().length() > 0
                            && emailEditText.getText().toString().length() > 0) {

                        registrationBut.setEnabled(true);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Indicatif par défaut
        phoneInput.setEmptyDefault("tg");

        // Observateur de validité du numéro
        phoneInput.setOnValidityChange((view, isValid) -> {
            if (isValid){
                setEditTextColor(R.color.limegreen);
            }else {
                setEditTextColor(R.color.red);
            }
        });

        // Couleur par défaut de l'EditText
        setEditTextColor(R.color.white);

        // Chargement du texte d'inscription
        setTextViewHTML(findViewById(R.id.email_registration_indication), getResources().getString(R.string.email_registration_indication_txt));

        // Chargement de l'email courant s'il existe
        setDefaultEmailAdress();

        // Etat par défaut du bouton d'inscription
        registrationBut.setEnabled(false);

        // Lancement de l'inscription
        registrationBut.setOnClickListener(v -> {
            if (Connectivity.getInstance(RegistrationActivity.this).isOnline()) {
                toRegister();
            }else {
                Toast.makeText(RegistrationActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            }
        });

        ImageView phoneInfo = findViewById(R.id.phoneInfo);
        // Construction de la bulle d'information
        phoneInfoTooltip = new Tooltip.Builder(phoneInfo)
                                        .setBackgroundColor(getResources().getColor(R.color.orange))
                                        .setTextColor(getResources().getColor(R.color.white))
                                        .setText(getString(R.string.phone_info_tooltip_txt))
                                        .setGravity(Gravity.TOP)
                                        .setCancelable(true)
                                        .build();

        phoneInfo.setOnClickListener(v -> phoneInfoTooltip.show());

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
        }
    }

    /**
     * Chargement de l'email courant de l'utilisateur s'il existe
     */
    private void setDefaultEmailAdress(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {

                emailEditText.setText(account.name);
                emailEditText.setSelection(account.name.length());

            }
        }
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
                    finish();
                    overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
                }

            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    /**
     * Customisation de l'arrière-plan de l'EditText contenu dans la librairie
     * @param color / Nouvelle couleur de l'arrière-plan
     */
    private void setEditTextColor(int color){

        Drawable drawable = phoneEditText.getBackground();
        drawable.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        phoneEditText.setBackground(drawable);

    }

    /**
     * Inscription du compte
     */
    private void toRegister(){

        // Si le champ numéro de téléphone est vide
        if (phoneEditText.getText().toString().isEmpty()){

            phoneInfoTooltip.show();
            return;
        }

        // Si l'un des autres champs est vide
        if (usernameEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty()
                || emailEditText.getText().toString().isEmpty()){
            return;
        }

        // Si le numéro n'est pas valide
//        if (!phoneInput.isValid()){
//
//            Toast.makeText(this, R.string.invalid_number, Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Si l'adresse email n'est pas valide
        if (!validateMail(emailEditText.getText().toString())){

            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        // Lancement du dialogue de chargement
        loadingDialog.setMsg(getString(R.string.be_patient));
        loadingDialog.show();

        // Inscription
        firebaseAuth
                .createUserWithEmailAndPassword(
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim())
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()){

                        // Si les résultats de la tâche sont inexistants
                        if (task.getResult() == null){

                            loadingDialog.dismiss();
                            return;
                        }

                        // Instanciation de l'utilisateur courant
                        final User user = new User(
                                task.getResult().getUser().getUid(),
                                capitalize(usernameEditText.getText().toString().trim()),
                                String.valueOf(phoneInput.getPhoneNumber().getNationalNumber()),
                                String.valueOf(phoneInput.getPhoneNumber().getCountryCode()),
                                String.valueOf(phoneInput.getNumber()),
                                phoneInput.getSelectedCountry().getIso() + "-" + phoneInput.getSelectedCountry().getName(),
                                emailEditText.getText().toString().trim(),
                                EMAIL
                        );

                        // Inscription dans la base de données
                        databaseReference
                                .child(task.getResult().getUser().getUid())
                                .setValue(user)
                                .addOnCompleteListener(task1 -> {

                                    loadingDialog.dismiss();

                                    if (task1.isSuccessful()){

                                        // On inscrit les données dans des variables préférentielles locales
                                        setLocalUserData(RegistrationActivity.this, user);

                                        // On redirige vers l'activité principale
                                        startActivityForResult(new Intent(RegistrationActivity.this, HomeActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), Constants.RANDOM_VALUE);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    }
                                });


                    }else {
                        loadingDialog.dismiss();

                        if (task.getException() == null){
                            return;
                        }

                        try
                        {
                            throw task.getException();
                        }
                        catch (FirebaseAuthWeakPasswordException weakPassword)
                        {
                            Log.d(TAG, "onComplete: weak_password");
                            Toast.makeText(RegistrationActivity.this, getString(R.string.weak_password), Toast.LENGTH_SHORT).show();
                        }
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                        {
                            Log.d(TAG, "onComplete: malformed_email");
                            Toast.makeText(RegistrationActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                        }
                        catch (FirebaseAuthUserCollisionException existEmail)
                        {
                            Log.d(TAG, "onComplete: exist_email");
                            Toast.makeText(RegistrationActivity.this, getString(R.string.exist_email), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "onComplete: " + e.getMessage());
                            Toast.makeText(RegistrationActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }
}
