package com.neteru.afrikett.ui.activities.showcase_activities.stories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.neteru.afrikett.BuildConfig;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.StoriesImgPreviewAdapter;
import com.neteru.afrikett.core.models.RemoteDB.StoriesImgPreview;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageEditorActivity;
import com.ortiz.touchview.TouchImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;

public class StoriesImgPreviewActivity extends AppCompatActivity {
    private List<StoriesImgPreview> storiesImgPreviewList;
    private ImageView toolbarModifyBut;
    private ImageView toolbarEditBut;
    private TouchImageView imageView;
    private RecyclerView recyclerView;
    private StoriesImgPreview currentPreview;
    private String showcaseName;
    private String showcaseLogo;
    private String showcaseSecondaryColor;
    private StoriesImgPreviewAdapter adapter;
    private Toolbar toolbar;
    private LinearLayout bottomEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_img_preview);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.bringToFront();
        appBarLayout.invalidate();

        // Barre d'outils customisée
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Espace image
        imageView = findViewById(R.id.imageView);

        // Recycler
        recyclerView = findViewById(R.id.recycler);

        // Bouton de redimensionnement d'image
        toolbarModifyBut = findViewById(R.id.toolbar_modify_but);

        // Bouton d'édition d'image
        toolbarEditBut = findViewById(R.id.toolbar_edit_but);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        String paths = getIntent().getStringExtra("paths"),
                names = getIntent().getStringExtra("names");

        showcaseName = getIntent().getStringExtra("showcaseName");
        showcaseLogo = getIntent().getStringExtra("showcaseLogo");
        showcaseSecondaryColor = getIntent().getStringExtra("showcaseSecondaryColor");

        // Récupération de la liste des aperçus
        getPreviewObjectList(names, paths);

        // Chargement de la photo de profil de l'interlocuteur
        loadProfilePhoto();

        // Chargement des écouteurs d'évènements
        setListeners();
    }

    /**
     * Parsage et récupération de la liste des aperçus
     * @param names / Chaîne formatée des noms de fichier d'aperçus
     * @param paths / Chaîne formatée des urls de fichier d'aperçus
     */
    private void getPreviewObjectList(String names, String paths) {

        if (paths != null && names != null){

            storiesImgPreviewList = new ArrayList<>();
            String[] pathsTab = paths.split("\n"),
                    namesTab = names.split("\n");

            // Chargement de la liste des aperçus contenant leurs noms, leurs types, leurs urls et leurs URIs
            for (int i = 0; i < pathsTab.length; i++){

                Uri tempUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(pathsTab[i]));

                storiesImgPreviewList.add(new StoriesImgPreview(namesTab[i], pathsTab[i], tempUri, storiesImgPreviewList.size()));


            }

            // Chargement par défaut du premier aperçu
            currentPreview = storiesImgPreviewList.get(0);
            imageView.setImageURI(currentPreview.getUri());
            imageView.startAnimation(getFadeInAnimation(StoriesImgPreviewActivity.this));

            // Chargement des miniatures des aperçus
            if (storiesImgPreviewList != null) {

                adapter = new StoriesImgPreviewAdapter(this, storiesImgPreviewList, R.layout.template_story_img_preview, preview -> {
                    // Désélection de toutes les miniatures
                    uncheckAllItems();

                    // Chargement de l'aperçu courant
                    currentPreview = preview;
                    imageView.setImageURI(currentPreview.getUri());
                    imageView.startAnimation(getFadeInAnimation(StoriesImgPreviewActivity.this));
                });
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        }
    }

    /**
     * Désélectionneur de toutes les miniatures
     */
    private void uncheckAllItems(){
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; i++){

            final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            StoriesImgPreviewAdapter.MyViewHolder myViewHolder = (StoriesImgPreviewAdapter.MyViewHolder)holder;
            myViewHolder.previewBox.setBackground(getResources().getDrawable(R.drawable.chat_img_vid_item_unchecked_border));

        }
    }

    /**
     * Chargement des écouteurs d'évènements
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){

        bottomEditor = findViewById(R.id.bottomEditor);

        // Disparition du sélecteur d'aperçus au touché
        imageView.setOnTouchListener((v, event) -> {

            // Si l'image est touchée
            if (event.getAction() == MotionEvent.ACTION_DOWN){

                // On fait disparaitre en douce le sélecteur
                if (recyclerView.getVisibility() != View.GONE){
                    recyclerView.setVisibility(View.GONE);
                    recyclerView.startAnimation(getFadeOutAnimation(StoriesImgPreviewActivity.this));
                }

                // On fait disparaitre en douce la barre d'outils
                if (toolbar.getVisibility() != View.GONE){
                    toolbar.setVisibility(View.GONE);
                    toolbar.startAnimation(getFadeOutAnimation(StoriesImgPreviewActivity.this));
                }

                // On fait disparaitre en douce le champ envoi
                if (bottomEditor.getVisibility() != View.GONE){
                    bottomEditor.setVisibility(View.GONE);
                    bottomEditor.startAnimation(getFadeOutAnimation(StoriesImgPreviewActivity.this));
                }

            }else if(event.getAction() == MotionEvent.ACTION_UP){

                // Sinon on les fait réapparaitre
                if (recyclerView.getVisibility() != View.VISIBLE){
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.startAnimation(getFadeInAnimation(StoriesImgPreviewActivity.this));
                }

                if (toolbar.getVisibility() != View.VISIBLE){
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.startAnimation(getFadeInAnimation(StoriesImgPreviewActivity.this));
                }

                if (bottomEditor.getVisibility() != View.VISIBLE){
                    bottomEditor.setVisibility(View.VISIBLE);
                    bottomEditor.startAnimation(getFadeInAnimation(StoriesImgPreviewActivity.this));
                }
            }

            return false;
        });

        toolbarModifyBut.setOnClickListener(view -> {

            // Ouverture de l'activité de modification d'images
            CropImage.activity(FileProvider.getUriForFile(StoriesImgPreviewActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", new File(currentPreview.getPath())))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setFixAspectRatio(true)
                    .start(StoriesImgPreviewActivity.this);


        });

        toolbarEditBut.setOnClickListener(v -> startActivityForResult(
                new Intent(StoriesImgPreviewActivity.this, ImageEditorActivity.class)
                            .putExtra("uri", currentPreview.getUriStr()), RANDOM_VALUE));

        final ImageButton send_but = findViewById(R.id.img_preview_send_button);

        // Envoi de la liste des aperçus
        send_but.setOnClickListener(view -> {
            if (storiesImgPreviewList != null){

                Intent mIntent = new Intent();
                // Envoi des données sérialisées
                mIntent.putExtra("previewData", (Serializable) storiesImgPreviewList);
                setResult(RESULT_OK, mIntent);
                finish();

            }
        });

    }

    /**
     * Chargement de la photo de profil de l'interlocuteur
     */
    private void loadProfilePhoto(){
        final ImageView toolbar_profile = findViewById(R.id.toolbar_profile_picture);

        if (showcaseLogo.equals(DEFAULT)) {

            toolbar_profile.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcaseName), Color.parseColor(showcaseSecondaryColor)));

        }else {

            Glide
                    .with(StoriesImgPreviewActivity.this)
                    .load(showcaseLogo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(toolbar_profile);
        }
    }

    /**
     * Résultat de l'éditeur
     * @param requestCode / code requête
     * @param resultCode / code résultat
     * @param data / données
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result != null) {
                    if (resultCode == RESULT_OK) { // Succès

                        // Mise à jour de l'URI de l'aperçu courant
                        currentPreview.setUri(result.getUri());
                        imageView.setImageURI(currentPreview.getUri());

                        // Mise à jour de la liste d'aperçu
                        storiesImgPreviewList.set(currentPreview.getPosition(), currentPreview);

                        adapter.notifyDataSetChanged();

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) { // Erreur

                        Toast.makeText(this, getString(R.string.loading_failure), Toast.LENGTH_SHORT).show();

                        result.getError().printStackTrace();

                    }
                }

                break;

            case RANDOM_VALUE:

                if (resultCode == RESULT_OK){

                    if (data == null){
                        return;
                    }

                    // Mise à jour de l'URI de l'aperçu courant
                    currentPreview.setUri(data.getData());
                    imageView.setImageURI(currentPreview.getUri());

                    // Mise à jour de la liste d'aperçu
                    storiesImgPreviewList.set(currentPreview.getPosition(), currentPreview);

                    adapter.notifyDataSetChanged();
                }

                break;

        }
    }
}
