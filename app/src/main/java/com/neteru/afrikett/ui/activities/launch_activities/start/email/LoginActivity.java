package com.neteru.afrikett.ui.activities.launch_activities.start.email;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.ui.activities.main_activities.HomeActivity;

import java.util.regex.Pattern;

import static com.neteru.afrikett.core.utilities.AppUtilities.requestEditTextFocus;
import static com.neteru.afrikett.core.utilities.AppUtilities.setLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.validateMail;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class LoginActivity extends AfrikettBaseActivity {

    // EditTexts
    private EditText emailEditText, passwordEditText;

    // Instance utilisateur courant
    private FirebaseAuth mAuth;

    // Reference base de données
    private DatabaseReference databaseReference;

    // Dialogue de chargement
    private LoadingDialog loadingDialog;

    // Button de connexion
    private Button loginBut;

    // TAG
    private final static String TAG = "EMAIL_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBut = findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.mdp);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("users");
        loadingDialog = new LoadingDialog(this);

        // Chargement du texte de mot de passe oublié
        setTextViewHTML(findViewById(R.id.forgot_password_indication), getResources().getString(R.string.forgot_password_indication_txt));

        // Chargement du texte d'inscription
        setTextViewHTML(findViewById(R.id.email_login_indication), getResources().getString(R.string.email_login_indication_txt));

        // Chargement de l'email courant s'il existe
        setDefaultEmailAdress();

        // Etat par défaut du bouton de connexion
        loginBut.setEnabled(false);

        // Lancement de l'authentification
        loginBut.setOnClickListener(v -> {
            if (Connectivity.getInstance(LoginActivity.this).isOnline()) {
                toLogin();
            }else {
                Toast.makeText(LoginActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
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
                    loginBut.setEnabled(false);
                }else {
                    if (passwordEditText.getText().toString().length() > 0) {
                        loginBut.setEnabled(true);
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
                    loginBut.setEnabled(false);
                }else {
                    if (emailEditText.getText().toString().length() > 0) {
                        loginBut.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Donne le focus au champ de texte email
        findViewById(R.id.email_icon).setOnClickListener(v -> requestEditTextFocus(LoginActivity.this, emailEditText));

        // Donne le focus au champ de texte mot de passe
        findViewById(R.id.password_icon).setOnClickListener(v -> requestEditTextFocus(LoginActivity.this, passwordEditText));

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
        }
    }

    /**
     * Lancement du processus de connexion
     */
    private void toLogin(){

        // Si tous les champs ne sont pas fournis
        if (emailEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty()){

            return;
        }

        // Si l'email n'est pas valide
        if (!validateMail(emailEditText.getText().toString())){

            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;

        }

        // Lancement du dialogue de chargement
        loadingDialog.setMsg(getString(R.string.login_in_progress));
        loadingDialog.show();

        // Authentification ...
        mAuth
                .signInWithEmailAndPassword(
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        if (task.getResult() != null){

                            // Récupération des données utilisateur de la base de données
                            databaseReference
                                    .child(task.getResult().getUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() != null){
                                                User user = dataSnapshot.getValue(User.class);

                                                loadingDialog.dismiss();

                                                // Si les données utilisateur n'existent pas
                                                if (user == null){

                                                    Toast.makeText(LoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                                                    return;

                                                }

                                                // On inscrit les données dans des variables préférentielles locales
                                                setLocalUserData(LoginActivity.this,
                                                         new User(
                                                                user.getId(),
                                                                user.getName(),
                                                                user.getNationalNumber(),
                                                                user.getCountryCode(),
                                                                user.getNumber(),
                                                                user.getCountry(),
                                                                user.getEmail(),
                                                                user.getConnectedWith()));

                                                // On redirige vers l'activité principale
                                                startActivityForResult(new Intent(LoginActivity.this, HomeActivity.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), Constants.RANDOM_VALUE);
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            loadingDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }

                    }else{

                        loadingDialog.dismiss();

                        if (task.getException() == null){
                            return;
                        }

                        try
                        {
                            throw task.getException();
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthInvalidUserException invalidEmail)
                        {
                            Log.d(TAG, "onComplete: invalid_email");
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                        {
                            Log.d(TAG, "onComplete: wrong_password");
                            Toast.makeText(LoginActivity.this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "onComplete: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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

                switch (span.getURL()){
                    case "#1":

                        // Ouverture de l'activité de gestion des mots de passe oubliés
                        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                        break;

                    case "#2":

                        // Ouverture de l'activité d'inscription
                        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                        break;

                }

            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }
}
