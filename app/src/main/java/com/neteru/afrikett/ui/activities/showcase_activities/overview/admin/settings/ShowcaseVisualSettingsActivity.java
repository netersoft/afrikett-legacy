package com.neteru.afrikett.ui.activities.showcase_activities.overview.admin.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Location;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.ShowcaseContactDetails;
import com.neteru.afrikett.core.utilities.AppUtilities;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.utilities.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import static com.neteru.afrikett.core.utilities.AppUtilities.Uri2Bitmap;
import static com.neteru.afrikett.core.utilities.AppUtilities.choosePhotoFromGallery;
import static com.neteru.afrikett.core.utilities.AppUtilities.getColorHex;
import static com.neteru.afrikett.core.utilities.AppUtilities.takePhotoFromCamera;
import static com.neteru.afrikett.core.utilities.Constants.CAMERA;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.GALLERY;

public class ShowcaseVisualSettingsActivity extends AfrikettBaseActivity
                                            implements CardView.OnClickListener, ColorPickerDialogListener {

    private String showcaseId;
    private String primaryColor = EMPTY;
    private String secondaryColor = EMPTY;
    private View pColor;
    private View sColor;
    private int indicator;
    private final static int DIALOG_1 = 0;
    private final static int DIALOG_2 = 1;
    private ImageView banner;
    private ImageView action_logo;
    private ImageView action_banner;
    private Uri fileURILogo;
    private Uri fileURIBanner;
    private RoundedImageView logo;
    private StorageReference bannerStorageReference;
    private StorageReference logoStorageReference;
    private DatabaseReference databaseReference;
    private LoadingDialog loadingDialog;
    private boolean initLogoUrlExist = false;
    private boolean initBannerUrlExist = false;
    private boolean erasedImg = false;
    private Showcase currentShowcase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_visual_settings);

        showcaseId = getIntent().getStringExtra("showcaseId");

        // Reference à la base de données
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(DATABASE_ROOT)
                .child("showcases");

        // Reference à la base de stockage des bannières
        bannerStorageReference = FirebaseStorage.getInstance()
                .getReference("showcases")
                .child("banners/"+ UUID.randomUUID().toString());

        // Reference à la base de stockage des logos
        logoStorageReference = FirebaseStorage.getInstance()
                .getReference("showcases")
                .child("logos/"+UUID.randomUUID().toString());

        CardView pColorCard = findViewById(R.id.first_cardView),
                sColorCard = findViewById(R.id.second_cardView);

        loadingDialog = new LoadingDialog(this);

        pColorCard.setOnClickListener(this);
        sColorCard.setOnClickListener(this);

        pColor = findViewById(R.id.pColor);
        sColor = findViewById(R.id.sColor);
        logo = findViewById(R.id.logo);
        banner = findViewById(R.id.banner);
        action_logo = findViewById(R.id.action_logo);
        action_banner = findViewById(R.id.action_banner);

        loadDefaultData();

        // Ecouteur de click sur l'espace logo
        logo.setOnClickListener(view -> {
            if (fileURILogo != null && !fileURILogo.equals(Uri.EMPTY) || initLogoUrlExist){ // Si un URI de logo est fournie

                // On remet les pendules à zero en vidant la variable globale contenant l'URI logo
                // On vide l'espace logo et on remet l'icône par defaut
                fileURILogo = Uri.EMPTY;
                action_logo.setImageResource(R.mipmap.ic_camera_alt_white_24dp);
                logo.setImageDrawable(null);

                initLogoUrlExist = false;
                erasedImg = true;

            }else { // Sinon,

                // Indicateur à zero et ouverture du dialogue de sélection d'images
                indicator = 0;
                showLogoDialog();

            }

        });

        banner.setOnClickListener(view -> {
            if (fileURIBanner != null && !fileURIBanner.equals(Uri.EMPTY) || initBannerUrlExist){ // Si un URI de bannière est fournie

                // On remet les pendules à zero en vidant la variable globale contenant l'URI bannière
                // On vide l'espace bannière et on remet l'icône par defaut
                fileURIBanner = Uri.EMPTY;
                action_banner.setImageResource(R.mipmap.ic_camera_alt_white_24dp);
                banner.setImageDrawable(null);

                initBannerUrlExist = false;
                erasedImg = true;

            }else { // Sinon

                // Indicateur à un et ouverture du dialogue de sélection d'images
                indicator = 1;
                showBannerDialog();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(getIntent().getStringExtra("showcasePrimaryColor")));
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
            Spannable title = new SpannableString(getString(R.string.visual_identity));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    private void loadDefaultData() {

        databaseReference
                .child(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        currentShowcase = dataSnapshot.getValue(Showcase.class);

                        if (currentShowcase == null){ return; }

                        primaryColor = currentShowcase.getPrimaryColor();
                        secondaryColor = currentShowcase.getSecondaryColor();

                        pColor.setBackgroundColor(Color.parseColor(primaryColor));
                        sColor.setBackgroundColor(Color.parseColor(secondaryColor));

                        if (!currentShowcase.getLogo().equals(DEFAULT)) {

                            Glide
                                    .with(ShowcaseVisualSettingsActivity.this)
                                    .load(currentShowcase.getLogo())
                                    .into(logo);

                            initLogoUrlExist = true;
                            action_logo.setImageResource(R.mipmap.ic_clear_white_24dp);

                        }

                        if (!currentShowcase.getBanner().equals(DEFAULT)) {

                            Glide
                                    .with(ShowcaseVisualSettingsActivity.this)
                                    .load(currentShowcase.getBanner())
                                    .into(banner);

                            initBannerUrlExist = true;
                            action_banner.setImageResource(R.mipmap.ic_clear_white_24dp);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Boîte de dialogue logo
     */
    public void showLogoDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.add_logo));

        String[] pictureDialogItems = {
                getString(R.string.pick_in_gallery),
                getString(R.string.order_from_a_designer)};

        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            ActivityCompat.requestPermissions(ShowcaseVisualSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_READ_PERMISSION);

                            break;
                        case 1:
                            Toast.makeText(ShowcaseVisualSettingsActivity.this, getString(R.string.this_feature_will_be_available_soon), Toast.LENGTH_SHORT).show();

                            break;
                    }
                });

        pictureDialog.show();
    }

    /**
     * Boîte de dialogue bannière
     */
    public void showBannerDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.add_banner));

        String[] pictureDialogItems = {
                getString(R.string.pick_in_gallery),
                getString(R.string.take_picture),
                getString(R.string.order_from_a_designer)};

        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            ActivityCompat.requestPermissions(ShowcaseVisualSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_READ_PERMISSION);

                            break;
                        case 1:
                            ActivityCompat.requestPermissions(ShowcaseVisualSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_WRITE_PERMISSION);

                            break;
                        case 2:
                            Toast.makeText(ShowcaseVisualSettingsActivity.this, getString(R.string.this_feature_will_be_available_soon), Toast.LENGTH_SHORT).show();

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
                    choosePhotoFromGallery(ShowcaseVisualSettingsActivity.this);
                    break;


                case 2:
                    takePhotoFromCamera(ShowcaseVisualSettingsActivity.this);
                    break;
            }

        } else {

            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) { return; }

        switch (requestCode){

            case GALLERY: // Résultat de l'ouverture de la galerie

                if (data != null) { // Si les données sont effectivement recueillies

                    Uri contentURI_1 = data.getData(); // On recupère l'URI

                    if (indicator == 0){

                        // Si l'indicateur est à zero il s'agit d'un URI logo
                        // On ouvre le modificateur d'images
                        CropImage.activity(contentURI_1)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .setFixAspectRatio(true)
                                .start(this);
                    }else {

                        // Sinon il s'agit d'un URI bannière
                        // On ouvre le modificateur d'images
                        CropImage.activity(contentURI_1)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true)
                                .start(this);
                    }
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
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setFixAspectRatio(true)
                            .start(this);

                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: // Résultat du modificateur d'images

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) { // Si tout s'est bien passée,

                    // On recupère l'uri de l'image modifiée
                    Uri resultUri = result.getUri();

                    if (indicator == 0){

                        // On fournie la variable globale de l'URI logo
                        fileURILogo = resultUri;
                        // On affiche le résultat
                        Glide
                                .with(this)
                                .load(Uri2Bitmap(ShowcaseVisualSettingsActivity.this, fileURILogo))
                                .into(logo);
                        action_logo.setImageResource(R.mipmap.ic_clear_white_24dp);

                        erasedImg = false;

                    }else {

                        // On fournie la variable globale de l'URI bannière
                        fileURIBanner = resultUri;
                        // On affiche le résultat
                        Glide
                                .with(this)
                                .load(Uri2Bitmap(ShowcaseVisualSettingsActivity.this, fileURIBanner))
                                .into(banner);
                        action_banner.setImageResource(R.mipmap.ic_clear_white_24dp);

                        erasedImg = false;

                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) { // Sinon si une erreur s'est produite,

                    Toast.makeText(ShowcaseVisualSettingsActivity.this, getString(R.string.loading_failure), Toast.LENGTH_SHORT).show();

                    result.getError().printStackTrace();

                }
                break;
        }

    }


    /**
     * Méthode invoquée lors du click des cardView couleur
     * @param view / cardView couleur
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.first_cardView:

                // Boîte de dialogue de selection de la couleur primaire
                ColorPickerDialog
                        .newBuilder()
                        .setColor(R.color.skyblue)
                        .setDialogId(DIALOG_1) // Identifiant dialogue couleur primaire
                        .setDialogTitle(R.string.primary_color)
                        .show(ShowcaseVisualSettingsActivity.this);
                break;

            case R.id.second_cardView:

                // Boîte de dialogue de selection de la couleur secondaire
                ColorPickerDialog
                        .newBuilder()
                        .setColor(R.color.skyblue)
                        .setDialogId(DIALOG_2) // Identifiant dialogue couleur secondaire
                        .setDialogTitle(R.string.secondary_color)
                        .show(ShowcaseVisualSettingsActivity.this);
                break;
        }
    }

    /**
     * Méthode invoquée lors du choix d'une couleur
     * @param dialogId / Identifiant de la boîte de dialogue de sélection de couleur
     * @param color / Valeur entière de la couleur
     */
    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == DIALOG_1){ // Si couleur primaire

            pColor.setBackgroundColor(color);

            // Récupération de la valeur hexadécimale de la couleur
            primaryColor = getColorHex(color);

        }else { // Sinon

            sColor.setBackgroundColor(color);

            // Récupération de la valeur hexadécimale de la couleur
            secondaryColor = getColorHex(color);

        }
    }

    /**
     * Méthode invoquée lors de la disparition de la boîte de dialogue
     * @param dialogId / Identifiant de la boîte de dialogue de sélection de couleur
     */
    @Override
    public void onDialogDismissed(int dialogId) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.validate_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_validate){
            if (dataChange()){ update();}
        }

        return super.onOptionsItemSelected(item);
    }

    private void update(){

        loadingDialog.setMsg(getString(R.string.be_patient));
        loadingDialog.show();

        uploadBanner();

    }

    /**
     * Téléversement de la bannière
     */
    private void uploadBanner(){

        if (fileURIBanner != null && !fileURIBanner.equals(Uri.EMPTY)) {

            bannerStorageReference
                    .putFile(fileURIBanner)
                    .addOnSuccessListener(taskSnapshotBanner -> bannerStorageReference
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> uploadLogo(uri.toString())))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        if (loadingDialog.isShowing()){  loadingDialog.dismiss(); }
                    });

        }else {
            uploadLogo(DEFAULT);
        }
    }

    /**
     * Téléversement du logo
     * @param bannerUrl / Url de téléchargement de la bannière
     */
    private void uploadLogo(final String bannerUrl){

        if (fileURILogo != null && !fileURILogo.equals(Uri.EMPTY)) {

            logoStorageReference
                    .putFile(fileURILogo)
                    .addOnSuccessListener(taskSnapshot -> logoStorageReference
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> saveData(uri.toString(), bannerUrl)))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        if (loadingDialog.isShowing()){  loadingDialog.dismiss(); }
                    });

        }else{
            saveData(DEFAULT, bannerUrl);
        }
    }

    /**
     * Mise à jour des données fournies
     * @param logoUrl / Url de téléchargement du logo
     * @param bannerUrl / Url de téléchargement de la bannière
     */
    private void saveData(String logoUrl, String bannerUrl){

        // Instanciation de l'objet vitrine
        Showcase showcase = new Showcase(
                currentShowcase.getId(),
                currentShowcase.getName(),
                currentShowcase.getDescription(),
                currentShowcase.getField(),
                bannerUrl,
                logoUrl,
                new ShowcaseContactDetails(currentShowcase.getShowcaseContactDetails().getEmail(),
                                           currentShowcase.getShowcaseContactDetails().getNumber(),
                                           currentShowcase.getShowcaseContactDetails().getWebsite(),
                        new Location(currentShowcase.getShowcaseContactDetails().getLocation().getAddress(),
                                     currentShowcase.getShowcaseContactDetails().getLocation().getLatitude(),
                                     currentShowcase.getShowcaseContactDetails().getLocation().getLongitude(),
                                     currentShowcase.getShowcaseContactDetails().getLocation().getPostalCode())),
                currentShowcase.getOwners(),
                primaryColor,
                secondaryColor
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

        return fileURILogo != null && !fileURILogo.equals(Uri.EMPTY) ||
                fileURIBanner != null && !fileURIBanner.equals(Uri.EMPTY) ||
                    !primaryColor.equals(currentShowcase.getPrimaryColor()) ||
                    !secondaryColor.equals(currentShowcase.getSecondaryColor()) ||
                        erasedImg;
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
