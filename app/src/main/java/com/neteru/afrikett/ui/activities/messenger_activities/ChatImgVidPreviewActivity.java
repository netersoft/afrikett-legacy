package com.neteru.afrikett.ui.activities.messenger_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.neteru.afrikett.BuildConfig;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.ChatImgVidPreviewAdapter;
import com.neteru.afrikett.core.models.RemoteDB.ChatImgVidPreview;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageEditorActivity;
import com.ortiz.touchview.TouchImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getSlideDownAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getSlideUpAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.isImage;
import static com.neteru.afrikett.core.utilities.AppUtilities.isKeyboardShown;
import static com.neteru.afrikett.core.utilities.AppUtilities.isVideo;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.IMAGE_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;
import static com.neteru.afrikett.core.utilities.Constants.VIDEO_MESSAGE;

public class ChatImgVidPreviewActivity extends AfrikettBaseActivity {
    private List<ChatImgVidPreview> chatImgVidPreviewList;
    private ImageView toolbarModifyBut;
    private ImageView toolbarEditBut;
    private EmojiEditText emojiEditText;
    private TouchImageView imageView;
    private VideoView videoView;
    private FrameLayout videoBox;
    private String targetName;
    private String targetProfilePic;
    private RecyclerView recyclerView;
    private MediaController mediaController;
    private ChatImgVidPreview currentPreview;
    private View root, imgVidBox;
    private ChatImgVidPreviewAdapter adapter;
    private Toolbar toolbar;
    private LinearLayout bottomEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_img_vid_preview);

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

        // Layout de base
        root = findViewById(R.id.rootLayout);

        // Espace video
        videoBox = findViewById(R.id.videoBox);

        // Lecteur video
        videoView = findViewById(R.id.videoView);

        // Espace image
        imageView = findViewById(R.id.imageView);

        // Espace commun image/video
        imgVidBox = findViewById(R.id.imgVidBox);

        // Recycler
        recyclerView = findViewById(R.id.recycler);

        // Bouton de modification d'image
        toolbarModifyBut = findViewById(R.id.toolbar_modify_but);

        // Bouton d'édition d'image
        toolbarEditBut = findViewById(R.id.toolbar_edit_but);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        String paths = getIntent().getStringExtra("paths"),
               names = getIntent().getStringExtra("names");

        targetName = getIntent().getStringExtra("targetName");
        targetProfilePic = getIntent().getStringExtra("targetProfilePic");

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

            chatImgVidPreviewList = new ArrayList<>();
            String[] pathsTab = paths.split("\n"),
                     namesTab = names.split("\n");

            // Chargement de la liste des aperçus contenant leurs noms, leurs types, leurs urls et leurs URIs
            for (int i = 0; i < pathsTab.length; i++){

                Uri tempUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(pathsTab[i]));

                if (isImage(pathsTab[i])) { // S'il s'agit d'une image

                    chatImgVidPreviewList.add(new ChatImgVidPreview(namesTab[i], pathsTab[i], tempUri, IMAGE_MESSAGE, null, chatImgVidPreviewList.size()));

                }else if(isVideo(pathsTab[i])){ // S'il s'agit d'une vidéo

                    chatImgVidPreviewList.add(new ChatImgVidPreview(namesTab[i], pathsTab[i], tempUri, VIDEO_MESSAGE, null, chatImgVidPreviewList.size()));

                }

            }

            // Chargement par défaut du premier aperçu
            currentPreview = chatImgVidPreviewList.get(0);
            setCentralView(currentPreview);

            // Chargement des miniatures des aperçus
            if (chatImgVidPreviewList != null) {

                adapter = new ChatImgVidPreviewAdapter(this, chatImgVidPreviewList, R.layout.template_chat_img_vid_preview, preview -> {

                    // Désélection de toutes les miniatures
                    uncheckAllItems();

                    // Chargement de l'aperçu courant
                    currentPreview = preview;
                    setCentralView(preview);

                    // Chargement de la description de l'aperçu courant
                    emojiEditText.setText(preview.getDescription() != null ? preview.getDescription() : EMPTY);
                    emojiEditText.setSelection(emojiEditText.getText() != null ? emojiEditText.getText().length() : 0);

                });
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        }
    }

    /**
     * Chargement de l'aperçu courant
     * @param preview / Aperçu
     */
    private void setCentralView(final ChatImgVidPreview preview){

        if (preview.getType() == IMAGE_MESSAGE){ // S'il s'agit d'une image

            if (imageView.getVisibility() != View.VISIBLE) {
                imageView.setVisibility(View.VISIBLE);
                videoBox.setVisibility(View.GONE);
                toolbarModifyBut.setVisibility(View.VISIBLE);
                toolbarEditBut.setVisibility(View.VISIBLE);
            }

            imageView.setImageURI(currentPreview.getUri());
            imageView.startAnimation(getFadeInAnimation(ChatImgVidPreviewActivity.this));

        }else { // Sinon

            if (videoBox.getVisibility() != View.VISIBLE) {
                imageView.setVisibility(View.GONE);
                videoBox.setVisibility(View.VISIBLE);
                toolbarModifyBut.setVisibility(View.GONE);
                toolbarEditBut.setVisibility(View.GONE);
            }

            videoView.setVideoURI(currentPreview.getUri());
            videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setOnVideoSizeChangedListener((mediaPlayer1, i, i1) -> {

                mediaController = new MediaController(ChatImgVidPreviewActivity.this);
                videoView.setMediaController(mediaController);

                mediaController.setAnchorView(videoView);

            }));
            videoView.startAnimation(getFadeInAnimation(ChatImgVidPreviewActivity.this));
            videoView.seekTo(100);
            videoView.requestFocus();
            videoView.start();
        }

    }

    /**
     * Désélectionneur de toutes les miniatures
     */
    private void uncheckAllItems(){
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; i++){

            final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            ChatImgVidPreviewAdapter.MyViewHolder myViewHolder = (ChatImgVidPreviewAdapter.MyViewHolder)holder;
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
                    recyclerView.startAnimation(getFadeOutAnimation(ChatImgVidPreviewActivity.this));
                }

                // On fait disparaitre en douce la barre d'outils
                if (toolbar.getVisibility() != View.GONE){
                    toolbar.setVisibility(View.GONE);
                    toolbar.startAnimation(getFadeOutAnimation(ChatImgVidPreviewActivity.this));
                }

                // On fait disparaitre en douce le champ légende
                if (bottomEditor.getVisibility() != View.GONE){
                    bottomEditor.setVisibility(View.GONE);
                    bottomEditor.startAnimation(getFadeOutAnimation(ChatImgVidPreviewActivity.this));
                }

            }else if(event.getAction() == MotionEvent.ACTION_UP){

                // Sinon on les fait réapparaitre
                if (recyclerView.getVisibility() != View.VISIBLE){
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.startAnimation(getFadeInAnimation(ChatImgVidPreviewActivity.this));
                }

                if (toolbar.getVisibility() != View.VISIBLE){
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.startAnimation(getFadeInAnimation(ChatImgVidPreviewActivity.this));
                }

                if (bottomEditor.getVisibility() != View.VISIBLE){
                    bottomEditor.setVisibility(View.VISIBLE);
                    bottomEditor.startAnimation(getFadeInAnimation(ChatImgVidPreviewActivity.this));
                }
            }

            return false;
        });

        emojiEditText = findViewById(R.id.img_preview_message_field);

        emojiEditText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Mise à jour de la description de l'aperçu courant
                currentPreview.setDescription(charSequence.toString());

                // Repositionnement de l'aperçu courant
                chatImgVidPreviewList.set(currentPreview.getPosition(), currentPreview);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        toolbarModifyBut.setOnClickListener(view -> {

            // Ouverture de l'activité de modification d'images
            CropImage.activity(FileProvider.getUriForFile(ChatImgVidPreviewActivity.this,
                                                         BuildConfig.APPLICATION_ID + ".provider", new File(currentPreview.getPath())))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setFixAspectRatio(true)
                    .start(ChatImgVidPreviewActivity.this);
        });

        toolbarEditBut.setOnClickListener(v -> startActivityForResult(
                new Intent(ChatImgVidPreviewActivity.this, ImageEditorActivity.class)
                        .putExtra("uri", currentPreview.getUriStr()), RANDOM_VALUE));

        final ImageButton emoji_but = findViewById(R.id.img_preview_emoji_button),
                send_but = findViewById(R.id.img_preview_send_button);

        // Switch emoji/keyboard button
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(root)
                .setOnEmojiPopupShownListener(() -> {

                    emoji_but.setColorFilter(ContextCompat.getColor(ChatImgVidPreviewActivity.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    emoji_but.setImageResource(R.mipmap.ic_keyboard_white_24dp);

                })
                .setOnEmojiPopupDismissListener(() -> {

                    emoji_but.setColorFilter(ContextCompat.getColor(ChatImgVidPreviewActivity.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    emoji_but.setImageResource(R.mipmap.ic_mood_white_24dp);

                })
                .build(emojiEditText);

        // Switch emoji/keyboard popup
        emoji_but.setOnClickListener(view -> emojiPopup.toggle());

        // Détecteur de clavier
        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (isKeyboardShown(root)){ // Si le clavier est ouvert

                // On fait disparaître la vue centrale
                if (imgVidBox.getVisibility() != View.GONE) {
                    imgVidBox.setVisibility(View.GONE);
                    imgVidBox.startAnimation(getSlideDownAnimation(ChatImgVidPreviewActivity.this));
                }
            }else { // Sinon

                // On la fait apparaitre
                if (imgVidBox.getVisibility() != View.VISIBLE) {
                    imgVidBox.setVisibility(View.VISIBLE);
                    imgVidBox.startAnimation(getSlideUpAnimation(ChatImgVidPreviewActivity.this));
                }
            }
        });

        // Envoi de la liste des aperçus
        send_but.setOnClickListener(view -> {
            if (chatImgVidPreviewList != null){

                Intent mIntent = new Intent();
                // Envoi des données sérialisées
                mIntent.putExtra("previewData", (Serializable) chatImgVidPreviewList);
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

        if (targetProfilePic.equals(DEFAULT)) {

            toolbar_profile.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(targetName), COLORS[getDigitFromString(targetName)]));

        }else {

            Glide
                    .with(ChatImgVidPreviewActivity.this)
                    .load(targetProfilePic)
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
                        chatImgVidPreviewList.set(currentPreview.getPosition(), currentPreview);

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
                    chatImgVidPreviewList.set(currentPreview.getPosition(), currentPreview);

                    adapter.notifyDataSetChanged();
                }

                break;
        }
    }
}
