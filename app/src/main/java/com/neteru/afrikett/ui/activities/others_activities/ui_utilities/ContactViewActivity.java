package com.neteru.afrikett.ui.activities.others_activities.ui_utilities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;

public class ContactViewActivity extends AfrikettBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        ImageView imageView = findViewById(R.id.contact_picture);
        TextView name = findViewById(R.id.contact_name),
                 number = findViewById(R.id.contact_number),
                 email = findViewById(R.id.contact_email);

        // Intent d'ajout de contact
        final Intent addContactIntent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);

        if (getIntent() != null){

            // Définition du nom
            if (getIntent().hasExtra("name") && getIntent().getStringExtra("name") != null) {
                name.setText(getIntent().getStringExtra("name"));

                // Définition de la photo de profil du contact
                imageView.setImageDrawable(TextDrawable.builder()
                        .buildRound(getFirstLetters(getIntent().getStringExtra("name")),
                                COLORS[getDigitFromString(getIntent().getStringExtra("name"))]));

                // Ajout du nom de contact à l'Intent
                addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, getIntent().getStringExtra("name"));

            }else {

                name.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);

            }

            // Définition du numéro du contact
            if (getIntent().hasExtra("number") && getIntent().getStringExtra("number") != null) {
                number.setText(getIntent().getStringExtra("number"));

                // Ajout du numéro de contact à l'Intent
                addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, getIntent().getStringExtra("number"));
            }else {
                number.setVisibility(View.GONE);
            }

            // Définition du mail du contact
            if (getIntent().hasExtra("email") && getIntent().getStringExtra("email") != null) {
                email.setText(getIntent().getStringExtra("email"));

                // Ajout du mail à l'Intent
                addContactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, getIntent().getStringExtra("email"));
            }else {
                email.setVisibility(View.GONE);
            }

        }

        // Ajouter comme nouveau contact
        findViewById(R.id.add_contact).setOnClickListener(view -> {

            startActivity(addContactIntent);
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.contact));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }
}
