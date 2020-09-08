package com.neteru.afrikett.ui.activities.messenger_activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.neteru.afrikett.BuildConfig;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.ChatsAdapter;
import com.neteru.afrikett.core.libs.BasicAudioPlayer.BasicAudioPlayer;
import com.neteru.afrikett.core.models.RemoteDB.ChatImgVidPreview;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.AudioMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.ContactMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.DateMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.DocumentMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.ImageMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.TextMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChatModels.VideoMessage;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.RemoteContactModel;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.utilities.Timing;
import com.neteru.afrikett.ui.activities.main_activities.HomeActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ContactViewActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.VideoViewActivity;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.neteru.afrikett.core.models.RemoteDB.MessengerChat.getCurrentDateId;
import static com.neteru.afrikett.core.utilities.AppUtilities.copyFileOrDirectory;
import static com.neteru.afrikett.core.utilities.AppUtilities.createNomediaFile;
import static com.neteru.afrikett.core.utilities.AppUtilities.createTempFile;
import static com.neteru.afrikett.core.utilities.AppUtilities.generateKey;
import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFileSizeFromPath;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.getMediaFileLength;
import static com.neteru.afrikett.core.utilities.AppUtilities.getNameFromPath;
import static com.neteru.afrikett.core.utilities.AppUtilities.getPathFromUri;
import static com.neteru.afrikett.core.utilities.AppUtilities.getThumbnailFromVideoPath;
import static com.neteru.afrikett.core.utilities.AppUtilities.getUriFromPath;
import static com.neteru.afrikett.core.utilities.AppUtilities.isImage;
import static com.neteru.afrikett.core.utilities.AppUtilities.isVideo;
import static com.neteru.afrikett.core.utilities.AppUtilities.saveImage;
import static com.neteru.afrikett.core.utilities.AppUtilities.setStringPreference;
import static com.neteru.afrikett.core.utilities.Constants.AUDIO_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.CONTACT_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DATE_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.DOCUMENT_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.DOCUMENT_MIME_TYPES;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.IMAGE_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.LONG_DELAY;
import static com.neteru.afrikett.core.utilities.Constants.MESSAGE_READ;
import static com.neteru.afrikett.core.utilities.Constants.MESSAGE_SEND;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_DOWNLOAD_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_UPLOAD_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;
import static com.neteru.afrikett.core.utilities.Constants.SENT_AUDIO_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.SENT_DOCUMENT_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.SENT_IMAGE_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.SENT_VIDEO_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.SENT_VIDEO_THUMBNAIL_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.TEXT_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.USER;
import static com.neteru.afrikett.core.utilities.Constants.VIDEO_MESSAGE;

public class ChatBoxActivity extends AppCompatActivity {
    private ImageButton emoji_but;
    private ImageButton attach_but;
    private ImageButton send_but;
    private TextView profile_name;
    private TextView profile_status;
    private EmojiEditText emojiEditText;
    private ImageView profile_pic;
    private ConstraintLayout rootView;
    private EmojiPopup emojiPopup;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private BottomSheet.Builder bottomSheetBuilder;
    private String targetId;
    private String targetName;
    private String targetColor;
    private String targetProfilePic;
    private String targetPhoneNumber;
    private List<MessengerChat> chatsList = new ArrayList<>();
    private Map<String, UploadTask> uploadTaskMap = new HashMap<>();
    private Map<String, StorageTask> storageTaskMap = new HashMap<>();
    private LayoutAnimationController animation;
    private LinearLayout box_bottom_block_message;
    private RecyclerView recyclerView;
    private ChatsAdapter adapter;
    private List<BasicAudioPlayer> audioPlayerList = new ArrayList<>();
    private Integer targetType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        // Barre d'outils
        Toolbar toolbar = findViewById(R.id.box_toolbar);

        // Bouton emoji
        emoji_but = findViewById(R.id.box_emoji_button);

        // Bouton attache
        attach_but = findViewById(R.id.box_attach_button);

        // Bouton d'envoi
        send_but = findViewById(R.id.box_send_button);

        // Nom de profil
        profile_name = findViewById(R.id.box_profile_name);

        // Status du profil
        profile_status = findViewById(R.id.box_profile_status);

        // EditText avec support des emojis
        emojiEditText = findViewById(R.id.box_message_field);

        // Boîte à message de blocage
        box_bottom_block_message = findViewById(R.id.box_bottom_block_message);

        // Photo de profil
        profile_pic = findViewById(R.id.box_profile_picture);

        // Recycler
        recyclerView = findViewById(R.id.box_message_recycler);

        // Vue racine
        rootView = findViewById(R.id.box_root);

        // ID de l'interlocuteur
        targetId = getIntent().getStringExtra("targetId");
        targetType = getIntent().getIntExtra("targetType", USER);

        // Reference vers la base de données
        databaseReference = FirebaseDatabase.getInstance()
                                            .getReference(DATABASE_ROOT);

        // Reference vers la base de stockage
        storageReference = FirebaseStorage.getInstance()
                                          .getReference("chats");

        // Customisation de la couleur de l'icône menu de la boîte d'outils
        if (toolbar.getOverflowIcon() != null) {
            toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
        }

        setSupportActionBar(toolbar);

        // Instanciation de l'animation des messages
        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        if (getSupportActionBar() != null) {

            // Customisation de la couleur du bouton retour de la boîte d'outils
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);

            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        // Chargement des écouteurs d'évènements
        setListeners();

        // Chargement des données de l'interlocuteur
        loadTargetData();

        // Chargement des messages de la discussion
        loadMessages();

        // Gestion des procédures liées au blocage de conversation
        checkBlockState();

        if (targetType == USER) {
            // Mise à jour périodique du status de connexion
            loadStatus();
        }
    }

    /**
     * Chargement des messages de la discussion
     */
    private void loadMessages(){
        databaseReference
                .child("messengers")
                .child(getNode())
                .child("chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){

                            // Purge de la liste
                            chatsList.clear();

                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                MessengerChat messengerChat = snapshot.getValue(MessengerChat.class);

                                chatsList.add(messengerChat);
                            }

                            recyclerView.setHasFixedSize(true);

                            // Chargement de l'animation à l'apparition des messages
                            recyclerView.setLayoutAnimation(animation);
                            adapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();

                            // Scroll au dernier message
                            scrollMyViewToBottom();
                        }else {

                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ChatBoxActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Scroller en bas de liste
     */
    private void scrollMyViewToBottom() {
        recyclerView.smoothScrollToPosition(chatsList.size() - 1);
    }

    /**
     * Ecouteurs d'évènements
     */
    @SuppressWarnings("ConstantConditions")
    private void setListeners(){

        adapter = new ChatsAdapter(this, chatsList, new ChatsAdapter.ChatsAdapterListener() {

            /**
             * Mise à jour du status du message : Envoyé
             * @param messengerChat / message
             */
            @Override
            public void setMessageStateToSend(MessengerChat messengerChat) {

                messengerChat.setMessageState(MESSAGE_SEND);

                databaseReference
                        .child("messengers")
                        .child(getNode())
                        .child("chats")
                        .child(messengerChat.getMessageId())
                        .setValue(messengerChat);
            }

            /**
             * Mise à jour du status du message et du noeud de discussion : Lu
             * @param messengerChat / message
             */
            @Override
            public void setMessageStateToRead(MessengerChat messengerChat) {

                messengerChat.setMessageState(MESSAGE_READ);

                databaseReference
                        .child("messengers")
                        .child(getNode())
                        .child("chats")
                        .child(messengerChat.getMessageId())
                        .setValue(messengerChat);

                databaseReference
                        .child("messengers")
                        .child(getNode())
                        .child("pendingMsg")
                        .setValue(false);
            }

            /**
             * Annulation d'une tâche de télévèrsement
             * @param messengerChat / message
             */
            @Override
            public void cancelUploadTask(MessengerChat messengerChat) {
                // Si la liste des tâches contient la tâche courante
                if (uploadTaskMap != null && uploadTaskMap.containsKey(messengerChat.getMessageId())){

                    // Si la tâche est en cours
                    if (uploadTaskMap.get(messengerChat.getMessageId()).isInProgress()){

                        // Annulation de la tâche
                        uploadTaskMap.get(messengerChat.getMessageId()).cancel();

                    }
                }
            }

            /**
             * Reprise d'un télévèrsement
             * @param messengerChat / message
             */
            @Override
            public void startUploadTask(MessengerChat messengerChat) {

                        uploadMessageData(messengerChat);

            }

            /**
             * Annulation d'un téléchargement
             * @param messengerChat / message
             */
            @Override
            public void cancelDownloadTask(MessengerChat messengerChat) {
                // Si la liste des téléchargements contient le téléchargement courant
                if (storageTaskMap != null && storageTaskMap.containsKey(messengerChat.getMessageId())){

                    // Si le téléchargement est en cours
                    if (storageTaskMap.get(messengerChat.getMessageId()).isInProgress()){

                        // Annulation du téléchargement
                        storageTaskMap.get(messengerChat.getMessageId()).cancel();

                    }
                }
            }

            /**
             * Début d'un téléchargement
             * @param messengerChat / message
             * @param path / destination du fichier téléchargé
             */
            @Override
            public void startDownloadMessageFile(MessengerChat messengerChat, String path) {
                downloadMessageFile(messengerChat, path);
            }

            /**
             * Stockage des lecteurs audio en cours
             * @param audioPlayer / lecteur audio
             */
            @Override
            public void storeAudioPlayers(BasicAudioPlayer audioPlayer) {
                audioPlayerList.add(audioPlayer);
            }

            /**
             * Suppression de message
             * @param id / Identifiant du message à supprimer
             */
            @Override
            public void deleteMessage(final String id, final boolean l) {

                final DatabaseReference nodeDataReference = databaseReference.child("messengers").child(getNode());

                nodeDataReference
                        .child("chats")
                        .child(id)
                        .setValue(null);

                if (l){
                    nodeDataReference
                            .child("lastMsg")
                            .setValue(EMPTY);

                    nodeDataReference
                            .child("pendingMsg")
                            .setValue(false);
                }
            }

            /**
             * Suppression récursive de message
             * @param prevId / Identifiant du message précédent
             * @param id / Identifiant du message courant
             */
            @Override
            public void deleteMessageRecursively(final String prevId, final String id, final boolean l) {

                final DatabaseReference nodeDataReference = databaseReference.child("messengers").child(getNode());

                nodeDataReference
                        .child("chats")
                        .child(id)
                        .setValue(null);

                nodeDataReference
                        .child("chats")
                        .child(prevId)
                        .setValue(null);

                if (l){
                    nodeDataReference
                            .child("lastMsg")
                            .setValue(EMPTY);

                    nodeDataReference
                            .child("pendingMsg")
                            .setValue(false);
                }

            }

            /**
             * Affichage du contact
             * @param messengerChat / message
             */
            @Override
            public void openMessageContact(MessengerChat messengerChat) {

                Intent contactIntent = new Intent(ChatBoxActivity.this, ContactViewActivity.class);
                contactIntent.putExtra("name", messengerChat.getContactName());
                contactIntent.putExtra("number", messengerChat.getContactNumber());
                contactIntent.putExtra("email", messengerChat.getContactEmail());

                startActivity(contactIntent);
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
            }

            /**
             * Ouverture du document
             * @param messengerChat / message
             */
            @Override
            public void openMessageDocument(MessengerChat messengerChat) {

                Uri uri;
                if (messengerChat.getSenderId().equals(getLocalUserData(ChatBoxActivity.this).getId())){
                    uri = messengerChat.extractMessageUri();
                }else {
                    uri = getUriFromPath(ChatBoxActivity.this, messengerChat.getMessageFinalPath());
                }

                Intent documentIntent = new Intent();
                documentIntent.setAction(Intent.ACTION_VIEW);
                documentIntent.setDataAndType(uri, getContentResolver().getType(uri));
                documentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(documentIntent);
            }

            /**
             * Affichage de l'image
             * @param messengerChat / message
             */
            @Override
            public void openMessageImage(MessengerChat messengerChat) {
                Intent imageIntent = new Intent(ChatBoxActivity.this, ImageViewActivity.class);

                if (messengerChat.getSenderId().equals(getLocalUserData(ChatBoxActivity.this).getId())){

                    imageIntent.putExtra(ImageViewActivity.URI_STR, messengerChat.getMessageUriStr());

                }else {

                    imageIntent.putExtra(ImageViewActivity.PATH, messengerChat.getMessageFinalPath());

                }
                imageIntent.putExtra(ImageViewActivity.LEGEND, messengerChat.getMessageLegend());

                startActivity(imageIntent);
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            /**
             * Ouverture de la video
             * @param messengerChat / message
             */
            @Override
            public void openMessageVideo(MessengerChat messengerChat) {
                Intent videoIntent = new Intent(ChatBoxActivity.this, VideoViewActivity.class);

                if (messengerChat.getSenderId().equals(getLocalUserData(ChatBoxActivity.this).getId())){

                    videoIntent.putExtra(VideoViewActivity.URI_STR, messengerChat.getMessageUriStr());

                }else {

                    videoIntent.putExtra(VideoViewActivity.PATH, messengerChat.getMessageFinalPath());

                }
                videoIntent.putExtra(VideoViewActivity.LEGEND, messengerChat.getMessageLegend());

                startActivity(videoIntent);
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
            }

        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);

        // Chargement des animations
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // Configuration du cache du recyclerView
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Instanciation du popup d'attache
        bottomSheetBuilder = new BottomSheet.Builder(this, R.style.BottomSheetStyle)
                                .grid()
                                .object(null)
                                .setTitle(null)
                                .setSheet(R.menu.chat_box_bottom_menu_dialog)
                                .setListener(new BottomSheetListener() {
                                    @Override
                                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {

                                    }

                                    @Override
                                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {

                                        switch (menuItem.getItemId()){
                                            case R.id.attach_document:

                                                ActivityCompat.requestPermissions(ChatBoxActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, DOCUMENT_MESSAGE);
                                                break;

                                            case R.id.attach_gallery:

                                                // Sélectionneur d'images
                                                ImagePicker.create(ChatBoxActivity.this)
                                                        .returnMode(ReturnMode.CAMERA_ONLY)
                                                        .folderMode(true)
                                                        .toolbarFolderTitle(getString(R.string.gallery))
                                                        .toolbarImageTitle(getString(R.string.select_an_image))
                                                        .includeVideo(true)
                                                        .showCamera(true)
                                                        .theme(R.style.ImagePickerTheme)
                                                        .multi()
                                                        .start();

                                                break;

                                            case R.id.attach_camera:

                                                // Caméra
                                                ImagePicker.cameraOnly().start(ChatBoxActivity.this);

                                                break;

                                            case R.id.attach_audio:

                                                ActivityCompat.requestPermissions(ChatBoxActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AUDIO_MESSAGE);
                                                break;

                                            case R.id.attach_contact:

                                                ActivityCompat.requestPermissions(ChatBoxActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_MESSAGE);
                                                break;

                                            case R.id.attach_dismiss:
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {

                                    }
                                });

        // Constructeur du popup Emoji
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiPopupShownListener(() -> {

                    emoji_but.setColorFilter(ContextCompat.getColor(ChatBoxActivity.this, R.color.skyblue), PorterDuff.Mode.MULTIPLY);
                    emoji_but.setImageResource(R.mipmap.ic_keyboard_white_24dp);

                })
                .setOnEmojiPopupDismissListener(() -> {

                    emoji_but.setColorFilter(ContextCompat.getColor(ChatBoxActivity.this, R.color.skyblue), PorterDuff.Mode.MULTIPLY);
                    emoji_but.setImageResource(R.mipmap.ic_mood_white_24dp);

                })
                .build(emojiEditText);

        // EditText avec support emoji
        emojiEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){

                    // Disparition du bouton attache et adaptation du padding de EditText
                    if (attach_but.getVisibility() != View.GONE) {
                        emojiEditText.setPadding(0, 0, 15, 0);
                        attach_but.setVisibility(View.GONE);
                        attach_but.setAnimation(getFadeOutAnimation(ChatBoxActivity.this));
                    }

                }else {

                    // Apparition du bouton attache
                    if (attach_but.getVisibility() != View.VISIBLE) {
                        emojiEditText.setPadding(0, 0, 0, 0);
                        attach_but.setVisibility(View.VISIBLE);
                        attach_but.setAnimation(getFadeInAnimation(ChatBoxActivity.this));
                    }

                }

                // Mise à jour du watcher avec l'ID de la cible
                databaseReference
                        .child("users")
                        .child(getLocalUserData(ChatBoxActivity.this).getId())
                        .child("watcher")
                        .setValue(targetId);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                // Mise à jour du watcher avec la valeur par défaut
                databaseReference
                        .child("users")
                        .child(getLocalUserData(ChatBoxActivity.this).getId())
                        .child("watcher")
                        .setValue(DEFAULT);

            }
        });

        // Switcher emoji/keyboard
        emoji_but.setOnClickListener(view -> emojiPopup.toggle());

        // Lancement du bottomSheetBuilder
        attach_but.setOnClickListener(view -> bottomSheetBuilder.show());

        // Envoi du message
        send_but.setOnClickListener(view -> {
            if (emojiEditText.getText() != null && !emojiEditText.getText().toString().isEmpty()){

                sendTextMessage(emojiEditText.getText().toString());

            }
        });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> { });
    }

    /**
     * Gestion des procédures en situation de blocage
     */
    private void checkBlockState(){

        // Récupération des états de blocage
        databaseReference
                .child("messengers")
                .child(getNode())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){

                            // Récupération des données du noeud de conversation
                            MessengerNode node = dataSnapshot.getValue(MessengerNode.class);

                            if (node != null){
                                if (node.getFirstIdBlockState() || node.getSecondIdBlockState()){

                                    // Si l'un des des Id est en état de blocage
                                    // On affiche le message de blocage de conversation
                                    box_bottom_block_message.setVisibility(View.VISIBLE);

                                }else{

                                    // Sinon on la cache
                                    box_bottom_block_message.setVisibility(View.GONE);

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Chargement et mise à jour du status du contact
     */
    private void loadStatus(){

        databaseReference
                .child("users")
                .child(targetId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        if (u != null) {
                            if (u.getStatus() == 0) { // Si le contact est déconnecté,

                                // On affiche la période passée depuis la dernière connexion
                                String s = getString(R.string.seen) + Timing.getInstance
                                                    (ChatBoxActivity.this, u.getLastConnection()).getTimePeriod().toLowerCase();
                                profile_status.setText(s);
                                profile_status.setTextColor(Color.DKGRAY);

                            } else { // Si le contact est connecté

                                // Si son observateur pointe sur l'utilisateur courant
                                if (u.getWatcher().equals(getLocalUserData(ChatBoxActivity.this).getId())) {

                                    // L'utilisateur est en train d'écrire
                                    profile_status.setText(getString(R.string.is_writing));
                                    profile_status.setTextColor(Color.GREEN);

                                } else { // Sinon

                                    // L'utilisateur est en ligne mais n'écrit rien
                                    if (profile_status.getText().toString().equals(getString(R.string.is_writing))) {

                                        /* Lancement du dispositif de latence */
                                        new Handler().postDelayed(() -> {

                                            profile_status.setText(getString(R.string.connected));
                                            profile_status.setTextColor(Color.GREEN);

                                        }, LONG_DELAY);

                                    }else {

                                        // A l'ouverture du box
                                        profile_status.setText(getString(R.string.connected));
                                        profile_status.setTextColor(Color.GREEN);

                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Envoi message textuel
     * @param msg / contenu du message textuel
     */
    private void sendTextMessage(String msg){

        // Création du message textuel
        writeDateMsg(new TextMessage(getPushKey(), getLocalUserData(ChatBoxActivity.this).getId(), targetId, TEXT_MESSAGE, msg));

    }

    /**
     * Générateur de clé
     * @return clé
     */
    private String getPushKey(){

        String key = databaseReference
                        .child("messengers")
                        .child(getNode())
                        .child("chats")
                        .push()
                        .getKey();

        key = key != null ? key : generateKey(28);

        return key;

    }

    /**
     * Générateur de noeud de discussion
     * @return noeud de discussion
     */
    private String getNode(){
        final List<String> list = new ArrayList<>();
        list.add(getLocalUserData(this).getId());
        list.add(targetId);
        Collections.sort(list);

        return list.get(0) + "&" + list.get(1);
    }

    /**
     * Enregistrement de la date
     * @param message / message
     */
    private void writeDateMsg(final MessengerChat message){

        final String dateMsgKey = getPushKey(), node = getNode();

        databaseReference
                .child("messengers")
                .child(node)
                .child("chats")
                .orderByChild("dateId")
                .equalTo(getCurrentDateId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){ // Si la date existe

                            // On passe directement à l'enregistrement du message
                            writeMessage(message);

                        }else { // Sinon

                            // On inscris d'abord la date
                            databaseReference
                                    .child("messengers")
                                    .child(node)
                                    .child("chats")
                                    .child(dateMsgKey)
                                    .setValue(new DateMessage(dateMsgKey, getLocalUserData(ChatBoxActivity.this).getId(), targetId, DATE_MESSAGE, getCurrentDateId()))
                                    .addOnSuccessListener(aVoid -> {

                                        // Puis on écrit le message
                                        writeMessage(message);

                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { } });
    }

    /**
     * Enregistrement du message
     * @param message / message
     */
    private void writeMessage(final MessengerChat message){

        // Génération d'une nouvelle clé d'enregistrement
        final String msgKey = getPushKey();
        message.setMessageId(msgKey);

        // Chemin de stockage pour les messages complexes
        switch (message.getMessageType()){
            case DOCUMENT_MESSAGE:

                writeNodeData(getString(R.string.send_u_document), message.getMessageSendingDate());
                message.setTempStorageLocation("documents/" + getLocalUserData(this).getId() + "/" + UUID.randomUUID().toString());
                break;

            case AUDIO_MESSAGE:

                writeNodeData(getString(R.string.send_u_audio), message.getMessageSendingDate());
                message.setTempStorageLocation("audios/" + getLocalUserData(this).getId() + "/" + UUID.randomUUID().toString());
                break;

            case VIDEO_MESSAGE:

                writeNodeData(getString(R.string.send_u_video), message.getMessageSendingDate());
                message.setTempStorageLocation("videos/" + getLocalUserData(this).getId() + "/" + UUID.randomUUID().toString());
                break;

            case IMAGE_MESSAGE:

                writeNodeData(getString(R.string.send_u_picture), message.getMessageSendingDate());
                message.setTempStorageLocation("images/" + getLocalUserData(this).getId() + "/" + UUID.randomUUID().toString());
                break;

            case TEXT_MESSAGE:

                writeNodeData(message.getMessageText(), message.getMessageSendingDate());
                break;
        }

        // Enregistrement définitif du message
        databaseReference
                .child("messengers")
                .child(getNode())
                .child("chats")
                .child(msgKey)
                .setValue(message)
                .addOnSuccessListener(aVoid -> {

                    if (message.getTempStorageLocation() != null){
                        // Si le chemin de stockage existe alors il s'agit d'un message complexe

                        // On procède alors à l'upload des données
                        uploadMessageData(message);

                    }else { // Sinon

                        // On passe directement à l'écriture du message
                        message.setMessageState(MESSAGE_SEND);

                        databaseReference
                                .child("messengers")
                                .child(getNode())
                                .child("chats")
                                .child(msgKey)
                                .setValue(message);

                    }

                });

        if (message.getMessageType() == TEXT_MESSAGE){ // S'il s'agit d'un message textuel

            emojiEditText.setText(null); // On vide l'EditText après l'opération

        }
    }

    /**
     * Mise à jour des données du noeud de discussion
     * @param lastMsg / Dernier message
     */
    private void writeNodeData(String lastMsg, String lastTime){

        // Reference au noeud de discussion
        DatabaseReference nodeDataReference = databaseReference.child("messengers").child(getNode());

        // Ecriture du dernier message
        nodeDataReference
                .child("lastMsg")
                .setValue(lastMsg);

        // Ecriture de la date du dernier message
        nodeDataReference
                .child("lastTime")
                .setValue(lastTime);

        // Inscription de l'etat de message en suspens
        nodeDataReference
                .child("pendingMsg")
                .setValue(true);

        // Ecriture de la cible du dernier message
        nodeDataReference
                .child("pendingMsgTarget")
                .setValue(targetId);
    }

    /**
     * Téléversement des données de messages complexes
     * @param message / message
     */
    private void uploadMessageData(final MessengerChat message){

        // Lancement de la tâche de téléversement
        UploadTask uploadTask = storageReference
                                        .child(message.getTempStorageLocation())
                                        .putFile(message.extractMessageUri());

                   uploadTask.addOnSuccessListener(taskSnapshot -> {
                       // Si l'opération réussie on recupère l'url de téléchargement du fichier téléversé
                       storageReference
                               .child(message.getTempStorageLocation())
                               .getDownloadUrl()
                               .addOnSuccessListener(uri -> {

                                   // On procède à la suppression du message pré-enregistré
                                   databaseReference
                                           .child("messengers")
                                           .child(getNode())
                                           .child("chats")
                                           .child(message.getMessageId())
                                           .removeValue()
                                           .addOnSuccessListener(aVoid -> {

                                               if (message.getMessageType() != VIDEO_MESSAGE) {

                                                   // Mise à jour de la clé
                                                   message.setMessageId(getPushKey());

                                                   // Mise à jour du status du message : envoyé
                                                   message.setMessageState(MESSAGE_SEND);

                                                   // Inscription de l'url de téléchargement
                                                   message.setMessageDownloadUrl(uri.toString());

                                                   // Enregistrement définitif du message
                                                   databaseReference
                                                           .child("messengers")
                                                           .child(getNode())
                                                           .child("chats")
                                                           .child(message.getMessageId())
                                                           .setValue(message);

                                               }else { // S'il s'agit d'une vidéo

                                                   // Chemin de stockage de l'aperçu de la video
                                                   final String thumbnailUrl = "videos/" + getLocalUserData(ChatBoxActivity.this).getId() + "/thumbnails/" + UUID.randomUUID().toString();

                                                   // Téléversement de l'aperçu de la video
                                                   storageReference
                                                           .child(thumbnailUrl)
                                                           .putFile(message.extractVideoThumbnailUri())
                                                           .addOnSuccessListener(taskSnapshot1 -> {

                                                               // Récupération de l'url de téléchargement de l'aperçu de la vidéo
                                                               storageReference
                                                                       .child(thumbnailUrl)
                                                                       .getDownloadUrl()
                                                                       .addOnSuccessListener(thumbUri -> {

                                                                           // Mise à jour des données
                                                                           message.setMessageId(getPushKey());
                                                                           message.setMessageState(MESSAGE_SEND);
                                                                           message.setMessageDownloadUrl(uri.toString());
                                                                           message.setVideoThumbnailDownloadUrl(thumbUri.toString());

                                                                           // Enregistrement définitif du message
                                                                           databaseReference
                                                                                   .child("messengers")
                                                                                   .child(getNode())
                                                                                   .child("chats")
                                                                                   .child(message.getMessageId())
                                                                                   .setValue(message);

                                                                       });

                                                           });
                                               }

                                           });

                               });
                   })
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // Suivi de progression du téléversement principal
                    // La variable de progression est mise à jour périodiquement
                    // Et est utilisée pour synchroniser la barre de progression
                    setStringPreference(ChatBoxActivity.this, MESSENGER_UPLOAD_PREFS, message.getMessageId(), String.valueOf(progress));
                });

        if(!uploadTaskMap.containsKey(message.getMessageId())){ // Si la tâche n'y ai pas déjà

             uploadTaskMap.put(message.getMessageId(), uploadTask); // On l'ajoute à la liste de tâches

        }
    }

    /**
     * Téléchargement de fichier
     * @param messengerChat / message
     * @param path / Destination locale du fichier
     */
    private void downloadMessageFile(final MessengerChat messengerChat, String path){

        // Reference à partir de l'url de téléchargement
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(messengerChat.getMessageDownloadUrl());

        // Création d'un fichier temporaire
        final File tempFile = createTempFile(path, messengerChat.getMessageFileName());

        if (tempFile != null){ // Si le fichier est créé avec succès

            // Téléchargement du fichier
            StorageTask storageTask = reference
                    .getFile(tempFile)
                    .addOnSuccessListener(taskSnapshot -> {

                        // Mise à jour du status du message : Lu
                        messengerChat.setMessageState(MESSAGE_READ);

                        // Sauvegarde du chemin local final du fichier
                        messengerChat.setMessageFinalPath(tempFile.getPath());

                        // Mise à jour du message
                        databaseReference
                                .child("messengers")
                                .child(getNode())
                                .child("chats")
                                .child(messengerChat.getMessageId())
                                .setValue(messengerChat);

                    })
                    .addOnFailureListener(e -> Toast.makeText(ChatBoxActivity.this, getString(R.string.download_error), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(taskSnapshot -> {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        // Suivi de progression du téléchargement principal
                        // La variable de progression est mise à jour périodiquement
                        // Et est utilisée pour synchroniser la barre de progression
                        setStringPreference(ChatBoxActivity.this, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId(), String.valueOf(progress));
                    });

            if(!storageTaskMap.containsKey(messengerChat.getMessageId())){ // Si le téléchargement n'y ai pas déjà

                storageTaskMap.put(messengerChat.getMessageId(), storageTask); // On l'ajoute à la liste

            }

        }else {

            Toast.makeText(ChatBoxActivity.this, getString(R.string.download_error), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Chargement des données de l'interlocuteur
     */
    private void loadTargetData(){
        if (targetId != null){

            if (targetType == USER) {
                // Récupération de la photo de l'interlocuteur
                databaseReference
                        .child("users")
                        .child(targetId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    final User targetUser = dataSnapshot.getValue(User.class);

                                    if (targetUser != null) {
                                        targetProfilePic = targetUser.getProfileUrl();
                                    }

                                    // Récupération du nom de l'interlocuteur dans la base de contacts de l'utilisateur courant
                                    databaseReference
                                            .child("contacts")
                                            .child(getLocalUserData(ChatBoxActivity.this).getId())
                                            .child(targetId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.getValue() != null) {

                                                        RemoteContactModel rcm = dataSnapshot.getValue(RemoteContactModel.class);

                                                        if (rcm != null) {

                                                            targetColor = EMPTY;
                                                            targetName = rcm.getName();
                                                            targetPhoneNumber = rcm.getPhoneNumber();

                                                            profile_name.setText(targetName);

                                                            if (targetProfilePic.equals(DEFAULT)) {

                                                                profile_pic.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(targetName), COLORS[getDigitFromString(targetName)]));

                                                            } else {

                                                                Glide
                                                                        .with(ChatBoxActivity.this)
                                                                        .load(targetProfilePic)
                                                                        .apply(RequestOptions.circleCropTransform())
                                                                        .into(profile_pic);
                                                            }

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }else {

                // Récupération de la photo de l'interlocuteur
                databaseReference
                        .child("showcases")
                        .child(targetId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    final Showcase targetShowcase = dataSnapshot.getValue(Showcase.class);

                                    if (targetShowcase == null) return;

                                    targetName = targetShowcase.getName();
                                    targetProfilePic = targetShowcase.getLogo();
                                    targetColor = targetShowcase.getSecondaryColor();
                                    targetPhoneNumber = targetShowcase.getShowcaseContactDetails().getNumber();

                                    profile_name.setText(targetName);
                                    profile_status.setText(targetShowcase.getShowcaseContactDetails().getLocation().getAddress());
                                    profile_status.setTextColor(Color.GRAY);

                                    if (targetProfilePic.equals(DEFAULT)) {

                                        profile_pic.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(targetName), Color.parseColor(targetColor)));

                                    } else {

                                        Glide
                                                .with(ChatBoxActivity.this)
                                                .load(targetProfilePic)
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(profile_pic);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

        }
    }

    /**
     * Sélecteur de document
     */
    private void getDocumentFiles(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            intent.setType("*/*");

            intent.putExtra(Intent.EXTRA_MIME_TYPES, DOCUMENT_MIME_TYPES);

        } else {
            StringBuilder mimeTypesStr = new StringBuilder();
            for (String mimeType : DOCUMENT_MIME_TYPES) {
                mimeTypesStr.append(mimeType).append("|");
            }
            intent.setType(mimeTypesStr.toString().substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,getString(R.string.send_document)), DOCUMENT_MESSAGE);

    }

    /**
     * Sélecteur d'audio
     */
    private void getAudioFiles(){

        Intent intent;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.setType("audio/*");

        startActivityForResult(Intent.createChooser(intent,getString(R.string.send_audio_file)), AUDIO_MESSAGE);
    }

    /**
     * Sélecteur de contact
     */
    private void getContacts(){

        new MultiContactPicker.Builder(this)
                .theme(R.style.ContactPickerTheme)
                .setTitleText(getString(R.string.send_contacts))
                .limitToColumn(LimitColumn.NONE)
                .searchIconColor(getResources().getColor(R.color.skyblue))
                .setActivityAnimations(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity,
                        R.anim.slide_in_left_activity,
                        R.anim.slide_out_right_activity)
                .showPickerForResult(CONTACT_MESSAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Si autorisation donnée,

            if (permissions.length > 0 && permissions[0].equals(Manifest.permission.READ_CONTACTS)){ // de lire les contacts

                // Redirection vers le sélecteur de contact
                getContacts();

            }else { // Sinon,

                switch (requestCode){

                    case DOCUMENT_MESSAGE:

                        // Redirection vers le sélecteur de document
                        getDocumentFiles();

                        break;

                    case AUDIO_MESSAGE:

                        // Redirection vers le sélecteur d'audio
                        getAudioFiles();

                        break;

                }

            }

        } else {

            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { // Résultat initial image(s)/vidéo(s)

            List<Image> images = ImagePicker.getImages(data);

            if (images != null){

                // Execution de la copie locale des fichiers et ouverture du visualisateur d'aperçu
                new ImgVidFileCopyTask(images).execute();
            }


        }else if (requestCode == DOCUMENT_MESSAGE){ // Résultat document

            if (data != null && data.getData() != null) {

                Uri documentUri = data.getData();

                // Execution de la copie locale des documents
                new DocAudioFileCopyTask(documentUri, DOCUMENT_MESSAGE).execute();

            }

        }else if (requestCode == AUDIO_MESSAGE){ // Résultat audio

            if (data != null && data.getData() != null){

                Uri audioUri = data.getData();

                // Execution de la copie locale des audios
                new DocAudioFileCopyTask(audioUri, AUDIO_MESSAGE).execute();
            }

        }else if (requestCode == CONTACT_MESSAGE){ // Résultat contact(s)

            if(resultCode == RESULT_OK && data != null) {

                List<ContactResult> results = MultiContactPicker.obtainResult(data);

                for (ContactResult contact : results){

                    if (contact.equals(results.get(0))) { // Pour le premier contact de la liste

                        // Enregistrement indirect du contact
                        writeDateMsg(new ContactMessage(getPushKey(), getLocalUserData(this).getId(), targetId, CONTACT_MESSAGE, contact.getDisplayName(),
                                contact.getPhoneNumbers().isEmpty() ? null : contact.getPhoneNumbers().get(0).getNumber(), contact.getEmails().isEmpty() ? null : contact.getEmails().get(0)));

                    }else { // Pour les autres contacts

                        // Enregistrement direct du contact
                        writeMessage(new ContactMessage(getPushKey(), getLocalUserData(this).getId(), targetId, CONTACT_MESSAGE, contact.getDisplayName(),
                                contact.getPhoneNumbers().isEmpty() ? null : contact.getPhoneNumbers().get(0).getNumber(), contact.getEmails().isEmpty() ? null : contact.getEmails().get(0)));

                    }
                }

            }

        }else if (requestCode == RANDOM_VALUE){ // Résultat final image(s)/video(s)

            if (resultCode == RESULT_OK && data != null){

                // Récupération des données sérializées
                @SuppressWarnings("unchecked")
                List<ChatImgVidPreview> chatImgVidPreviewList = (List<ChatImgVidPreview>) data.getSerializableExtra("previewData");

                for (ChatImgVidPreview chatImgVidPreview : chatImgVidPreviewList){
                    if (chatImgVidPreview.getType() == IMAGE_MESSAGE){ // Images

                        if (chatImgVidPreview.equals(chatImgVidPreviewList.get(0))) { // Pour la première image de la liste

                            // Enregistrement indirect de l'image
                            writeDateMsg(
                                    new ImageMessage(getPushKey(), getLocalUserData(this).getId(),
                                            targetId, chatImgVidPreview.getType(), chatImgVidPreview.getUri(),
                                            chatImgVidPreview.getDescription(), chatImgVidPreview.getName(), getFileSizeFromPath(chatImgVidPreview.getPath())));

                        }else { // Pour les autres images

                            // Enregistrement direct de l'image
                            writeMessage(
                                    new ImageMessage(getPushKey(), getLocalUserData(this).getId(),
                                            targetId, chatImgVidPreview.getType(), chatImgVidPreview.getUri(),
                                            chatImgVidPreview.getDescription(), chatImgVidPreview.getName(), getFileSizeFromPath(chatImgVidPreview.getPath())));

                        }

                    }else{  // Video

                        // Tâche d'extraction de l'aperçu video
                        new VidThumbnailSettingTask(chatImgVidPreviewList, chatImgVidPreview).execute();

                    }
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chat_box_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.chat_box_info){

            // Ouverture de la page d'informations sur l'interlocuteur
            startActivity(new Intent(this, ChatBoxInfoActivity.class)
                                .putExtra("targetId", targetId)
                                .putExtra("targetType", targetType)
                                .putExtra("targetName", targetName)
                                .putExtra("targetColor", targetColor)
                                .putExtra("targetProfilePic", targetProfilePic)
                                .putExtra("targetPhoneNumber", targetPhoneNumber));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Capture de l'action retour
     */
    @Override
    public void onBackPressed() {

        if (getIntent().getBooleanExtra("captureBack", false)){ // Si capture activée

            // Redirection vers le fragment messenger
            startActivity(
                    new Intent(this, HomeActivity.class)
                            .putExtra("redirectToFragment", R.id.nav_messenger)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }else {
            super.onBackPressed();
        }

        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
    }

    /**
     * Capture du bouton retour
     * @return booleen
     */
    @Override
    public boolean onSupportNavigateUp() {

        if (getIntent().getBooleanExtra("captureBack", false)){ // Si capture activée

            // Redirection vers le fragment messenger
            startActivity(
                    new Intent(this, HomeActivity.class)
                            .putExtra("redirectToFragment", R.id.nav_messenger)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }else {
            finish();
        }

        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Arrêt de tous les lecteurs audios en fin d'activité
        if (audioPlayerList != null){

            for (BasicAudioPlayer audioPlayer : audioPlayerList){
                audioPlayer.stopPlaying();
            }

        }
    }

    /**
     * Tâche d'extraction de l'aperçu vidéo
     */
    @SuppressLint("StaticFieldLeak")
    class VidThumbnailSettingTask extends AsyncTask<String, Void, Void>{

        private List<ChatImgVidPreview> chatImgVidPreviewList;
        private ChatImgVidPreview chatImgVidPreview;
        private LoadingDialog loadingDialog;

        VidThumbnailSettingTask(List<ChatImgVidPreview> list, ChatImgVidPreview chat){

            chatImgVidPreviewList = list;
            chatImgVidPreview = chat;

            loadingDialog = new LoadingDialog(ChatBoxActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            // Enregistrement locale de l'aperçu
            Uri thumbUri = saveImage(ChatBoxActivity.this, getThumbnailFromVideoPath(chatImgVidPreview.getPath()), SENT_VIDEO_THUMBNAIL_DIRECTORY);

            // Création du fichier .nomedia
            createNomediaFile(SENT_VIDEO_THUMBNAIL_DIRECTORY);

            if (chatImgVidPreview.equals(chatImgVidPreviewList.get(0))) { // Pour le premier fichier video

                // Enregistrement indirect de la video
                writeDateMsg(
                        new VideoMessage(getPushKey(), getLocalUserData(ChatBoxActivity.this).getId(),
                                targetId, chatImgVidPreview.getType(), chatImgVidPreview.getUri(), thumbUri,
                                chatImgVidPreview.getDescription(), chatImgVidPreview.getName(), getMediaFileLength(ChatBoxActivity.this, chatImgVidPreview.getUri()), getFileSizeFromPath(chatImgVidPreview.getPath())));

            }else { // Pour les autres fichiers vidéos

                // Enregistrement direct de la vidéo
                writeMessage(
                        new VideoMessage(getPushKey(), getLocalUserData(ChatBoxActivity.this).getId(),
                                targetId, chatImgVidPreview.getType(), chatImgVidPreview.getUri(), thumbUri,
                                chatImgVidPreview.getDescription(), chatImgVidPreview.getName(), getMediaFileLength(ChatBoxActivity.this, chatImgVidPreview.getUri()), getFileSizeFromPath(chatImgVidPreview.getPath())));

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadingDialog.dismiss();
        }
    }

    /**
     * Tâche de copie de fichiers image(s)/vidéo(s)
     */
    @SuppressLint("StaticFieldLeak")
    class ImgVidFileCopyTask extends AsyncTask<String, Void, Void>{

        private List<Image> imageList;
        private LoadingDialog loadingDialog;
        private StringBuilder pathBuilder = new StringBuilder(),
                              nameBuilder = new StringBuilder();

        ImgVidFileCopyTask(List<Image> images){
            imageList = images;

            loadingDialog = new LoadingDialog(ChatBoxActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for (Image image: imageList){

                String finalDirectory = EMPTY;

                // Sélection du chemin de destination
                if (isImage(image.getPath())){

                    finalDirectory = SENT_IMAGE_DIRECTORY;

                }else if(isVideo(image.getPath())){

                    finalDirectory = SENT_VIDEO_DIRECTORY;

                }

                // Copie du fichier
                copyFileOrDirectory(image.getPath(), finalDirectory);

                // Création du fichier .nomedia
                createNomediaFile(finalDirectory);

                // Préparation des chaînes formatées
                pathBuilder.append(finalDirectory).append(File.separator).append(image.getName()).append("\n");
                nameBuilder.append(image.getName()).append("\n");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            loadingDialog.dismiss();

            // Redirection vers le visualisateur d'aperçus
            startActivityForResult(new Intent(ChatBoxActivity.this, ChatImgVidPreviewActivity.class)
                    .putExtra("names", nameBuilder.toString())
                    .putExtra("paths", pathBuilder.toString())
                    .putExtra("targetId", targetId)
                    .putExtra("targetName", targetName)
                    .putExtra("targetProfilePic", targetProfilePic), RANDOM_VALUE);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            super.onPostExecute(aVoid);
        }
    }

    /**
     * Tâche de copie de fichiers audios/documents
     */
    @SuppressLint("StaticFieldLeak")
    class DocAudioFileCopyTask extends AsyncTask<String, Void, Void>{

        private Uri uri;
        private int type;
        private LoadingDialog loadingDialog;

        DocAudioFileCopyTask(Uri u, int t){
            uri = u;
            type = t;

            loadingDialog = new LoadingDialog(ChatBoxActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            // Extraction du chemin d'accès au fichier
            String path = getPathFromUri(ChatBoxActivity.this, uri), name = null;
            if (path != null) {
                name = getNameFromPath(path); // Extraction du nom de fichier
            }

            if (type == DOCUMENT_MESSAGE){ // Document

                // Copie locale du document
                copyFileOrDirectory(path, SENT_DOCUMENT_DIRECTORY);

                // Création du fichier .nomedia
                createNomediaFile(SENT_DOCUMENT_DIRECTORY);

                // Extraction de l'URI de la copie locale
                Uri tempUri = FileProvider.getUriForFile(ChatBoxActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(SENT_DOCUMENT_DIRECTORY + File.separator + name));

                // Enregistrement indirect du document
                writeDateMsg(
                        new DocumentMessage(getPushKey(), getLocalUserData(ChatBoxActivity.this).getId(),
                                targetId, DOCUMENT_MESSAGE, tempUri, name, getFileSizeFromPath(getPathFromUri(ChatBoxActivity.this, uri))));

            }else {

                // Copie locale de l'audio
                copyFileOrDirectory(path, SENT_AUDIO_DIRECTORY);

                // Création du fichier .nomedia
                createNomediaFile(SENT_AUDIO_DIRECTORY);

                // Extraction de l'URI de la copie locale
                Uri tempUri = FileProvider.getUriForFile(ChatBoxActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(SENT_AUDIO_DIRECTORY + File.separator + name));

                // Enregistrement indirect de l'audio
                writeDateMsg(
                        new AudioMessage(getPushKey(), getLocalUserData(ChatBoxActivity.this).getId(),
                                targetId, AUDIO_MESSAGE, tempUri, name, getMediaFileLength(ChatBoxActivity.this, uri), getFileSizeFromPath(getPathFromUri(ChatBoxActivity.this, uri))));

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadingDialog.dismiss();
        }
    }
}
