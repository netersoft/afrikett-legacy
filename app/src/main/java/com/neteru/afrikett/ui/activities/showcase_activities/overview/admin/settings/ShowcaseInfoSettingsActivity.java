package com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.interfaces.GeocodingSystemListener;
import com.neteru.afrikett.core.models.RemoteDB.Location;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.ShowcaseContactDetails;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.GeocodingSystem;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings.sub_settings.ShowcaseOpeningHoursActivity;
import com.schibstedspain.leku.LocationPickerActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.validateMail;
import static com.neteru.afrikett.core.utilities.Constants.AGRO;
import static com.neteru.afrikett.core.utilities.Constants.CATERING;
import static com.neteru.afrikett.core.utilities.Constants.COMMUNICATION;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EDUCATION_AND_TRAINING;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.ENTERTAINMENT;
import static com.neteru.afrikett.core.utilities.Constants.FASHION_AND_CLOTHING;
import static com.neteru.afrikett.core.utilities.Constants.FINANCE_AND_BANKING;
import static com.neteru.afrikett.core.utilities.Constants.HEALTH;
import static com.neteru.afrikett.core.utilities.Constants.HOTEL_BUSINESS;
import static com.neteru.afrikett.core.utilities.Constants.INFORMATION_SCIENCE;
import static com.neteru.afrikett.core.utilities.Constants.OTHER;
import static com.neteru.afrikett.core.utilities.Constants.PROFESSIONAL_SERVICES;
import static com.neteru.afrikett.core.utilities.Constants.PUBLIC_SERVICES;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;
import static com.neteru.afrikett.core.utilities.Constants.RETAIL_SALE;
import static com.neteru.afrikett.core.utilities.Constants.SHORT_DELAY;
import static com.neteru.afrikett.core.utilities.Constants.TRADE_AND_DISTRIBUTION;
import static com.neteru.afrikett.core.utilities.Constants.TRANSPORTS_AND_LOGISTICS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

public class ShowcaseInfoSettingsActivity extends AfrikettBaseActivity {

    private String showcaseId;
    private String showcasePrimaryColor;
    private String initName;
    private String initDesc;
    private String initAddress;
    private String initEmail;
    private String initPhone;
    private String initWebsite;
    private String initOpeningHours;
    private Integer initField;
    private DatabaseReference databaseReference;
    private Showcase currentShowcase;
    private Location newLocation;
    private AlertDialog dialog;
    private LoadingDialog loadingDialog;
    private TextView field;
    private TextView descLength;
    private TextInputEditText nameEditText;
    private TextInputEditText descEditText;
    private TextInputEditText addressEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText websiteEditText;
    private TextInputEditText openingHoursEdittext;
    private int fieldIndex = 0;

    private final static int MAP_BUTTON_REQUEST_CODE = 2160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_info_settings);

        showcaseId = getIntent().getStringExtra("showcaseId");
        showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");

        // Reference à la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("showcases");
        loadingDialog = new LoadingDialog(this);

        new Handler().postDelayed(this::getShowcaseData, SHORT_DELAY);

        field = findViewById(R.id.field);
        descLength = findViewById(R.id.description_length);
        nameEditText = findViewById(R.id.name_edittext);
        descEditText = findViewById(R.id.description_edittext);
        addressEditText = findViewById(R.id.address_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        phoneEditText = findViewById(R.id.phone_edittext);
        websiteEditText = findViewById(R.id.website_edittext);
        openingHoursEdittext = findViewById(R.id.opening_hours_edittext);

        View.OnClickListener onFieldClickListener = v -> {

            CharSequence[] fields = new CharSequence[]{
                    getString(R.string.agro),
                    getString(R.string.information_science),
                    getString(R.string.fashion_and_clothing),
                    getString(R.string.communication),
                    getString(R.string.entertainment),
                    getString(R.string.education_and_training),
                    getString(R.string.finance_and_banking),
                    getString(R.string.transport_and_logistics),
                    getString(R.string.trade_and_distribution),
                    getString(R.string.public_services),
                    getString(R.string.hotel_business),
                    getString(R.string.health),
                    getString(R.string.professional_services),
                    getString(R.string.retail_sale),
                    getString(R.string.catering),
                    getString(R.string.other)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ShowcaseInfoSettingsActivity.this)
                    .setCancelable(true)
                    .setTitle(R.string.activity_category)
                    .setSingleChoiceItems(fields, fieldIndex, (dialogInterface, i) -> {

                        fieldIndex = i;
                        field.setText(getField(i > 7 ? i - 7 : i - 8));

                        dialogInterface.dismiss();

                    });
            dialog = builder.create();
            dialog.show();

        };

        field.setOnClickListener(onFieldClickListener);
        findViewById(R.id.field_layout).setOnClickListener(onFieldClickListener);

        openingHoursEdittext.setOnClickListener(v -> {

            startActivityForResult(new Intent(ShowcaseInfoSettingsActivity.this, ShowcaseOpeningHoursActivity.class)
                    .putExtra("showcasePrimaryColor", showcasePrimaryColor), RANDOM_VALUE);
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        addressEditText.setOnClickListener(v -> toLocationSelector());

        addressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                toLocationSelector();
            }
        });

        descEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newLength = s.length() + "/260";
                descLength.setText(newLength);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(showcasePrimaryColor));
        }

        if (getSupportActionBar() != null){
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.info));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    private void getShowcaseData() {

        databaseReference
                .child(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        currentShowcase = dataSnapshot.getValue(Showcase.class);

                        if (currentShowcase == null){
                            return;
                        }

                        initName = currentShowcase.getName() != null ? currentShowcase.getName() : EMPTY;
                        initDesc = currentShowcase.getDescription() != null ? currentShowcase.getDescription() : EMPTY;
                        initAddress = currentShowcase.getShowcaseContactDetails().getLocation().getAddress() != null
                                        ? currentShowcase.getShowcaseContactDetails().getLocation().getAddress() : EMPTY;
                        initEmail = currentShowcase.getShowcaseContactDetails().getEmail() != null
                                        ? currentShowcase.getShowcaseContactDetails().getEmail() : EMPTY;
                        initPhone = currentShowcase.getShowcaseContactDetails().getNumber() != null
                                        ? currentShowcase.getShowcaseContactDetails().getNumber() : EMPTY;
                        initWebsite = currentShowcase.getShowcaseContactDetails().getWebsite() != null
                                        ? currentShowcase.getShowcaseContactDetails().getWebsite() : EMPTY;
                        initOpeningHours = currentShowcase.getOpeningHours() != null ? currentShowcase.getOpeningHours()
                                                .split("<br>")[0]
                                                .replace("<h5>", EMPTY)
                                                .replace("</h5>", EMPTY) : EMPTY;
                        initField = currentShowcase.getField() != null ? currentShowcase.getField() : OTHER;

                        nameEditText.setText(initName);
                        descEditText.setText(initDesc);
                        emailEditText.setText(initEmail);
                        phoneEditText.setText(initPhone);
                        addressEditText.setText(initAddress);
                        websiteEditText.setText(initWebsite);
                        openingHoursEdittext.setText(initOpeningHours);

                        fieldIndex = initField > -1 ? initField + 7 : initField + 8;

                        field.setText(getField(initField));

                        newLocation = currentShowcase.getShowcaseContactDetails().getLocation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String getField(int field){

        String value = EMPTY;

        switch (field) {
            case AGRO:
                value = getString(R.string.agro);
                break;
            case INFORMATION_SCIENCE:
                value = getString(R.string.information_science);
                break;
            case FASHION_AND_CLOTHING:
                value = getString(R.string.fashion_and_clothing);
                break;
            case COMMUNICATION:
                value = getString(R.string.communication);
                break;
            case ENTERTAINMENT:
                value = getString(R.string.entertainment);
                break;
            case EDUCATION_AND_TRAINING:
                value = getString(R.string.education_and_training);
                break;
            case FINANCE_AND_BANKING:
                value = getString(R.string.finance_and_banking);
                break;
            case TRANSPORTS_AND_LOGISTICS:
                value = getString(R.string.transport_and_logistics);
                break;
            case TRADE_AND_DISTRIBUTION:
                value = getString(R.string.trade_and_distribution);
                break;
            case PUBLIC_SERVICES:
                value = getString(R.string.public_services);
                break;
            case HOTEL_BUSINESS:
                value = getString(R.string.hotel_business);
                break;
            case HEALTH:
                value = getString(R.string.health);
                break;
            case PROFESSIONAL_SERVICES:
                value = getString(R.string.professional_services);
                break;
            case RETAIL_SALE:
                value = getString(R.string.retail_sale);
                break;
            case CATERING:
                value = getString(R.string.catering);
                break;
            case OTHER:
                value = getString(R.string.other);
                break;
        }

        return value;
    }

    /**
     * Ouverture du sélecteur de localisation
     */
    private void toLocationSelector(){

        if (!Connectivity.getInstance(this).isOnline()){

            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            return;
        }

        startActivityForResult(
                new LocationPickerActivity.Builder()
                        .withLocation(Double.parseDouble(getString(R.string.default_Latitude)),
                                Double.parseDouble(getString(R.string.default_longitude)))
                        .withGeolocApiKey(getString(R.string.api_key))
                        .shouldReturnOkOnBackPressed()
                        .withSatelliteViewHidden()
                        .withGooglePlacesEnabled()
                        .withGoogleTimeZoneEnabled()
                        .withVoiceSearchHidden()
                        .build(getApplicationContext())
                , MAP_BUTTON_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) { return; }

        switch (requestCode){
            case RANDOM_VALUE:

                if(resultCode == RESULT_OK && data != null){
                    String optionTitle = data.getStringExtra("data")
                                             .split("<br>")[0]
                                             .replace("<h5>", EMPTY)
                                             .replace("</h5>", EMPTY);

                    openingHoursEdittext.setText(optionTitle);
                }
                break;

            case MAP_BUTTON_REQUEST_CODE:

                if (resultCode == RESULT_OK){

                    if (data == null){ return; }

                    // On met à jour l'EditText de localisation
                    final double latitude = data.getDoubleExtra(LATITUDE, 0.0),
                            longitude = data.getDoubleExtra(LONGITUDE, 0.0);
                    final String address = data.getStringExtra(LOCATION_ADDRESS),
                            postalcode = data.getStringExtra(ZIPCODE);

                    if (address.isEmpty()){

                        GeocodingSystem.getInstance(this).convertCoordinatesToAddress(latitude, longitude, new GeocodingSystemListener() {
                            @Override
                            public void onTaskStarted() { }

                            @Override
                            public void onTaskCompleted(@Nullable Address addresses) {

                                if (addresses != null) {
                                    addressEditText.setText(addresses.getAddressLine(0));
                                    newLocation = new Location(addresses.getAddressLine(0), latitude, longitude, addresses.getPostalCode());
                                }

                            }

                            @Override
                            public void onErrorOccurred() {

                                addressEditText.setText(getString(R.string.unknown_location));
                                newLocation = new Location(getString(R.string.unknown_location), latitude, longitude, postalcode);

                            }

                        });

                    }else {

                        addressEditText.setText(address);
                        newLocation = new Location(address, latitude, longitude, postalcode);

                    }

                }

                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.validate_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_validate && dataChange()){

            if (!TextUtils.isEmpty(nameEditText.getText().toString())
                && !TextUtils.isEmpty(descEditText.getText().toString())
                && !TextUtils.isEmpty(addressEditText.getText().toString())
                && ((!TextUtils.isEmpty(emailEditText.getText().toString())
                && validateMail(emailEditText.getText().toString()))
                || !TextUtils.isEmpty(phoneEditText.getText().toString()))){

                updateData();

            }else {
                Toast.makeText(this, R.string.insufficient_info, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateData() {

        loadingDialog.setMsg(getString(R.string.be_patient));
        loadingDialog.show();

        Showcase showcase = new Showcase(
                showcaseId,
                nameEditText.getText().toString().trim(),
                descEditText.getText().toString().trim(),
                fieldIndex > 7 ? fieldIndex - 7 : fieldIndex - 8,
                currentShowcase.getBanner(),
                currentShowcase.getLogo(),
                new ShowcaseContactDetails(emailEditText.getText().toString(),
                                           phoneEditText.getText().toString(),
                                           websiteEditText.getText().toString(),
                                           newLocation),
                currentShowcase.getOwners(),
                currentShowcase.getPrimaryColor(),
                currentShowcase.getSecondaryColor()
        );

        // Mise à jour de l'objet dans la base de données
        databaseReference
                .child(showcaseId)
                .setValue(showcase)
                .addOnSuccessListener(aVoid -> {

                    if (loadingDialog.isShowing()){  loadingDialog.dismiss(); }

                    finish();
                    overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    if (loadingDialog.isShowing()){  loadingDialog.dismiss(); }
                });
    }

    private boolean dataChange(){

        if (currentShowcase == null){ return false; }

        return nameEditText.getText() != null && !nameEditText.getText().toString().equals(initName)
                || descEditText.getText() != null && !descEditText.getText().toString().equals(initDesc)
                || field.getText() != null && !field.getText().toString().equals(getField(initField))
                || addressEditText.getText() != null && !addressEditText.getText().toString().equals(initAddress)
                || emailEditText.getText() != null && !emailEditText.getText().toString().equals(initEmail)
                || phoneEditText.getText() != null && !phoneEditText.getText().toString().equals(initPhone)
                || websiteEditText.getText() != null && !websiteEditText.getText().toString().equals(initWebsite)
                || openingHoursEdittext.getText() != null && !openingHoursEdittext.getText().toString().equals(initOpeningHours);
    }

    /**
     * Dialogue d'annulation des modifications
     */
    private void showCancelModificationsDialog(){

        // Dialogue de confirmation de l'opération
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.modify_profile))
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
