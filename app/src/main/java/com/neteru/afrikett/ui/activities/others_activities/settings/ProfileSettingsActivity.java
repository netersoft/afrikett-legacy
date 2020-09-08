package com.neteru.afrikett.ui.activities.others_activities.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.interfaces.GeocodingSystemListener;
import com.neteru.afrikett.core.models.RemoteDB.Location;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AppUtilities;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.core.utilities.GeocodingSystem;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.schibstedspain.leku.LocationPickerActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.UUID;

import static com.neteru.afrikett.core.utilities.AppUtilities.Uri2Bitmap;
import static com.neteru.afrikett.core.utilities.AppUtilities.choosePhotoFromGallery;
import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setStringPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.takePhotoFromCamera;
import static com.neteru.afrikett.core.utilities.Constants.CAMERA;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.GALLERY;
import static com.neteru.afrikett.core.utilities.Constants.UNSPECIFIED;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.ZIPCODE;

public class ProfileSettingsActivity extends AfrikettBaseActivity {

    private String currentName;
    private String currentPictureUrl;
    private String currentBio;
    private String currentBirthday;
    private String newPictureUrl;
    private String newBirthday;
    private TextInputEditText locationEditText;
    private TextInputEditText nameEditText;
    private TextInputEditText bioEditText;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;
    private final static int MAP_BUTTON_REQUEST_CODE = 2160;
    private final static String TAG = "Profile Settings";
    private Location currentLocation;
    private Location newLocation;
    private StorageReference storageReference;
    private TextView bioLength;
    private TextView birthday;
    private TextView sex;
    private CharSequence[] sexModalities;
    private LoadingDialog loadingDialog;
    private int currentSex;
    private int newSex;
    private ImageView imgProfil;
    private Uri newPictureURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Nouvel URI
        newPictureURI = Uri.EMPTY;

        // Valeurs courantes
        currentBio = EMPTY;
        currentName = EMPTY;
        currentBirthday = EMPTY;
        currentSex = UNSPECIFIED;
        currentPictureUrl = EMPTY;
        currentLocation = new Location();

        sexModalities = new CharSequence[]{getString(R.string.unspecified), getString(R.string.male), getString(R.string.female)};

        sex = findViewById(R.id.sex);
        birthday = findViewById(R.id.birthday);
        imgProfil = findViewById(R.id.imgProfil);
        bioLength = findViewById(R.id.bio_length);
        bioEditText = findViewById(R.id.bio_edittext);
        nameEditText = findViewById(R.id.name_edittext);
        locationEditText = findViewById(R.id.location_edittext);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setMsg(getString(R.string.be_patient));

        // Reference générale à la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        // Reference à l'utilisateur dans la base de données
        userDatabaseReference = databaseReference.child("users").child(getLocalUserData(this).getId());

        // Reference à la base de stockage des photos de profil
        storageReference = FirebaseStorage.getInstance()
                .getReference("users")
                .child("profiles")
                .child(getLocalUserData(this).getId()+"/"+ UUID.randomUUID().toString());

        // Chargement des données courantes de l'utilisateur
        loadUserData();

        // Ouverture du sélecteur de localisation
        View.OnClickListener onLocationClick = v -> toLocationSelector(),

        // Ouverture du spinner de date
        onBirthdayClick = v -> new SpinnerDatePickerDialogBuilder()
                .context(ProfileSettingsActivity.this)
                .callback((view, year, monthOfYear, dayOfMonth) -> {

                    newBirthday = formatDayAndMonth(dayOfMonth)
                                    + "/" + formatDayAndMonth(monthOfYear + 1)
                                    + "/" + year;

                    birthday.setText(newBirthday);
                })
                .spinnerTheme(R.style.DatePickerStyle)
                .showTitle(false)
                .showDaySpinner(true)
                .defaultDate(2000, 0, 1)
                .maxDate(2010, 0, 1)
                .minDate(1950, 0, 1)
                .build()
                .show(),

        // Ouverture du sélecteur de genre
        onSexClick = v -> new AlertDialog.Builder(ProfileSettingsActivity.this)
                .setCancelable(true)
                .setSingleChoiceItems(sexModalities, newSex, (dialogInterface, i) -> {

                    newSex = i;
                    sex.setText(sexModalities[i]);

                    dialogInterface.dismiss();
                })
                .show();

        // Branchement des écouteurs d'évènements
        locationEditText.setOnClickListener(onLocationClick);
        findViewById(R.id.location_layout).setOnClickListener(onLocationClick);

        locationEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                toLocationSelector();
            }
        });

        sex.setOnClickListener(onSexClick);
        findViewById(R.id.sex_layout).setOnClickListener(onSexClick);

        birthday.setOnClickListener(onBirthdayClick);
        findViewById(R.id.birthday_layout).setOnClickListener(onBirthdayClick);

        // Compteur de chaîne de caractère de la bio
        bioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newLength = s.length() + "/140";
                bioLength.setText(newLength);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Ouverture du sélecteur galerie / camera
        findViewById(R.id.fabProfil).setOnClickListener(v -> showProfilePicDialog());

        // Défilement vers le haut par défaut
        ((ScrollView) findViewById(R.id.scroll)).smoothScrollTo(0,0);

        if (getSupportActionBar() != null) {

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.modify_profile));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        }

    }

    /**
     * Formate les valeurs de jour et de mois de la date d'anniversaire
     * @param in / valeur en entrée
     * @return valeur en sortie
     */
    public static String formatDayAndMonth(int in){
        if (String.valueOf(in).length() == 1){

            return "0"+in;
        }

        return String.valueOf(in);
    }

    /**
     * Ouverture du sélecteur galerie / caméra
     */
    private void showProfilePicDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.change_profile_picture));

        String[] pictureDialogItems;
        pictureDialogItems = new String[]{
                                        getString(R.string.pick_in_gallery),
                                        getString(R.string.take_picture)};

        // Si la photo de profil est modifiable
        // On ajoute la commande de suppression au sélectur
        if (!currentPictureUrl.equals(DEFAULT) || newPictureURI != Uri.EMPTY){

            pictureDialogItems = new String[]{
                                        getString(R.string.pick_in_gallery),
                                        getString(R.string.take_picture),
                                        getString(R.string.rm_picture)};

        }

        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            ActivityCompat.requestPermissions(ProfileSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_READ_PERMISSION);

                            break;
                        case 1:
                            ActivityCompat.requestPermissions(ProfileSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_WRITE_PERMISSION);

                            break;
                        case 2:
                            // En cas de suppression on remet tous à défaut
                            newPictureURI = Uri.EMPTY;
                            newPictureUrl = DEFAULT;

                            // Puis on charge le textDrawable par défaut
                            loadProfilePicture(currentPictureUrl);

                            break;
                    }
                });

        pictureDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                case 1:
                    choosePhotoFromGallery(this);
                    break;


                case 2:
                    takePhotoFromCamera(this);
                    break;
            }

        } else {

            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "CANCELLED");
            return;
        }

        switch (requestCode){

            case GALLERY: // Résultat de l'ouverture de la galerie

                if (data != null) { // Si les données sont effectivement recueillies

                    Uri contentURI_1 = data.getData(); // On recupère l'URI

                    // On ouvre le modificateur d'images
                    CropImage.activity(contentURI_1)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setFixAspectRatio(true)
                            .start(this);

                }
                break;

            case CAMERA: // Résultat de l'ouverture de la camera

                if (data != null && data.getExtras() != null) {

                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                    // On recupère l'URI
                    if (thumbnail == null){ return; }
                    Uri contentURI_2 = AppUtilities.saveImage(this, thumbnail, null);

                    // On ouvre le modificateur d'images
                    CropImage.activity(contentURI_2)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setFixAspectRatio(true)
                            .start(this);

                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: // Résultat du modificateur d'images

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) { // Si tout s'est bien passée,

                    // On déclare la variable globale avec la nouvelle URI
                    newPictureURI = result.getUri();

                    // On charge l'URI
                    Glide
                            .with(ProfileSettingsActivity.this)
                            .load(Uri2Bitmap(this, newPictureURI))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgProfil);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) { // Sinon si une erreur s'est produite,

                    Toast.makeText(this, getString(R.string.loading_failure), Toast.LENGTH_SHORT).show();

                    result.getError().printStackTrace();

                }
                break;

            case MAP_BUTTON_REQUEST_CODE:

                // Si les données sont vides, on met fin à l'opération
                if (data == null){ return; }

                // Si le résultat de l'opération est positif
                if (resultCode == RESULT_OK) {

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
                                    locationEditText.setText(addresses.getAddressLine(0));
                                    newLocation = new Location(addresses.getAddressLine(0), latitude, longitude, addresses.getPostalCode());
                                }

                            }

                            @Override
                            public void onErrorOccurred() {

                                locationEditText.setText(getString(R.string.unknown_location));
                                newLocation = new Location(getString(R.string.unknown_location), latitude, longitude, postalcode);

                            }

                        });

                    }else {

                        locationEditText.setText(address);
                        newLocation = new Location(address, latitude, longitude, postalcode);

                    }

                }

                break;

        }

    }

    /**
     * Chargement de la photo de profil
     * @param url / lien de la photo de profil
     */
    private void loadProfilePicture(String url) {

        if (url.equals(DEFAULT)) { // Si photo de profil par défaut

            imgProfil.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(currentName), COLORS[getDigitFromString(currentName)]));

        }else { // Sinon

            Glide
                    .with(ProfileSettingsActivity.this)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfil);

        }

    }

    /**
     * Chargement des données de l'utilisateur
     */
    private void loadUserData() {

        databaseReference
                .child("users")
                .child(getLocalUserData(this).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        // Objet contenant les données de l'utilisateur courant
                        User currentUser = dataSnapshot.getValue(User.class);

                        if (currentUser == null){
                            return;
                        }

                        // Sauvegarde des données dans des variables globales
                        currentSex = currentUser.getSex();
                        currentBio = currentUser.getBio() != null ? currentUser.getBio() : EMPTY;
                        currentName = currentUser.getName() != null ? currentUser.getName() : EMPTY;
                        currentBirthday = currentUser.getBirthday() != null ? currentUser.getBirthday() : EMPTY;
                        currentPictureUrl = currentUser.getProfileUrl() != null ? currentUser.getProfileUrl() : EMPTY;
                        currentLocation = currentUser.getLocation() != null ? currentUser.getLocation() : new Location();

                        newSex = currentSex;
                        newBirthday = currentBirthday;
                        newLocation = currentLocation;
                        newPictureUrl = currentPictureUrl;

                        // Chargement de la photo de profil
                        loadProfilePicture(currentPictureUrl);

                        // Remplissage des champs de texte
                        birthday.setText(currentBirthday.equals(EMPTY) ? getString(R.string.add_ur_anniversary): currentBirthday);
                        sex.setText(sexModalities[currentSex]);
                        nameEditText.setText(currentName);
                        bioEditText.setText(currentBio);

                        // Chargement de la localisation
                        String coordinates = (currentLocation.getLatitude() != null ? currentLocation.getLatitude() : "0.0") +
                                                " - "+
                                             (currentLocation.getLongitude() != null ? currentLocation.getLongitude() : "0.0");

                        locationEditText.setText(
                                currentLocation.getAddress() != null && !currentLocation.getAddress().isEmpty()
                                        ? currentLocation.getAddress()
                                        : (coordinates.equals("0.0 - 0.0")
                                                        ? EMPTY
                                                        : coordinates));

                        // Chargement de la longueur initiale de la bio
                        String initialLength = bioEditText.getText() != null
                                               ? bioEditText.getText().toString().length() + "/140"
                                               : "0/140";

                        bioLength.setText(initialLength);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.validate_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Click sur le bouton de mise à jour des données
        if (item.getItemId() == R.id.action_validate && dataChange()){

            if (Connectivity.getInstance(this).isOnline()){
                checkData();
            }else {
                Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Vérification des données
     */
    public void checkData(){

        // Si le champ nom est vide on arrête tout
        if (nameEditText.getText() != null && TextUtils.isEmpty(nameEditText.getText().toString())){

            Toast.makeText(this, getString(R.string.name_cant_be_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        // Dialogue de confirmation de l'opération
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.update_settings_dialog_msg))
                .setPositiveButton(R.string.ok, (dialog, which) -> {

                    loadingDialog.show();

                    // S'il y a une URI en attente
                    if (newPictureURI != Uri.EMPTY){
                        uploadProfilePic(); // On l'upload d'abord
                    }else { // Sinon
                        updateBaseProfilePic(newPictureUrl); // On met à jour la base
                    }

                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    /**
     * Upload de la nouvelle photo de profil
     */
    private void uploadProfilePic(){

        storageReference
                .putFile(newPictureURI)
                .addOnSuccessListener(taskSnapshot -> {

                    // Récupération du lien de la nouvelle photo de profil
                    storageReference
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {

                                // On met à jour la base avec le nouvel url
                                newPictureUrl = uri.toString();
                                updateBaseProfilePic(newPictureUrl);

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();

                    loadingDialog.dismiss();
                });
    }

    /**
     * Mise à jour de l'url de photo de profil dans la base
     * @param url / url de la photo de profil
     */
    private void updateBaseProfilePic(String url){

        userDatabaseReference
                .child("profileUrl")
                .setValue(url)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        loadingDialog.dismiss();
                        return;
                    }

                    updateBaseBio();
                });

    }

    /**
     * Mise à jour de la bio dans la base
     */
    private void updateBaseBio() {

        userDatabaseReference
                .child("bio")
                .setValue(bioEditText.getText() != null ? bioEditText.getText().toString().trim() : EMPTY)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        loadingDialog.dismiss();
                        return;
                    }

                    updateBaseLocation();
                });
    }

    /**
     * Mise à jour de la localisation dans la base
     */
    private void updateBaseLocation() {

        userDatabaseReference
                .child("location")
                .setValue(newLocation)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        loadingDialog.dismiss();
                        return;
                    }

                    updateBaseName();
                });
    }

    /**
     * Mise à jour du nom dans la base
     */
    private void updateBaseName() {

        userDatabaseReference
                .child("name")
                .setValue(nameEditText.getText() != null ? nameEditText.getText().toString().trim() : EMPTY)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        loadingDialog.dismiss();
                        return;
                    }

                    updateBaseSex();
                });
    }

    /**
     * Mise à jour du genre dans la base
     */
    private void updateBaseSex() {

        userDatabaseReference
                .child("sex")
                .setValue(newSex)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        loadingDialog.dismiss();
                        return;
                    }

                    updateBaseBirthday();
                });
    }

    /**
     * Mise à jour de l'anniversaire dans la base
     */
    private void updateBaseBirthday() {

        userDatabaseReference
                .child("birthday")
                .setValue(newBirthday)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()){

                        loadingDialog.dismiss();
                        return;
                    }

                    updateLocalData();
                });
    }

    /**
     * Mise à jour des données locales
     */
    private void updateLocalData() {

        // Mise à jour du nom
        setStringPreference(this,
                                    Constants.USER_PREFS,
                               "name", nameEditText.getText() != null
                                                ? nameEditText.getText().toString().trim()
                                                : EMPTY);

        loadingDialog.dismiss();

        finish();
        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
    }

    /**
     * Vérifie si les données ont subi des modifications
     * @return / booleen
     */
    private boolean dataChange(){
        String bio = bioEditText.getText() != null ? bioEditText.getText().toString().trim() : EMPTY,
               name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : EMPTY;

        return !currentName.trim().equals(name)
                    || !currentBio.trim().equals(bio)
                    || !currentPictureUrl.equals(newPictureUrl)
                    || !currentBirthday.equals(newBirthday)
                    || currentLocation != newLocation
                    || newPictureURI != Uri.EMPTY
                    || currentSex != newSex;
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
