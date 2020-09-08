package com.neteru.afrikett.ui.activities.messenger_activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Report;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.generateKey;
import static com.neteru.afrikett.core.utilities.AppUtilities.getBooleanPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.getStringAbsolutePosition;
import static com.neteru.afrikett.core.utilities.AppUtilities.setBooleanPreference;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE_REPORT;
import static com.neteru.afrikett.core.utilities.Constants.USER;
import static com.neteru.afrikett.core.utilities.Constants.USER_REPORT;

public class ChatBoxInfoActivity extends AfrikettBaseActivity {
    // Valeur par défaut du choix de blocage lors du signalement
    private boolean reportChoiceValue = false;
    // Reference base de données
    private DatabaseReference reportDbReference;
    private DatabaseReference blockStateDbReference;
    private TextView blockContact;
    private TextView reportContact;
    private String contactId;
    private Integer contactType;
    private String contactName;
    private String contactProfilePic;
    private String contactPhoneNumber;
    private String contactColor;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_info);

        reportDbReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("reports");

        // Composants de la vue
        blockContact = findViewById(R.id.block);
        reportContact = findViewById(R.id.report);
        TextView name = findViewById(R.id.contactName),
                 phoneNumber = findViewById(R.id.contactNumber);
        ImageView profilPicture = findViewById(R.id.contactPicture);
        SwitchCompat autoDownloadSwitch = findViewById(R.id.autoDownloadSwitch),
                     notificationsSwitch = findViewById(R.id.notificationsSwitch);

        userId = getLocalUserData(ChatBoxInfoActivity.this).getId();

        // Récupération des données du contact
        if (getIntent() != null){

            contactId = getIntent().getStringExtra("targetId");
            contactName = getIntent().getStringExtra("targetName");
            contactColor = getIntent().getStringExtra("targetColor");
            contactType = getIntent().getIntExtra("targetType", USER);
            contactProfilePic = getIntent().getStringExtra("targetProfilePic");
            contactPhoneNumber = getIntent().getStringExtra("targetPhoneNumber");

            // Chargement de la photo de profil
            if (contactProfilePic.equals(DEFAULT)) {

                profilPicture.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(contactName),
                                               contactType == USER ? COLORS[getDigitFromString(contactName)] : Color.parseColor(contactColor)));

            } else {

                Glide
                        .with(this)
                        .load(contactProfilePic)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilPicture);
            }

            // Nom du contact
            name.setText(contactName);

            // Numéro de téléphone du contact
            phoneNumber.setText(contactPhoneNumber);

            // Récupération de l'état de blocage
            getBlockState();
        }

        // Configuration par défaut du switch de téléchargement automatique
        if (getBooleanPreference(this, MESSENGER_PREFS, "autoDownload", false)){

            autoDownloadSwitch.setChecked(true);

        }else {

            autoDownloadSwitch.setChecked(false);

        }

        // Configuration par défaut du switch de notifications
        if (getBooleanPreference(this, MESSENGER_PREFS, "showNotif", false)){

            notificationsSwitch.setChecked(true);

        }else {

            notificationsSwitch.setChecked(false);

        }

        // Changement de la valeur de la variable de téléchargement automatique
        autoDownloadSwitch.setOnCheckedChangeListener((compoundButton, b) -> setBooleanPreference(ChatBoxInfoActivity.this, MESSENGER_PREFS, "autoDownload", b));

        // Changement de la valeur de la variable de notifications
        notificationsSwitch.setOnCheckedChangeListener((compoundButton, b) -> setBooleanPreference(ChatBoxInfoActivity.this, MESSENGER_PREFS, "showNotif", b));

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.chat_box_info_title));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    /**
     * Récupération de l'état de blocage
     */
    private void getBlockState() {

        // Position de l'ID utilisateur courant
        List<String> tempList = new ArrayList<>();
        tempList.add(contactId);
        int position = getStringAbsolutePosition(getLocalUserData(this).getId(), tempList, true);

        // Récupération de la reference de la base de données
        blockStateDbReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT)
                .child("messengers")
                .child(getNode())
                .child(position == 0 ? "firstIdBlockState" : "secondIdBlockState");

        // Etat de blocage
        blockStateDbReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            Boolean state = dataSnapshot.getValue(Boolean.class);

                            // Configuration par défaut de la commande de blocage / déblocage
                            if (state != null) {
                                if (state) {

                                    // Si le contact est bloqué
                                    blockContact.setText(getString(R.string.unblock));

                                } else {

                                    // Sinon
                                    blockContact.setText(getString(R.string.block));

                                }

                                // Préparation des boîtes de dialogue
                                setDialogs(state);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Préparation des boîtes de dialogue
     * @param state / Etat du blocage
     */
    private void setDialogs(final Boolean state){

        // Bloquer / Débloquer le contact
        blockContact.setOnClickListener(view -> {

            if (state){

                // Si le contact est bloqué
                new AlertDialog.Builder(ChatBoxInfoActivity.this)
                        .setTitle(getString(R.string.unblock_contact_title))
                        .setMessage(getString(R.string.unblock_contact_msg))
                        .setPositiveButton(getString(R.string.unblock), (dialogInterface, i) -> {

                            setBlockState(false); // Déblocage du contact

                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();

            }else {

                // Sinon
                new AlertDialog.Builder(ChatBoxInfoActivity.this)
                        .setTitle(getString(R.string.block_contact_title))
                        .setMessage(getString(R.string.block_contact_msg))
                        .setPositiveButton(getString(R.string.block), (dialogInterface, i) -> {

                            setBlockState(true); // Blocage du contact

                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();

            }

        });

        // Signaler le contact
        reportContact.setOnClickListener(view -> {
            CharSequence[] choice = {getString(R.string.report_contact_msg)};
            boolean[] choiceSet = {reportChoiceValue};

            new AlertDialog.Builder(ChatBoxInfoActivity.this)
                    .setTitle(getString(R.string.report_contact))
                    .setMultiChoiceItems(choice, choiceSet, (dialogInterface, i, b) -> {

                        // Valeur de l'option blocage
                        if(i == 0){ reportChoiceValue = b; }

                    })
                    .setPositiveButton(getString(R.string.report), (dialogInterface, i) -> {

                        if (reportChoiceValue){
                            setBlockState(true); // blocage du contact
                        }

                        // Disparition de la boîte de dialogue courante
                        dialogInterface.dismiss();

                        // Boîte de commentaire
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatBoxInfoActivity.this);
                        builder.setTitle(R.string.report_contact);

                        // Vue personnalisée
                        @SuppressLint("InflateParams")
                        View reportView = LayoutInflater.from(ChatBoxInfoActivity.this)
                                                        .inflate(R.layout.layout_report_comment, null);
                        final EditText comment = reportView.findViewById(R.id.comment);

                        builder
                                .setPositiveButton(getString(R.string.send), (dialogInterface1, i1) -> {

                                    String key = reportDbReference.push().getKey();

                                    // Enregistrement du signalement
                                    reportDbReference
                                        .child(key != null ? key : generateKey(13))
                                        .setValue(new Report(key,
                                                             contactId,
                                                             contactName,
                                                             contactProfilePic,
                                                             contactType == USER ? contactPhoneNumber : contactColor,
                                                             contactType == USER ? USER_REPORT : SHOWCASE_REPORT,
                                                             userId,
                                                             comment.getText().toString()))

                                    .addOnSuccessListener(aVoid -> Toast.makeText(ChatBoxInfoActivity.this, getString(R.string.ur_request_will_be_processed), Toast.LENGTH_SHORT).show());


                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .setView(reportView)
                                .show();

                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();

        });


    }

    /**
     * Changement de l'état du blocage
     * @param newState / nouvel état de blocage
     */
    private void setBlockState(Boolean newState) {
        if (blockStateDbReference != null){

            blockStateDbReference.setValue(newState);

        }
    }

    /**
     * Générateur de noeud de discussion
     * @return noeud de discussion
     */
    private String getNode(){
        final List<String> list = new ArrayList<>();
        list.add(getLocalUserData(this).getId());
        list.add(contactId);
        Collections.sort(list);

        return list.get(0) + "&" + list.get(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

        return true;
    }
}
