package com.neteru.afrikett.ui.activities.launch_activities.start.phone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.ui.activities.launch_activities.start.email.LoginActivity;

import java.util.concurrent.TimeUnit;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.saveInfoData;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class ValidationActivity extends AppCompatActivity
            implements Button.OnClickListener {

    private FirebaseAuth mAuth;
    private String country;
    private String number;
    private String nationalNumber;
    private String countryCode;
    private String mVerificationID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText editCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressBar progressBar;
    private final static String LETTER_SPACING = " ";
    private String previousText;
    private TextView minutesTextView;
    private TextView secondsTextView;
    private int minutes = 0;
    private int seconds = 0;
    private CardView warningBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        //Instance de l'authentificateur
        mAuth = FirebaseAuth.getInstance();

        //On récupère les données téléphoniques recueillies au démarrage
        if (getIntent() != null) {
            country = getIntent().getStringExtra("country");
            number = getIntent().getStringExtra("number");
            nationalNumber = getIntent().getStringExtra("nationalNumber");
            countryCode = getIntent().getStringExtra("countryCode");
        }

        TextView title = findViewById(R.id.title);
        editCode = findViewById(R.id.verification_code);
        Button resend = findViewById(R.id.resend),
                verify = findViewById(R.id.verify);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        minutesTextView = findViewById(R.id.minutes_text_view);
        secondsTextView = findViewById(R.id.seconds_text_view);
        minutesTextView.setText(getString(R.string.time_init_value));
        secondsTextView.setText(getString(R.string.time_init_value));

        warningBox = findViewById(R.id.warning_box);
        TextView warningTextView = findViewById(R.id.warning_text);
        // Chargement du texte d'indication
        setTextViewHTML(warningTextView, getResources().getString(R.string.validation_warning_text));

        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                // Si le texte courant est différent de l'ancien autrement dit s'il y'a modification
                if (!text.equals(previousText)) {
                    // On retire tous les espaces
                    text = text.replace(LETTER_SPACING, EMPTY);

                    // Ajout d'espace entre chaque caractere
                    StringBuilder newText = new StringBuilder();
                    for (int i = 0; i < text.length(); i++) {
                        if (i == text.length() - 1) {
                            // Ne rien ajouter au dernier caractère
                            newText.append(text.charAt(text.length() - 1));
                        } else {
                            newText.append(text.charAt(i));
                            newText.append(LETTER_SPACING);
                        }
                    }

                    previousText = newText.toString();

                    // Mise à jour avec le nouveau texte espacé
                    editCode.setText(newText);

                    // Curseur en fin de texte
                    editCode.setSelection(newText.length());
                }
            }
        });

        initFireBaseCallbacks();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            title.setText(Html.fromHtml(getString(R.string.verification_activity_title, countryCode, nationalNumber), Html.FROM_HTML_MODE_COMPACT));
        }else{
            title.setText(Html.fromHtml(getString(R.string.verification_activity_title, countryCode, nationalNumber),null, null));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.skyblue));
        }

        //On branche les écouteurs d'évenements
        resend.setOnClickListener(this);
        verify.setOnClickListener(this);

        timer();
    }

    /**
     * Initialise et lance l'authentificateur
     */
    private void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) { // Quand verification terminée

                progressBar.setVisibility(View.INVISIBLE);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) { // Quand vérification échouée
                progressBar.setVisibility(View.INVISIBLE);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(ValidationActivity.this, getString(R.string.number_verification_failure), Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Toast.makeText(ValidationActivity.this, getString(R.string.operation_failure), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) { // Quand code envoyé
                /* Toast.makeText(ValidationActivity.this, getString(R.string.code_sended), Toast.LENGTH_SHORT).show(); */

                mVerificationID = verificationId;
                mResendToken = token; // On sauvegarde le token renvoyé

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        sendCode();
    }

    /**
     * Envoie le code de vérification
     */
    private void sendCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Numéro de téléphone à vérifier
                60,                 // Delai d'envoi
                TimeUnit.SECONDS,   // Unité du delai d'envoi
                this,               // Activité
                mCallbacks          // OnVerificationStateChangedCallbacks
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.resend: // Renvoie le code de vérification
                if (mResendToken != null) {

                    progressBar.setVisibility(View.VISIBLE);

                    try {

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                number,    // Numéro de téléphone à vérifier
                                6,               // Delai d'envoi
                                TimeUnit.MINUTES,   // Unité du delai d'envoi
                                this,               // Activité
                                mCallbacks,         // OnVerificationStateChangedCallbacks
                                mResendToken);             // Forcer le renvoi du Token depuis le callback

                    }catch (Exception e){

                        Toast.makeText(ValidationActivity.this, getString(R.string.code_sending_error), Toast.LENGTH_SHORT).show();

                    }

                }else{
                    Toast.makeText(ValidationActivity.this, getString(R.string.be_patient), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.verify: // On vérifie le code renseigné par l'utilisateur

                if (!editCode.getText().toString().isEmpty()) {

                    if (!mVerificationID.isEmpty()) {

                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationID, editCode.getText().toString().trim().replace(LETTER_SPACING, EMPTY)));

                    }else{
                        Toast.makeText(ValidationActivity.this, getString(R.string.be_patient), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * Vérificateur
     * @param credential / Objet contenant les éléments nécessaires à l'authentification
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.INVISIBLE);

                    if (task.isSuccessful()) {

                        Toast.makeText(ValidationActivity.this, getString(R.string.verified_number), Toast.LENGTH_SHORT).show();
                        next();

                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(ValidationActivity.this, getString(R.string.verification_failure), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Redirection vers l'activité suivante avec les paramètres nécessaires
     */
    public void next(){

        saveInfoData(this, nationalNumber, countryCode, number, country);

        startActivityForResult(new Intent(ValidationActivity.this, InfoActivity.class)
                        .putExtra("number", number)
                        .putExtra("country", country)
                        .putExtra("countryCode", countryCode)
                        .putExtra("nationalNumber", nationalNumber)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                Constants.RANDOM_VALUE);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void timer(){
        CountDownTimer countDownTimer = new CountDownTimer(59000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds++;
                secondsTextView.setText(String.valueOf(seconds).length() == 1 ? "0"+seconds : String.valueOf(seconds));

                if (minutesTextView.getText().toString().equals("01") && warningBox.getVisibility() != View.VISIBLE){
                    warningBox.setVisibility(View.VISIBLE);
                    warningBox.startAnimation(getFadeInAnimation(ValidationActivity.this));
                }
            }

            @Override
            public void onFinish() {
                minutes++;
                minutesTextView.setText(String.valueOf(minutes).length() == 1 ? "0"+minutes : String.valueOf(minutes));
                seconds = 0;
                secondsTextView.setText(getString(R.string.time_init_value));

                timer();
            }
        };
        countDownTimer.start();
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

                if (span.getURL().equals("#1")){

                    startActivityForResult(new Intent(ValidationActivity.this, LoginActivity.class), 0);
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                }

            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Non support de l'action Retour
        finish();
    }
}
