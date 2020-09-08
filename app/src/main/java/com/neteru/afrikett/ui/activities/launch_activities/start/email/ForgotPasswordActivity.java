package com.neteru.afrikett.ui.activities.launch_activities.start.email;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import java.util.regex.Pattern;

import static com.neteru.afrikett.core.utilities.AppUtilities.requestEditTextFocus;
import static com.neteru.afrikett.core.utilities.AppUtilities.validateMail;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class ForgotPasswordActivity extends AfrikettBaseActivity {

    private EditText accountEmail;
    private Button send;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        accountEmail = findViewById(R.id.email);
        send = findViewById(R.id.send);

        // Instance d'authentification
        firebaseAuth = FirebaseAuth.getInstance();

        // Etat par défaut du bouton d'envoi
        send.setEnabled(false);

        // Email courant de l'utilisateur s'il existe
        setDefaultEmailAdress();

        // Observateur du champ adresse email
        accountEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    send.setEnabled(false);
                }else {
                    send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Réinitialisation de l'email
        send.setOnClickListener(v -> resetPassword());

        // Quêteur de focus
        findViewById(R.id.email_icon).setOnClickListener(v -> requestEditTextFocus(ForgotPasswordActivity.this, accountEmail));

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Réinitialisation du mot de passe
     */
    private void resetPassword() {
        // Vérification du champ de texte
        if (accountEmail.getText().toString().isEmpty()){
            return;
        }

        // Vérification de la validité de l'adresse email fournie
        if (!validateMail(accountEmail.getText().toString())){

            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        // Boîte de dialogue de vérification
        new AlertDialog.Builder(this)

                .setMessage(getString(R.string.forgot_password_dialog_msg) + accountEmail.getText().toString())

                .setPositiveButton(getString(R.string.send), (dialogInterface, i) -> {

                    // Réinitialisation du mot de passe
                    firebaseAuth.sendPasswordResetEmail(accountEmail.getText().toString().trim())
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){

                                    Toast.makeText(getApplicationContext(), getString(R.string.password_sent), Toast.LENGTH_SHORT).show();

                                } else{

                                    Toast.makeText(getApplicationContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();

                                }
                            });
                })
                .setNeutralButton(getString(R.string.cancel), null)
                .show();

    }

    /**
     * Chargement de l'email courant de l'utilisateur s'il existe
     */
    private void setDefaultEmailAdress(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {

                send.setEnabled(true);
                accountEmail.setText(account.name);
                accountEmail.setSelection(account.name.length());

            }
        }
    }
}
