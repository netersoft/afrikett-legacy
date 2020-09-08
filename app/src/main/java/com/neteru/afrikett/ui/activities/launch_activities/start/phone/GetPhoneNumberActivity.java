package com.neteru.afrikett.ui.activities.launch_activities.start.phone;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.ui.activities.launch_activities.start.email.LoginActivity;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import static com.neteru.afrikett.core.utilities.AppUtilities.requestEditTextFocus;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class GetPhoneNumberActivity extends AppCompatActivity {

    // Entrée numéro de téléphone
    private IntlPhoneInput phoneInputView;

    // EditText de l'entrée numéro de téléphone
    private AppCompatEditText phoneEditText;

    // Button de ssoumission de la requête
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_number);

        // Texte d'indication
        TextView indicationTextView = findViewById(R.id.get_phone_number_indication);
        // Chargement du texte d'indication
        setTextViewHTML(indicationTextView, getResources().getString(R.string.get_phone_number_indication_txt));

        submit = findViewById(R.id.next);
        phoneInputView = findViewById(R.id.phone_input);
        phoneEditText = phoneInputView.findViewById(R.id.intl_phone_edit__phone);

        // Couleur par défaut de l'EditText
        setEditTextColor(R.color.white);
        submit.setEnabled(false);

        // Observateur de l'EditText
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){

                    setEditTextColor(R.color.white);
                    submit.setEnabled(false);

                }else {

                    submit.setEnabled(true);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Indicatif par défaut
        phoneInputView.setEmptyDefault("tg");

        // Observateur de validité du numéro
        phoneInputView.setOnValidityChange((view, isValid) -> {
            if (isValid){
                setEditTextColor(R.color.limegreen);
            }else {
                setEditTextColor(R.color.red);
            }
        });

        // Soumission du numéro depuis le clavier
        phoneInputView.setOnKeyboardDone((view, isValid) -> toValidationActivity());

        // Soumission du numéro depuis le bouton
        submit.setOnClickListener(view -> toValidationActivity());

        // Donne le focus au champ de texte
        findViewById(R.id.phone_icon).setOnClickListener(v -> requestEditTextFocus(GetPhoneNumberActivity.this, phoneEditText));

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
        }
    }

    /**
     * Vérification du numéro et ouverture du validateur
     */
    private void toValidationActivity(){

        // Si le champ est vide
        if (phoneEditText.getText() != null && phoneEditText.getText().toString().isEmpty()){
            return;
        }

        if(phoneInputView.isValid()) { // Si le numéro est valide

            //Boîte de dialogue de vérification
            new AlertDialog.Builder(GetPhoneNumberActivity.this)

                    .setMessage(getString(R.string.phone_dialog_message_a)+phoneInputView.getPhoneNumber().getCountryCode()
                            +" "+phoneInputView.getPhoneNumber().getNationalNumber()+" "+getString(R.string.phone_dialog_message_b))

                    .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {

                        startActivityForResult(new Intent(GetPhoneNumberActivity.this, ValidationActivity.class)
                                .putExtra("number", String.valueOf(phoneInputView.getNumber()))
                                .putExtra("countryCode", String.valueOf(phoneInputView.getPhoneNumber().getCountryCode()))
                                .putExtra("nationalNumber", String.valueOf(phoneInputView.getPhoneNumber().getNationalNumber()))
                                .putExtra("country", phoneInputView.getSelectedCountry().getIso() + "-" + phoneInputView.getSelectedCountry().getName())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), Constants.RANDOM_VALUE);

                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    })
                    .setNeutralButton(getString(R.string.modify), null)
                    .show();

        }else {
            Toast.makeText(GetPhoneNumberActivity.this, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
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

                if (span.getURL().equals("#1")){

                    startActivity(new Intent(GetPhoneNumberActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Non support de l'action retour
        finish();
    }
}
