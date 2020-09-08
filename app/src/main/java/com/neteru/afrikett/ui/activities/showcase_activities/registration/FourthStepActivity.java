package com.neteru.afrikett.ui.activities.showcase_activities.registration;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.neteru.afrikett.ui.activities.main_activities.HomeActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.ManageActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.neteru.afrikett.core.utilities.AppUtilities.Uri2Bitmap;
import static com.neteru.afrikett.core.utilities.AppUtilities.choosePhotoFromGallery;
import static com.neteru.afrikett.core.utilities.AppUtilities.generateKey;
import static com.neteru.afrikett.core.utilities.AppUtilities.getColorHex;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.takePhotoFromCamera;
import static com.neteru.afrikett.core.utilities.Constants.CAMERA;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.GALLERY;

/**
 * Showcase Registration : Logo, Bannière, Couleur Primaire, Couleur Secondaire
 */
public class FourthStepActivity extends AfrikettBaseActivity
                                implements CardView.OnClickListener, ColorPickerDialogListener {

    private View pColor;
    private View sColor;
    private final static int DIALOG_1 = 0;
    private final static int DIALOG_2 = 1;
    private int indicator;
    private int field;
    private ImageView banner;
    private ImageView action_logo;
    private ImageView action_banner;
    private Uri fileURILogo;
    private Uri fileURIBanner;
    private RoundedImageView logo;
    private String name;
    private String description;
    private String address;
    private String email;
    private String tel;
    private String primaryColor;
    private String secondaryColor;
    private String postalCode;
    private String website;
    private String userId;
    private double latitude;
    private double longitude;
    private StorageReference bannerStorageReference;
    private StorageReference logoStorageReference;
    private DatabaseReference databaseReference;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth_step);

        // Reference à la base de données
        databaseReference = FirebaseDatabase.getInstance()
                                    .getReference(DATABASE_ROOT)
                                    .child("showcases");

        // Reference à la base de stockage des bannières
        bannerStorageReference = FirebaseStorage.getInstance()
                                    .getReference("showcases")
                                    .child("banners/"+UUID.randomUUID().toString());

        // Reference à la base de stockage des logos
        logoStorageReference = FirebaseStorage.getInstance()
                                    .getReference("showcases")
                                    .child("logos/"+UUID.randomUUID().toString());

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

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

        // Ecouteur de click sur l'espace logo
        logo.setOnClickListener(view -> {
            if (fileURILogo != null && !fileURILogo.equals(Uri.EMPTY) ){ // Si un URI de logo est fournie

                // On remet les pendules à zero en vidant la variable globale contenant l'URI logo
                // On vide l'espace logo et on remet l'icône par defaut
                fileURILogo = Uri.EMPTY;
                action_logo.setImageResource(R.mipmap.ic_camera_alt_white_24dp);
                logo.setImageDrawable(null);

            }else { // Sinon,

                // Indicateur à zero et ouverture du dialogue de sélection d'images
                indicator = 0;
                showLogoDialog();

            }

        });

        banner.setOnClickListener(view -> {
            if (fileURIBanner != null && !fileURIBanner.equals(Uri.EMPTY)){ // Si un URI de bannière est fournie

                // On remet les pendules à zero en vidant la variable globale contenant l'URI bannière
                // On vide l'espace bannière et on remet l'icône par defaut
                fileURIBanner = Uri.EMPTY;
                action_banner.setImageResource(R.mipmap.ic_camera_alt_white_24dp);
                banner.setImageDrawable(null);

            }else { // Sinon

                // Indicateur à un et ouverture du dialogue de sélection d'images
                indicator = 1;
                showBannerDialog();

            }
        });

        findViewById(R.id.save).setOnClickListener(view -> {

            save(); // Enregistrement des données

        });

        primaryColor = "#009ee3"; // Couleur primaire par défaut
        secondaryColor = "#fbb03b"; // Couleur secondaire par défaut

        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        field = getIntent().getIntExtra("field", 0);
        address = getIntent().getStringExtra("address");
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        postalCode = getIntent().getStringExtra("postalCode");
        website = getIntent().getStringExtra("website");
        email = getIntent().getStringExtra("email");
        tel = getIntent().getStringExtra("tel");

        userId = getLocalUserData(this).getId();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        }
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
                            ActivityCompat.requestPermissions(FourthStepActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_READ_PERMISSION);

                            break;
                        case 1:
                            Toast.makeText(FourthStepActivity.this, getString(R.string.this_feature_will_be_available_soon), Toast.LENGTH_SHORT).show();

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
                            ActivityCompat.requestPermissions(FourthStepActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_READ_PERMISSION);

                            break;
                        case 1:
                            ActivityCompat.requestPermissions(FourthStepActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_WRITE_PERMISSION);

                            break;
                        case 2:
                            Toast.makeText(FourthStepActivity.this, getString(R.string.this_feature_will_be_available_soon), Toast.LENGTH_SHORT).show();

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
                    choosePhotoFromGallery(FourthStepActivity.this);
                    break;


                case 2:
                    takePhotoFromCamera(FourthStepActivity.this);
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
                                .load(Uri2Bitmap(FourthStepActivity.this, fileURILogo))
                                .into(logo);
                        action_logo.setImageResource(R.mipmap.ic_clear_white_24dp);

                    }else {

                        // On fournie la variable globale de l'URI bannière
                        fileURIBanner = resultUri;
                        // On affiche le résultat
                        Glide
                                .with(this)
                                .load(Uri2Bitmap(FourthStepActivity.this, fileURIBanner))
                                .into(banner);
                        action_banner.setImageResource(R.mipmap.ic_clear_white_24dp);

                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) { // Sinon si une erreur s'est produite,

                    Toast.makeText(FourthStepActivity.this, getString(R.string.loading_failure), Toast.LENGTH_SHORT).show();

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
                        .show(FourthStepActivity.this);
                break;

            case R.id.second_cardView:

                // Boîte de dialogue de selection de la couleur secondaire
                ColorPickerDialog
                        .newBuilder()
                        .setColor(R.color.skyblue)
                        .setDialogId(DIALOG_2) // Identifiant dialogue couleur secondaire
                        .setDialogTitle(R.string.secondary_color)
                        .show(FourthStepActivity.this);
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

    /**
     * Lancement de l'enregistrement
     * On vérifie la fourniture de la variable globale bannière
     * si elle est fournie, on procède à l'upload, on recupère l'url de téléchargement avec laquelle on lance l'upload du logo
     * sinon on lance l'upload du logo avec la constante DEFAUT
     * On procède à la même opération pour l'upload du logo et si l'URI de cette dernière n'est pas fournie, on lui
     * attribue également la constante DEFAUT puis on procède à l'enregistrement finale des données fournies.
     */
    private void save(){

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
     * Enregistrement finale des données fournies
     * @param logoUrl / Url de téléchargement du logo
     * @param bannerUrl / Url de téléchargement de la bannière
     */
    private void saveData(String logoUrl, String bannerUrl){

        // Initialisation de la liste des administrateurs de la vitrine
        List<String> owners = new ArrayList<>();
        owners.add(userId);

        // Récupération de la clé
        String key = databaseReference.push().getKey();
        key = key != null ? key : generateKey(28);

        // Instanciation de l'objet vitrine
        Showcase showcase = new Showcase(
                    key,
                    name,
                    description,
                    field,
                    bannerUrl,
                    logoUrl,
                    new ShowcaseContactDetails(email, tel, website,
                            new Location(address, latitude, longitude, postalCode)),
                    owners,
                    primaryColor,
                    secondaryColor
                );

        // Inscription de l'objet dans la base de données
        databaseReference
                .child(key)
                .setValue(showcase)
                .addOnSuccessListener(aVoid -> {

                    DatabaseReference userNbShowcasesDbReference = databaseReference.child("users").child(userId).child("nbShowcases");

                    userNbShowcasesDbReference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getValue() == null) return;

                                    Integer nb = dataSnapshot.getValue(Integer.class);

                                    if (nb != null){

                                        userNbShowcasesDbReference.setValue(nb +1);

                                    }

                                    if (loadingDialog.isShowing()){  loadingDialog.dismiss(); }

                                    // Redirection vers le fragment home.
                                    startActivity(
                                            new Intent(FourthStepActivity.this, HomeActivity.class)
                                                    .putExtra("redirectToActivity", ManageActivity.class.getName())
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    if (loadingDialog.isShowing()){  loadingDialog.dismiss(); }
                });

    }
}
