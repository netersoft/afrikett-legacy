package com.neteru.afrikett.ui.activities.showcase_activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.interfaces.GeocodingSystemListener;
import com.neteru.afrikett.core.models.RemoteDB.Location;
import com.neteru.afrikett.core.utilities.AppUtilities;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.GeocodingSystem;
import com.schibstedspain.leku.LocationPickerActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.hideKeyboard;
import static com.neteru.afrikett.core.utilities.AppUtilities.requestEditTextFocus;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.SHORT_DELAY;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

/**
 * Showcase Registration : Adresse, Email, Telephone
 */
public class ThirdStepActivity extends AfrikettBaseActivity {
    private EditText addressEditText, emailEditText, telEditText, websiteEditText;
    private final static int MAP_BUTTON_REQUEST_CODE = 2160;
    private String name, description;
    private Location newLocation;
    private int field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_step);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        newLocation = new Location();

        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        field = getIntent().getIntExtra("field", 0);

        websiteEditText = findViewById(R.id.website);
        addressEditText = findViewById(R.id.address);
        emailEditText = findViewById(R.id.email);
        telEditText = findViewById(R.id.tel);

        findViewById(R.id.address_icon).setOnClickListener(v -> requestEditTextFocus(ThirdStepActivity.this, addressEditText));

        findViewById(R.id.email_icon).setOnClickListener(v -> requestEditTextFocus(ThirdStepActivity.this, emailEditText));

        findViewById(R.id.phone_icon).setOnClickListener(v -> requestEditTextFocus(ThirdStepActivity.this, telEditText));

        findViewById(R.id.website_icon).setOnClickListener(v -> requestEditTextFocus(ThirdStepActivity.this, websiteEditText));

        addressEditText.setOnClickListener(v -> toLocationSelector());

        addressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                toLocationSelector();
            }
        });

        findViewById(R.id.next).setOnClickListener(view -> {

            if (!addressEditText.getText().toString().isEmpty()){ // Si adresse physique fournie,

                if (!emailEditText.getText().toString().isEmpty() || !telEditText.getText().toString().isEmpty()){ // Si email ou phone fournie,

                    if (!emailEditText.getText().toString().isEmpty()){ // Si email fournie,

                        if (AppUtilities.validateMail(emailEditText.getText().toString())){ // Si email valide,

                            next();

                        }else{

                            Toast.makeText(ThirdStepActivity.this, R.string.invalid_email, Toast.LENGTH_SHORT).show();

                        }
                    }else{

                        next();

                    }



                }else {

                    Toast.makeText(ThirdStepActivity.this, getString(R.string.insufficient_info), Toast.LENGTH_SHORT).show();

                }

            }else {
                Toast.makeText(ThirdStepActivity.this, getString(R.string.missing_info), Toast.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        }
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

        if (requestCode == MAP_BUTTON_REQUEST_CODE){

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

        }
    }

    private void next(){

        hideKeyboard(ThirdStepActivity.this);

        new Handler().postDelayed(() -> {

            startActivity(new Intent(ThirdStepActivity.this ,FourthStepActivity.class)
                    .putExtra("name", name)
                    .putExtra("field", field)
                    .putExtra("description", description)
                    .putExtra("address", newLocation.getAddress())
                    .putExtra("latitude", newLocation.getLatitude())
                    .putExtra("longitude", newLocation.getLongitude())
                    .putExtra("postalCode", newLocation.getPostalCode())
                    .putExtra("email", emailEditText.getText().toString())
                    .putExtra("website", websiteEditText.getText().toString())
                    .putExtra("tel", telEditText.getText().toString()));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        }, SHORT_DELAY / 3);


    }
}
