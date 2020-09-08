package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.libs.BasicAudioPlayer.BasicAudioPlayer;
import com.neteru.afrikett.core.libs.EmojiAndSocialTextView.EmojiAndSocialTextView;
import com.neteru.afrikett.core.models.RemoteDB.MessengerChat;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.GlideApp;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.neteru.afrikett.core.utilities.AppUtilities.cutLongText;
import static com.neteru.afrikett.core.utilities.AppUtilities.getBooleanPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.getStringPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.getUriFromPath;
import static com.neteru.afrikett.core.utilities.AppUtilities.removePreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.setMargins;
import static com.neteru.afrikett.core.utilities.Constants.AUDIO_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.AUDIO_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.CONTACT_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.DATE_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.DOCUMENT_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.DOCUMENT_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.IMAGE_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.IMAGE_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.LINK_EMAIL;
import static com.neteru.afrikett.core.utilities.Constants.LINK_HASHTAG;
import static com.neteru.afrikett.core.utilities.Constants.LINK_PHONE;
import static com.neteru.afrikett.core.utilities.Constants.LINK_URL;
import static com.neteru.afrikett.core.utilities.Constants.MESSAGE_LOAD;
import static com.neteru.afrikett.core.utilities.Constants.MESSAGE_READ;
import static com.neteru.afrikett.core.utilities.Constants.MESSAGE_SEND;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_DOWNLOAD_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_UPLOAD_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.TEXT_MESSAGE;
import static com.neteru.afrikett.core.utilities.Constants.VIDEO_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.VIDEO_MESSAGE;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    // Contexte de l'activité source
    private Context context;

    // Vue courante
    private View itemView;

    // Support de la vue courante
    private MyViewHolder myViewHolder;

    // Liste de messages
    private List<MessengerChat> messengerChatList;

    // Interface connectrice à l'activité source
    private ChatsAdapterListener listener;

    // Exécutables de tâches asynchrones
    private Runnable videoProgressChecker, documentProgressChecker, audioProgressChecker;

    // Gestionnaire de tâches asynchrones
    private Handler videoProgressHandler, documentProgressHandler, audioProgressHandler;

    // Liste de lecteurs audios
    private List<BasicAudioPlayer> audioPlayerList;

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView template_date_date,
                         template_right_text_hour,
                         template_left_text_hour,
                         template_right_image_hour,
                         template_left_image_hour,
                         template_right_document_hour,
                         template_left_document_hour,
                         template_right_document_size,
                         template_left_document_size,
                         template_right_document_name,
                         template_left_document_name,
                         template_right_audio_hour,
                         template_left_audio_hour,
                         template_right_audio_size,
                         template_left_audio_size,
                         template_right_video_hour,
                         template_left_video_hour,
                         template_right_video_length,
                         template_left_video_length,
                         template_right_contact_hour,
                         template_left_contact_hour,
                         template_right_contact_name,
                         template_left_contact_name,
                         template_right_contact_number,
                         template_left_contact_number,
                         template_right_contact_email,
                         template_left_contact_email,
                         global_right_hour,
                         global_left_hour;

        private EmojiAndSocialTextView template_right_text_txt,
                                       template_left_text_txt,
                                       template_right_image_legend,
                                       template_left_image_legend,
                                       template_right_video_legend,
                                       template_left_video_legend;

        private ImageView template_right_text_state,
                          template_right_image_state,
                          template_right_document_state,
                          template_right_audio_state,
                          template_right_video_state,
                          template_right_contact_state,
                          template_right_contact_picture,
                          template_left_contact_picture,
                          template_right_image_img,
                          template_left_image_img,
                          template_right_video_thumbnail,
                          template_left_video_thumbnail,
                          template_right_video_play_button,
                          template_left_video_play_button,
                          global_right_state;

        private CircleProgressBar template_right_video_progress_bar,
                                  template_right_document_progress_bar,
                                  template_right_audio_progress_bar,
                                  template_left_video_progress_bar,
                                  template_left_document_progress_bar,
                                  template_left_audio_progress_bar;
 

        private View left_document_message_content,
                     left_contact_message_content,
                     left_audio_message_content,
                     left_video_message_content,
                     left_image_message_content,
                     left_text_message_content,
                     right_document_message_content,
                     right_contact_message_content,
                     right_audio_message_content,
                     right_video_message_content,
                     right_image_message_content,
                     right_text_message_content,
                     template_right_image_progress_box,
                     template_right_image_retry_box,
                     template_right_image_center_box,
                     template_right_video_center_box,
                     template_right_video_progress_box,
                     template_right_video_retry_box,
                     template_right_document_center_box,
                     template_right_document_progress_box,
                     template_right_document_retry_box,
                     template_right_audio_center_box,
                     template_right_audio_progress_box,
                     template_right_audio_retry_box,
                     template_left_image_progress_box,
                     template_left_image_retry_box,
                     template_left_image_center_box,
                     template_left_video_center_box,
                     template_left_video_progress_box,
                     template_left_video_retry_box,
                     template_left_document_center_box,
                     template_left_document_progress_box,
                     template_left_document_retry_box,
                     template_left_audio_center_box,
                     template_left_audio_progress_box,
                     template_left_audio_retry_box,
                     global_right_content,
                     global_left_content;

        private BasicAudioPlayer template_right_audio_player,
                                 template_left_audio_player;

        MyViewHolder(View view){
            super(view);

            // ************************************* MAIN CONTENT

                //LEFT BUBBLE
                left_document_message_content = view.findViewById(R.id.template_left_document_content);
                left_contact_message_content = view.findViewById(R.id.template_left_contact_content);
                left_audio_message_content = view.findViewById(R.id.template_left_audio_content);
                left_video_message_content = view.findViewById(R.id.template_left_video_content);
                left_image_message_content = view.findViewById(R.id.template_left_image_content);
                left_text_message_content = view.findViewById(R.id.template_left_text_content);

                //RIGHT BUBBLE
                right_document_message_content = view.findViewById(R.id.template_right_document_content);
                right_contact_message_content = view.findViewById(R.id.template_right_contact_content);
                right_audio_message_content = view.findViewById(R.id.template_right_audio_content);
                right_video_message_content = view.findViewById(R.id.template_right_video_content);
                right_image_message_content = view.findViewById(R.id.template_right_image_content);
                right_text_message_content = view.findViewById(R.id.template_right_text_content);

                //DATE TEMPLATE
                template_date_date = view.findViewById(R.id.template_date_date);

            // ************************************* RIGHT SIDE CONTENT

                //RIGHT TEXT TEMPLATE
                template_right_text_txt = view.findViewById(R.id.template_right_text_txt);
                template_right_text_hour = view.findViewById(R.id.template_right_text_hour);
                template_right_text_state = view.findViewById(R.id.template_right_text_state);

                //RIGHT IMAGE TEMPLATE
                template_right_image_img = view.findViewById(R.id.template_right_image_img);
                template_right_image_legend = view.findViewById(R.id.template_right_image_legend);
                template_right_image_progress_box = view.findViewById(R.id.template_right_image_progress_box);
                template_right_image_center_box = view.findViewById(R.id.template_right_image_center_box);
                template_right_image_retry_box = view.findViewById(R.id.template_right_image_retry_box);
                template_right_image_hour = view.findViewById(R.id.template_right_image_hour);
                template_right_image_state = view.findViewById(R.id.template_right_image_state);

                //RIGHT VIDEO TEMPLATE
                template_right_video_thumbnail = view.findViewById(R.id.template_right_video_thumbnail);
                template_right_video_legend = view.findViewById(R.id.template_right_video_legend);
                template_right_video_hour = view.findViewById(R.id.template_right_video_hour);
                template_right_video_state = view.findViewById(R.id.template_right_video_state);
                template_right_video_play_button = view.findViewById(R.id.template_right_video_play_button);
                template_right_video_progress_bar = view.findViewById(R.id.template_right_video_progress_bar);
                template_right_video_length = view.findViewById(R.id.template_right_video_length);
                template_right_video_progress_box = view.findViewById(R.id.template_right_video_progress_box);
                template_right_video_center_box = view.findViewById(R.id.template_right_video_center_box);
                template_right_video_retry_box = view.findViewById(R.id.template_right_video_retry_box);

                //RIGHT CONTACT TEMPLATE
                template_right_contact_hour = view.findViewById(R.id.template_right_contact_hour);
                template_right_contact_name = view.findViewById(R.id.template_right_contact_name);
                template_right_contact_number = view.findViewById(R.id.template_right_contact_number);
                template_right_contact_email = view.findViewById(R.id.template_right_contact_email);
                template_right_contact_state = view.findViewById(R.id.template_right_contact_state);
                template_right_contact_picture =  view.findViewById(R.id.template_right_contact_picture);

                //RIGHT DOCUMENT TEMPLATE
                template_right_document_hour = view.findViewById(R.id.template_right_document_hour);
                template_right_document_state = view.findViewById(R.id.template_right_document_state);
                template_right_document_size = view.findViewById(R.id.template_right_document_size);
                template_right_document_name = view.findViewById(R.id.template_right_document_name);
                template_right_document_progress_bar = view.findViewById(R.id.template_right_document_progress_bar);
                template_right_document_progress_box = view.findViewById(R.id.template_right_document_progress_box);
                template_right_document_center_box = view.findViewById(R.id.template_right_document_center_box);
                template_right_document_retry_box = view.findViewById(R.id.template_right_document_retry_box);

                //RIGHT AUDIO TEMPLATE
                template_right_audio_player = view.findViewById(R.id.template_right_audio_player);
                template_right_audio_hour = view.findViewById(R.id.template_right_audio_hour);
                template_right_audio_state = view.findViewById(R.id.template_right_audio_state);
                template_right_audio_size = view.findViewById(R.id.template_right_audio_size);
                template_right_audio_progress_bar = view.findViewById(R.id.template_right_audio_progress_bar);
                template_right_audio_progress_box = view.findViewById(R.id.template_right_audio_progress_box);
                template_right_audio_center_box = view.findViewById(R.id.template_right_audio_center_box);
                template_right_audio_retry_box = view.findViewById(R.id.template_right_audio_retry_box);

            // ************************************* LEFT SIDE CONTENT

                //LEFT TEXT TEMPLATE
                template_left_text_txt = view.findViewById(R.id.template_left_text_txt);
                template_left_text_hour = view.findViewById(R.id.template_left_text_hour);

                //LEFT IMAGE TEMPLATE
                template_left_image_img = view.findViewById(R.id.template_left_image_img);
                template_left_image_legend = view.findViewById(R.id.template_left_image_legend);
                template_left_image_progress_box = view.findViewById(R.id.template_left_image_progress_box);
                template_left_image_center_box = view.findViewById(R.id.template_left_image_center_box);
                template_left_image_retry_box = view.findViewById(R.id.template_left_image_retry_box);
                template_left_image_hour = view.findViewById(R.id.template_left_image_hour);

                //LEFT VIDEO TEMPLATE
                template_left_video_thumbnail = view.findViewById(R.id.template_left_video_thumbnail);
                template_left_video_legend = view.findViewById(R.id.template_left_video_legend);
                template_left_video_hour = view.findViewById(R.id.template_left_video_hour);
                template_left_video_play_button = view.findViewById(R.id.template_left_video_play_button);
                template_left_video_progress_bar = view.findViewById(R.id.template_left_video_progress_bar);
                template_left_video_length = view.findViewById(R.id.template_left_video_length);
                template_left_video_progress_box = view.findViewById(R.id.template_left_video_progress_box);
                template_left_video_center_box = view.findViewById(R.id.template_left_video_center_box);
                template_left_video_retry_box = view.findViewById(R.id.template_left_video_retry_box);

                //LEFT CONTACT TEMPLATE
                template_left_contact_hour = view.findViewById(R.id.template_left_contact_hour);
                template_left_contact_name = view.findViewById(R.id.template_left_contact_name);
                template_left_contact_number = view.findViewById(R.id.template_left_contact_number);
                template_left_contact_email = view.findViewById(R.id.template_left_contact_email);
                template_left_contact_picture =  view.findViewById(R.id.template_left_contact_picture);

                //LEFT DOCUMENT TEMPLATE
                template_left_document_hour = view.findViewById(R.id.template_left_document_hour);
                template_left_document_size = view.findViewById(R.id.template_left_document_size);
                template_left_document_name = view.findViewById(R.id.template_left_document_name);
                template_left_document_progress_bar = view.findViewById(R.id.template_left_document_progress_bar);
                template_left_document_progress_box = view.findViewById(R.id.template_left_document_progress_box);
                template_left_document_center_box = view.findViewById(R.id.template_left_document_center_box);
                template_left_document_retry_box = view.findViewById(R.id.template_left_document_retry_box);

                //LEFT AUDIO TEMPLATE
                template_left_audio_player = view.findViewById(R.id.template_left_audio_player);
                template_left_audio_hour = view.findViewById(R.id.template_left_audio_hour);
                template_left_audio_size = view.findViewById(R.id.template_left_audio_size);
                template_left_audio_progress_bar = view.findViewById(R.id.template_left_audio_progress_bar);
                template_left_audio_progress_box = view.findViewById(R.id.template_left_audio_progress_box);
                template_left_audio_center_box = view.findViewById(R.id.template_left_audio_center_box);
                template_left_audio_retry_box = view.findViewById(R.id.template_left_audio_retry_box);

        }
    }

    public ChatsAdapter(Context ctx, List<MessengerChat> list, ChatsAdapterListener l){

        this.context = ctx;
        this.messengerChatList = list;
        this.listener = l;

        audioPlayerList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {

        boolean fromUser = messengerChatList.get(position).getSenderId().equals(getLocalUserData(context).getId());

        // Prise en main des liens
        EmojiAndSocialTextView.OnLinkClickListener linkClickListener = (linkType, matchedText) -> {
            switch (linkType){

                case LINK_EMAIL:
                    break;

                case LINK_HASHTAG:
                    break;

                case LINK_PHONE:
                    break;

                case LINK_URL:
                    break;

            }
        };

        // Gestion prématurée des écouteurs de clicks
        switch (messengerChatList.get(position).getMessageType()){

            case TEXT_MESSAGE:

                itemView = LayoutInflater
                                .from(parent.getContext())
                                .inflate(fromUser
                                            ? R.layout.template_right_text_message
                                            : R.layout.template_left_text_message, parent,false);

                myViewHolder = new MyViewHolder(itemView);

                if (fromUser){
                    myViewHolder.template_right_text_txt.setOnLinkClickListener(linkClickListener);
                }else{
                    myViewHolder.template_left_text_txt.setOnLinkClickListener(linkClickListener);
                }

                break;

            case IMAGE_MESSAGE:

                itemView = LayoutInflater
                                .from(parent.getContext())
                                .inflate(fromUser
                                            ? R.layout.template_right_image_message
                                            : R.layout.template_left_image_message, parent,false);

                myViewHolder = new MyViewHolder(itemView);

                if (fromUser){

                    myViewHolder.template_right_image_legend.setOnLinkClickListener(linkClickListener);
                    // Affichage de l'image
                    myViewHolder.template_right_image_img.setOnClickListener(view -> listener.openMessageImage(messengerChatList.get(position)));

                }else{
                    myViewHolder.template_left_image_legend.setOnLinkClickListener(linkClickListener);

                    if (messengerChatList.get(position).getMessageState() == MESSAGE_READ) {
                        // Affichage de l'image
                        myViewHolder.template_left_image_img.setOnClickListener(view -> listener.openMessageImage(messengerChatList.get(position)));
                    }
                }

                break;

            case VIDEO_MESSAGE:

                itemView = LayoutInflater
                                .from(parent.getContext())
                                .inflate(fromUser
                                            ? R.layout.template_right_video_message
                                            : R.layout.template_left_video_message, parent,false);

                myViewHolder = new MyViewHolder(itemView);

                if (fromUser){

                    myViewHolder.template_right_video_legend.setOnLinkClickListener(linkClickListener);
                    // Ouverture de la video
                    myViewHolder.template_right_video_play_button.setOnClickListener(view -> listener.openMessageVideo(messengerChatList.get(position)));

                }else{
                    myViewHolder.template_left_video_legend.setOnLinkClickListener(linkClickListener);

                    if (messengerChatList.get(position).getMessageState() == MESSAGE_READ){

                        // Ouverture de la video
                        myViewHolder.template_left_video_play_button.setOnClickListener(view -> listener.openMessageVideo(messengerChatList.get(position)));
                    }
                }

                break;

            case DOCUMENT_MESSAGE:

                itemView = LayoutInflater
                                .from(parent.getContext())
                                .inflate(fromUser
                                            ? R.layout.template_right_document_message
                                            : R.layout.template_left_document_message, parent,false);

                myViewHolder = new MyViewHolder(itemView);

                if (fromUser){

                    // Ouverture du document
                    myViewHolder.right_document_message_content.setOnClickListener(view -> listener.openMessageDocument(messengerChatList.get(position)));

                }else{
                    if (messengerChatList.get(position).getMessageState() == MESSAGE_READ){

                        // Ouverture du document
                        myViewHolder.left_document_message_content.setOnClickListener(view -> listener.openMessageDocument(messengerChatList.get(position)));

                    }
                }
                break;

            case CONTACT_MESSAGE:

                itemView = LayoutInflater
                                .from(parent.getContext())
                                .inflate(fromUser
                                            ? R.layout.template_right_contact_message
                                            : R.layout.template_left_contact_message, parent,false);

                myViewHolder = new MyViewHolder(itemView);

                if (fromUser){

                    // Affichage du contact
                    myViewHolder.right_contact_message_content.setOnClickListener(view -> listener.openMessageContact(messengerChatList.get(position)));

                }else {

                    // Affichage du contact
                    if (messengerChatList.get(position).getMessageState() == MESSAGE_READ){

                        myViewHolder.left_contact_message_content.setOnClickListener(view -> listener.openMessageContact(messengerChatList.get(position)));

                    }

                }
                break;

            case DATE_MESSAGE:

                itemView = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.template_date_message, parent,false);

                myViewHolder = new MyViewHolder(itemView);
                break;

        }

        // Options message
        if (fromUser && messengerChatList.get(position).getMessageType() != DATE_MESSAGE){

            itemView.setOnLongClickListener(view -> {

                // Construction de la boîte de dialogue
                AlertDialog.Builder deleteMsgDialog = new AlertDialog.Builder(context);

                String[] deleteMsgDialogItems = {context.getString(R.string.delete_message)};

                deleteMsgDialog.setItems(deleteMsgDialogItems,
                        (dialog, which) -> {
                            if (which == 0) {
                                if (messengerChatList.get(position - 1) != null
                                        && messengerChatList.get(position - 1).getMessageType() == DATE_MESSAGE
                                        && messengerChatList.size() - (position + 1) < 1
                                        || messengerChatList.size() - (position + 1) >= 1
                                        && messengerChatList.get(position + 1).getMessageType() == DATE_MESSAGE) {

                                    // Supprimer récursivement le message
                                    listener.deleteMessageRecursively(messengerChatList.get(position - 1).getMessageId(),
                                            messengerChatList.get(position).getMessageId(),
                                            messengerChatList.size() - position == 1);

                                } else {

                                    // Supprimer le message
                                    listener.deleteMessage(
                                            messengerChatList.get(position).getMessageId(),
                                            messengerChatList.size() - position == 1);

                                }
                            }
                        });

                deleteMsgDialog.show();

                return false;
            });
        }

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MessengerChat messengerChat = messengerChatList.get(position);

        if (messengerChat == null){ return; }

        if (messengerChat.getMessageType() == DATE_MESSAGE){ // MESSAGE DE TYPE DATE

            // Définition de la date
            holder.template_date_date.setText(MessengerChat.extractDateFromDateId(context, messengerChat.getDateId()).toUpperCase());

        }else { // AUTRES TYPES DE MESSAGE

            if (messengerChat.getSenderId().equals(getLocalUserData(context).getId())) {

                switch (messengerChat.getMessageType()) {

                    case DOCUMENT_MESSAGE:
                        //INITIALISATION
                        holder.template_right_document_center_box.setVisibility(View.VISIBLE);
                        holder.template_right_document_progress_box.setVisibility(View.VISIBLE);
                        holder.template_right_document_retry_box.setVisibility(View.GONE);

                        // GLOBAL VIEW
                        holder.global_right_hour = holder.template_right_document_hour;
                        holder.global_right_state = holder.template_right_document_state;
                        holder.global_right_content = holder.right_document_message_content;

                        // Formatage du nom de fichier et des métadonnées
                        String[] nameTab = messengerChat.getMessageFileName().split("\\.");
                        String documentMetaData = (nameTab.length > 1 ? nameTab[1].toUpperCase() + " . " : EMPTY) + messengerChat.getFileSize();

                        holder.template_right_document_name.setText(cutLongText(nameTab[0], 15));
                        holder.template_right_document_size.setText(documentMetaData);

                        // Si le message est envoyé ou lu
                        if (messengerChat.getMessageState() != MESSAGE_LOAD){

                            // Disparition du box de téléversement
                            holder.template_right_document_center_box.setVisibility(View.GONE);

                            // Désactivation de l'exécutable de progression
                            if (documentProgressHandler != null) {
                                documentProgressHandler.removeCallbacks(documentProgressChecker);
                            }

                        }else { // Sinon si le message est chargé

                            // Initialisation de la barre de progression
                            holder.template_right_document_progress_bar.setMax(100);
                            holder.template_right_document_progress_bar.setProgress(0);

                            // Instanciation du gestionnaire de progression
                            documentProgressHandler = new Handler();

                            // Instanciation de l'exécutable de progression
                            documentProgressChecker = () -> {
                                try {

                                    // Récupération de la variable de progression
                                    String progress = getStringPreference(context, MESSENGER_UPLOAD_PREFS, messengerChat.getMessageId(), null);
                                    if (progress != null){

                                        int intProgress = Integer.valueOf(progress.split("\\.")[0]);

                                        // Mise à jour de la barre de progression
                                        holder.template_right_document_progress_bar.setProgress(intProgress);

                                        // Désactivation de l'exécutable de progression en fin de télévèrsement
                                        if (intProgress == 100){
                                            documentProgressHandler.removeCallbacks(documentProgressChecker);

                                            // Suppression de la variable de progression
                                            removePreference(context, MESSENGER_UPLOAD_PREFS, messengerChat.getMessageId());
                                        }
                                    }

                                }finally {
                                    documentProgressHandler.postDelayed(documentProgressChecker, 500);
                                }
                            };

                            // Lancement de l'exécutable de progression
                            documentProgressChecker.run();

                        }

                        // Switcher barre de progression / reprise de tâche
                        holder.template_right_document_center_box.setOnClickListener(view -> {

                            if (holder.template_right_document_progress_box.getVisibility() == View.VISIBLE){

                                holder.template_right_document_progress_box.setVisibility(View.GONE);
                                holder.template_right_document_retry_box.setVisibility(View.VISIBLE);

                                // Annulation du téléversement
                                listener.cancelUploadTask(messengerChat);

                                // Désactivation de l'exécutable de progression
                                if (documentProgressHandler != null) {
                                    documentProgressHandler.removeCallbacks(documentProgressChecker);
                                }
                            }else {

                                holder.template_right_document_progress_box.setVisibility(View.VISIBLE);
                                holder.template_right_document_retry_box.setVisibility(View.GONE);

                                // Lancement du téléversement
                                listener.startUploadTask(messengerChat);
                            }

                        });

                        break;

                    case CONTACT_MESSAGE:
                        //INITIALISATION
                        holder.template_right_contact_number.setVisibility(View.VISIBLE);
                        holder.template_right_contact_email.setVisibility(View.VISIBLE);

                        // GLOBAL VIEW
                        holder.global_right_state = holder.template_right_contact_state;
                        holder.global_right_hour = holder.template_right_contact_hour;
                        holder.global_right_content = holder.right_contact_message_content;

                        // Formatage du nom de contact
                        holder.template_right_contact_name.setText(cutLongText(messengerChat.getContactName(), 15));

                        // Définition du numéro du contact
                        if (messengerChat.getContactNumber() != null) {
                            holder.template_right_contact_number.setText(messengerChat.getContactNumber());
                        }else {
                            holder.template_right_contact_number.setVisibility(View.GONE);
                        }

                        // Définition du mail du contact
                        if (messengerChat.getContactEmail() != null) {
                            holder.template_right_contact_email.setText(messengerChat.getContactEmail());
                        }else {
                            holder.template_right_contact_email.setVisibility(View.GONE);
                        }

                        // Définition de la photo de profil du contact
                        holder.template_right_contact_picture.setImageDrawable(TextDrawable.builder()
                                            .buildRound(getFirstLetters(messengerChat.getContactName()),
                                            COLORS[getDigitFromString(messengerChat.getContactName())]));

                        break;

                    case AUDIO_MESSAGE:
                        //INITIALISATION
                        holder.template_right_audio_center_box.setVisibility(View.VISIBLE);
                        holder.template_right_audio_progress_box.setVisibility(View.VISIBLE);
                        holder.template_right_audio_retry_box.setVisibility(View.GONE);

                        // GLOBAL VIEW
                        holder.global_right_hour = holder.template_right_audio_hour;
                        holder.global_right_state = holder.template_right_audio_state;
                        holder.global_right_content = holder.right_audio_message_content;

                        // Formatage du nom de fichier et des métadonnées
                        String[] audioNameTab = messengerChat.getMessageFileName().split("\\.");
                        String audioMetaData = (audioNameTab.length > 1 ? audioNameTab[1].toUpperCase() + " . " : EMPTY) + messengerChat.getFileSize();

                        holder.template_right_audio_size.setText(audioMetaData);

                        // Arrêt des lecteurs en cours
                        if (audioPlayerList != null){
                            for (BasicAudioPlayer audioPlayer : audioPlayerList){
                                if (!holder.template_right_audio_player.equals(audioPlayer)) {
                                    audioPlayer.stopPlaying();
                                }
                            }
                        }

                        // Lancement du lecteur audio
                        if (holder.template_right_audio_player.getCurrentUri() == null) {
                            // Chargement du lecteur audio
                            holder.template_right_audio_player.loadAudioUri(messengerChat.extractMessageUri(), messengerChat.getMediaFileLength());

                            // Ajout du lecteur audio à la liste
                            audioPlayerList.add(holder.template_right_audio_player);

                            listener.storeAudioPlayers(holder.template_right_audio_player);
                        }

                        // Si le message est envoyé ou lu
                        if (messengerChat.getMessageState() != MESSAGE_LOAD){

                            // Disparition du box de téléversement
                            holder.template_right_audio_center_box.setVisibility(View.GONE);

                            // Désactivation de l'exécutable de progression
                            if (audioProgressHandler != null) {
                                audioProgressHandler.removeCallbacks(audioProgressChecker);
                            }

                        }else { // Sinon si le message est chargé

                            // Initialisation de la barre de progression
                            holder.template_right_audio_progress_bar.setMax(100);
                            holder.template_right_audio_progress_bar.setProgress(0);

                            // Instanciation du gestionnaire de progression
                            audioProgressHandler = new Handler();

                            // Instanciation de l'exécutable de progression
                            audioProgressChecker = () -> {
                                try {

                                    // Récupération de la variable de progression
                                    String progress = getStringPreference(context, MESSENGER_UPLOAD_PREFS, messengerChat.getMessageId(), null);
                                    if (progress != null){

                                        int intProgress = Integer.valueOf(progress.split("\\.")[0]);

                                        // Mise à jour de la barre de progression
                                        holder.template_right_audio_progress_bar.setProgress(intProgress);

                                        // Désactivation de l'exécutable de progression en fin de télévèrsement
                                        if (intProgress == 100){
                                            audioProgressHandler.removeCallbacks(audioProgressChecker);

                                            // Suppression de la variable de progression
                                            removePreference(context, MESSENGER_UPLOAD_PREFS, messengerChat.getMessageId());
                                        }
                                    }

                                }finally {
                                    audioProgressHandler.postDelayed(audioProgressChecker, 500);
                                }
                            };

                            // Lancement de l'exécutable de progression
                            audioProgressChecker.run();

                        }

                        // Switcher barre de progression / reprise de tâche
                        holder.template_right_audio_center_box.setOnClickListener(view -> {

                            if (holder.template_right_audio_progress_box.getVisibility() == View.VISIBLE){

                                holder.template_right_audio_progress_box.setVisibility(View.GONE);
                                holder.template_right_audio_retry_box.setVisibility(View.VISIBLE);

                                // Annulation du téléversement
                                listener.cancelUploadTask(messengerChat);

                                // Désactivation de l'exécutable de progression
                                if (audioProgressHandler != null) {
                                    audioProgressHandler.removeCallbacks(audioProgressChecker);
                                }
                            }else {

                                holder.template_right_audio_progress_box.setVisibility(View.VISIBLE);
                                holder.template_right_audio_retry_box.setVisibility(View.GONE);

                                // Lancement du téléversement
                                listener.startUploadTask(messengerChat);
                            }

                        });
                        
                        break;

                    case VIDEO_MESSAGE:
                        //INITIALISATION
                        holder.template_right_video_center_box.setVisibility(View.VISIBLE);
                        holder.template_right_video_legend.setVisibility(View.VISIBLE);
                        holder.template_right_video_progress_box.setVisibility(View.VISIBLE);
                        holder.template_right_video_retry_box.setVisibility(View.GONE);

                        // GLOBAL VIEW
                        holder.global_right_state = holder.template_right_video_state;
                        holder.global_right_hour = holder.template_right_video_hour;
                        holder.global_right_content = holder.right_video_message_content;

                        // Définition de la longueur de la vidéo
                        holder.template_right_video_length.setText(messengerChat.getMediaFileLength());

                        // Si la vidéo a une légende
                        if (messengerChat.getMessageLegend() != null){

                            // Définition de la légende
                            holder.template_right_video_legend.setLinkText(messengerChat.getMessageLegend());

                        }else {
                            holder.template_right_video_legend.setVisibility(View.GONE);
                        }

                        // Chargement de l'aperçu
                        GlideApp
                                .with(context.getApplicationContext())
                                .load(messengerChat.extractVideoThumbnailUri() != null
                                        ? messengerChat.extractVideoThumbnailUri()
                                        : messengerChat.getVideoThumbnailDownloadUrl())
                                .thumbnail(0.1f)
                                .into(holder.template_right_video_thumbnail);


                        // Si le message est envoyé ou lu
                        if (messengerChat.getMessageState() != MESSAGE_LOAD){

                            // Disparition du box de téléversement
                            holder.template_right_video_center_box.setVisibility(View.GONE);

                            // Désactivation de l'exécutable de progression
                            if (videoProgressHandler != null) {
                                videoProgressHandler.removeCallbacks(videoProgressChecker);
                            }

                        }else { // Sinon si le message est chargé

                            // Initialisation de la barre de progression
                            holder.template_right_video_progress_bar.setMax(100);
                            holder.template_right_video_progress_bar.setProgress(0);

                            // Instanciation du gestionnaire de progression
                            videoProgressHandler = new Handler();

                            // Instanciation de l'exécutable de progression
                            videoProgressChecker = () -> {
                                try {

                                    // Récupération de la variable de progression
                                    String progress = getStringPreference(context, MESSENGER_UPLOAD_PREFS, messengerChat.getMessageId(), null);
                                    if (progress != null){

                                        int intProgress = Integer.valueOf(progress.split("\\.")[0]);

                                        // Mise à jour de la barre de progression
                                        holder.template_right_video_progress_bar.setProgress(intProgress);

                                        // Désactivation de l'exécutable de progression en fin de télévèrsement
                                        if (intProgress == 100){
                                            videoProgressHandler.removeCallbacks(videoProgressChecker);

                                            // Suppression de la variable de progression
                                            removePreference(context, MESSENGER_UPLOAD_PREFS, messengerChat.getMessageId());
                                        }
                                    }

                                }finally {
                                    videoProgressHandler.postDelayed(videoProgressChecker, 500);
                                }
                            };

                            // Lancement de l'exécutable de progression
                            videoProgressChecker.run();

                        }

                        // Switcher barre de progression / reprise de tâche
                        holder.template_right_video_center_box.setOnClickListener(view -> {

                            if (holder.template_right_video_progress_box.getVisibility() == View.VISIBLE){

                                holder.template_right_video_progress_box.setVisibility(View.GONE);
                                holder.template_right_video_retry_box.setVisibility(View.VISIBLE);

                                // Annulation du téléversement
                                listener.cancelUploadTask(messengerChat);

                                // Désactivation de l'exécutable de progression
                                if (videoProgressHandler != null) {
                                    videoProgressHandler.removeCallbacks(videoProgressChecker);
                                }
                            }else {

                                holder.template_right_video_progress_box.setVisibility(View.VISIBLE);
                                holder.template_right_video_retry_box.setVisibility(View.GONE);

                                // Lancement du téléversement
                                listener.startUploadTask(messengerChat);
                            }

                        });

                        break;

                    case IMAGE_MESSAGE:
                        //INITIALISATION
                        holder.template_right_image_center_box.setVisibility(View.VISIBLE);
                        holder.template_right_image_legend.setVisibility(View.VISIBLE);
                        holder.template_right_image_progress_box.setVisibility(View.VISIBLE);
                        holder.template_right_image_retry_box.setVisibility(View.GONE);

                        // GLOBAL VIEW
                        holder.global_right_hour = holder.template_right_image_hour;
                        holder.global_right_state = holder.template_right_image_state;
                        holder.global_right_content = holder.right_image_message_content;

                        // Si l'image a une légende
                        if (messengerChat.getMessageLegend() != null) {

                            // Définition de la légende
                            holder.template_right_image_legend.setLinkText(messengerChat.getMessageLegend());

                        }else {
                            holder.template_right_image_legend.setVisibility(View.GONE);
                        }

                        // Chargement de l'image locale
                        GlideApp
                                .with(context.getApplicationContext())
                                .load(messengerChat.extractMessageUri() != null
                                        ? messengerChat.extractMessageUri()
                                        : messengerChat.getMessageDownloadUrl())
                                .thumbnail(0.1f)
                                .into(holder.template_right_image_img);


                        // Si le message est envoyé ou lu
                        if (messengerChat.getMessageState() != MESSAGE_LOAD){

                            // Disparition du box de téléversement
                            holder.template_right_image_center_box.setVisibility(View.GONE);

                        }

                        // Switcher barre de progression / reprise de tâche
                        holder.template_right_image_center_box.setOnClickListener(view -> {

                            if (holder.template_right_image_progress_box.getVisibility() == View.VISIBLE){

                                holder.template_right_image_progress_box.setVisibility(View.GONE);
                                holder.template_right_image_retry_box.setVisibility(View.VISIBLE);

                                // Annulation du téléversement
                                listener.cancelUploadTask(messengerChat);

                            }else {

                                holder.template_right_image_progress_box.setVisibility(View.VISIBLE);
                                holder.template_right_image_retry_box.setVisibility(View.GONE);

                                // Lancement du téléversement
                                listener.startUploadTask(messengerChat);
                            }

                        });

                        break;

                    case TEXT_MESSAGE:

                        // GLOBAL VIEW
                        holder.global_right_hour = holder.template_right_text_hour;
                        holder.global_right_state = holder.template_right_text_state;
                        holder.global_right_content = holder.right_text_message_content;

                        // Définition du contenu
                        holder.template_right_text_txt.setLinkText(messengerChat.getMessageText());

                        // Si le message est chargé avec une connexion disponible
                        if (messengerChat.getMessageState() == MESSAGE_LOAD && Connectivity.getInstance(context).isOnline()) {

                            // Mise à jour du status du message : Envoyé
                            listener.setMessageStateToSend(messengerChat);

                        }

                        break;

                }

                // Définition globale de l'heure d'envoi du message
                holder.global_right_hour.setText(messengerChat.getMessageSendingDate().split(" ")[3]);

                if (messengerChat.getMessageState() == MESSAGE_SEND){ // Si le message est envoyé

                    // On coche l'indicateur d'état du message
                    holder.global_right_state.setImageResource(R.mipmap.ic_done_white_18dp);
                    holder.global_right_state.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

                }else if (messengerChat.getMessageState() == MESSAGE_READ){ // Sinon si le message est lu

                    // On double-coche l'indicateur d'état du message
                    holder.global_right_state.setImageResource(R.mipmap.ic_done_all_white_18dp);
                    holder.global_right_state.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

                }else { // Sinon s'il est juste chargé

                    // On met l'indicateur dans le mode correspondant
                    holder.global_right_state.setImageResource(R.mipmap.ic_schedule_white_18dp);
                    holder.global_right_state.setColorFilter(ContextCompat.getColor(context, R.color.dimgray), android.graphics.PorterDuff.Mode.SRC_IN);

                }

                if ((position + 1) < messengerChatList.size()){ // Si ce message n'est pas le dernier

                    if (messengerChatList.get(position + 1).getSenderId().equals(messengerChat.getSenderId())) {

                        // Et que le message suivant se trouve du même côté
                        // On applique la marge correspondante à cette situation
                        setMargins(holder.global_right_content, 80, 0, 0, 5);

                    }else {

                        // Et que le message suivant se trouve du côté opposé
                        // On applique la marge correspondante à cette situation
                        setMargins(holder.global_right_content, 80, 0, 0, 25);

                    }
                }

            } else {

                // Si le message est chargé dans le cas où l'utilisateur courant est le destinataire
                if (messengerChat.getMessageState() != MESSAGE_LOAD) {

                    switch (messengerChat.getMessageType()) {
                        case DOCUMENT_MESSAGE:
                            //INITIALISATION
                            holder.template_left_document_center_box.setVisibility(View.VISIBLE);

                            if (messengerChat.getMessageState() == MESSAGE_READ){ // Si le message est lu

                                // Disparition du box de téléchargement
                                holder.template_left_document_center_box.setVisibility(View.GONE);

                            }else{ // Sinon

                                if (getBooleanPreference(context, MESSENGER_PREFS, "autoDownload", false)){
                                    // Si le téléchargement automatique est activé,
                                    holder.template_left_document_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_document_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    startDocumentDownload(holder.template_left_document_progress_bar, messengerChat);

                                }else {

                                    holder.template_left_document_progress_box.setVisibility(View.GONE);
                                    holder.template_left_document_retry_box.setVisibility(View.VISIBLE);

                                }
                            }

                            // GLOBAL VIEW
                            holder.global_left_hour = holder.template_left_document_hour;
                            holder.global_left_content = holder.left_document_message_content;

                            // Formatage du nom et des métadonnées du fichier
                            String[] nameTab = messengerChat.getMessageFileName().split("\\.");
                            String documentMetaData = (nameTab.length > 1 ? nameTab[1].toUpperCase() + " . " : EMPTY) + messengerChat.getFileSize();

                            holder.template_left_document_name.setText(cutLongText(nameTab[0], 15));
                            holder.template_left_document_size.setText(documentMetaData);

                            // Switcher barre de progression / reprise
                            holder.template_left_document_center_box.setOnClickListener(view -> {

                                if (holder.template_left_document_progress_box.getVisibility() == View.VISIBLE){

                                    holder.template_left_document_progress_box.setVisibility(View.GONE);
                                    holder.template_left_document_retry_box.setVisibility(View.VISIBLE);

                                    // Annulation du téléchargement
                                    listener.cancelDownloadTask(messengerChat);

                                    // Désactivation de l'exécutable de progression
                                    if (documentProgressHandler != null) {
                                        documentProgressHandler.removeCallbacks(documentProgressChecker);
                                    }

                                }else {

                                    holder.template_left_document_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_document_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    startDocumentDownload(holder.template_left_document_progress_bar, messengerChat);

                                }

                            });

                            break;

                        case CONTACT_MESSAGE:
                            //INITIALISATION
                            holder.template_left_contact_number.setVisibility(View.VISIBLE);
                            holder.template_left_contact_email.setVisibility(View.VISIBLE);

                            // GLOBAL VIEW
                            holder.global_left_hour = holder.template_left_contact_hour;
                            holder.global_left_content = holder.left_contact_message_content;

                            // Définition du nom du contact
                            holder.template_left_contact_name.setText(cutLongText(messengerChat.getContactName(), 15));

                            // Définition du numéro du contact
                            if (messengerChat.getContactNumber() != null) {
                                holder.template_left_contact_number.setText(messengerChat.getContactNumber());
                            }else {
                                holder.template_left_contact_number.setVisibility(View.GONE);
                            }

                            // Définition de l'email du contact
                            if (messengerChat.getContactEmail() != null) {
                                holder.template_left_contact_email.setText(messengerChat.getContactEmail());
                            }else {
                                holder.template_left_contact_email.setVisibility(View.GONE);
                            }

                            // Définition de la photo de profil du contact
                            holder.template_left_contact_picture.setImageDrawable(TextDrawable.builder()
                                    .buildRound(getFirstLetters(messengerChat.getContactName()),
                                            COLORS[getDigitFromString(messengerChat.getContactName())]));

                            // Mise à jour du status du message : Lu
                            listener.setMessageStateToRead(messengerChat);

                            break;

                        case AUDIO_MESSAGE:
                            //INITIALISATION
                            holder.template_left_audio_center_box.setVisibility(View.VISIBLE);

                            if (messengerChat.getMessageState() == MESSAGE_READ){ // Si le message est lu

                                // Disparition du box de téléchargement
                                holder.template_left_audio_center_box.setVisibility(View.GONE);

                            }else { // Sinon
                                if (getBooleanPreference(context, MESSENGER_PREFS, "autoDownload", false)){
                                    // Si le téléchargement automatique est activé,
                                    holder.template_left_audio_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_audio_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    startAudioDownload(holder.template_left_audio_progress_bar, messengerChat);

                                }else {

                                    holder.template_left_audio_progress_box.setVisibility(View.GONE);
                                    holder.template_left_audio_retry_box.setVisibility(View.VISIBLE);

                                }
                            }

                            // GLOBAL VIEW
                            holder.global_left_hour = holder.template_left_audio_hour;
                            holder.global_left_content = holder.left_audio_message_content;

                            // Formatage du nom et des métadonnées du fichier
                            String[] audioNameTab = messengerChat.getMessageFileName().split("\\.");
                            String audioMetaData = (audioNameTab.length > 1 ? audioNameTab[1].toUpperCase() + " . " : EMPTY) + messengerChat.getFileSize();

                            holder.template_left_audio_size.setText(audioMetaData);

                            // Arrêt des lecteurs en cours
                            if (audioPlayerList != null){
                                for (BasicAudioPlayer audioPlayer : audioPlayerList){
                                    if (!holder.template_left_audio_player.equals(audioPlayer)) {
                                        audioPlayer.stopPlaying();
                                    }
                                }
                            }

                            // Lancement du lecteur audio
                            if (holder.template_left_audio_player.getCurrentUri() == null) {
                                // Chargement du lecteur audio
                                holder.template_left_audio_player.loadAudioUri(getUriFromPath(context, messengerChat.getMessageFinalPath()),
                                                                               messengerChat.getMediaFileLength());

                                // Ajout du lecteur audio à la liste
                                audioPlayerList.add(holder.template_left_audio_player);

                                listener.storeAudioPlayers(holder.template_left_audio_player);
                            }

                            // Switcher barre de progression / reprise
                            holder.template_left_audio_center_box.setOnClickListener(view -> {

                                if (holder.template_left_audio_progress_box.getVisibility() == View.VISIBLE){

                                    holder.template_left_audio_progress_box.setVisibility(View.GONE);
                                    holder.template_left_audio_retry_box.setVisibility(View.VISIBLE);

                                    // Annulation du téléchargement
                                    listener.cancelDownloadTask(messengerChat);

                                    // Désactivation de l'exécutable de progression
                                    if (audioProgressHandler != null) {
                                        audioProgressHandler.removeCallbacks(audioProgressChecker);
                                    }
                                }else {

                                    holder.template_left_audio_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_audio_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    startAudioDownload(holder.template_left_audio_progress_bar, messengerChat);
                                }

                            });

                            break;

                        case VIDEO_MESSAGE:
                            //INITIALISATION
                            holder.template_left_video_center_box.setVisibility(View.VISIBLE);
                            holder.template_left_video_legend.setVisibility(View.VISIBLE);

                            if (messengerChat.getMessageState() == MESSAGE_READ){ // Si le message est lu

                                // Disparition du box de téléchargement
                                holder.template_left_video_center_box.setVisibility(View.GONE);

                            }else{ // Sinon
                                if (getBooleanPreference(context, MESSENGER_PREFS, "autoDownload", false)){
                                    // Si le téléchargement automatique est activé,
                                    holder.template_left_video_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_video_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    startVideoDownload(holder.template_left_video_progress_bar, messengerChat);

                                }else {

                                    holder.template_left_video_progress_box.setVisibility(View.GONE);
                                    holder.template_left_video_retry_box.setVisibility(View.VISIBLE);

                                }
                            }

                            // GLOBAL VIEW
                            holder.global_left_hour = holder.template_left_video_hour;
                            holder.global_left_content = holder.left_video_message_content;

                            // Définition de la longueur de la vidéo
                            holder.template_left_video_length.setText(messengerChat.getMediaFileLength());

                            // Si la vidéo a une légende
                            if (messengerChat.getMessageLegend() != null){

                                // Définition de la légende
                                holder.template_left_video_legend.setLinkText(messengerChat.getMessageLegend());

                            }else {
                                holder.template_left_video_legend.setVisibility(View.GONE);
                            }

                            // Chargement de l'aperçu
                            GlideApp
                                    .with(context.getApplicationContext())
                                    .load(messengerChat.getVideoThumbnailDownloadUrl())
                                    .thumbnail(0.1f)
                                    .into(holder.template_left_video_thumbnail);


                            // Switcher barre de progression / reprise
                            holder.template_left_video_center_box.setOnClickListener(view -> {

                                if (holder.template_left_video_progress_box.getVisibility() == View.VISIBLE){

                                    holder.template_left_video_progress_box.setVisibility(View.GONE);
                                    holder.template_left_video_retry_box.setVisibility(View.VISIBLE);

                                    // Annulation du téléchargement
                                    listener.cancelDownloadTask(messengerChat);

                                    // Désactivation de l'exécutable de progression
                                    if (videoProgressHandler != null) {
                                        videoProgressHandler.removeCallbacks(videoProgressChecker);
                                    }

                                }else {

                                    holder.template_left_video_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_video_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    startVideoDownload(holder.template_left_video_progress_bar, messengerChat);
                                }

                            });
                            
                            break;

                        case IMAGE_MESSAGE:
                            //INITIALISATION
                            holder.template_left_image_center_box.setVisibility(View.VISIBLE);
                            holder.template_left_image_legend.setVisibility(View.VISIBLE);

                            if (messengerChat.getMessageState() == MESSAGE_READ){ // Si le message est lu

                                // Disparition du box de téléchargement
                                holder.template_left_image_center_box.setVisibility(View.GONE);

                            }else{ // Sinon
                                if (getBooleanPreference(context, MESSENGER_PREFS, "autoDownload", false)){
                                    // Si le téléchargement automatique est activé,
                                    holder.template_left_image_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_image_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    listener.startDownloadMessageFile(messengerChat, IMAGE_DIRECTORY);

                                }else {

                                    holder.template_left_image_progress_box.setVisibility(View.GONE);
                                    holder.template_left_image_retry_box.setVisibility(View.VISIBLE);

                                }
                            }

                            // GLOBAL VIEW
                            holder.global_left_hour = holder.template_left_image_hour;
                            holder.global_left_content = holder.left_image_message_content;

                            // Si l'image a une légende
                            if (messengerChat.getMessageLegend() != null) {

                                // Définition de la légende
                                holder.template_left_image_legend.setLinkText(messengerChat.getMessageLegend());

                            }else {
                                holder.template_left_image_legend.setVisibility(View.GONE);
                            }

                            // Chargement de l'image
                            if (messengerChat.getMessageState() == MESSAGE_READ){

                                GlideApp
                                        .with(context.getApplicationContext())
                                        .load(messengerChat.getMessageFinalPath())
                                        .thumbnail(0.1f)
                                        .into(holder.template_left_image_img);

                            }else {

                                GlideApp
                                        .with(context.getApplicationContext())
                                        .load(messengerChat.getMessageDownloadUrl())
                                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                                        .thumbnail(0.1f)
                                        .into(holder.template_left_image_img);

                            }

                            // Switcher barre de progression / reprise
                            holder.template_left_image_center_box.setOnClickListener(view -> {

                                if (holder.template_left_image_progress_box.getVisibility() == View.VISIBLE){

                                    holder.template_left_image_progress_box.setVisibility(View.GONE);
                                    holder.template_left_image_retry_box.setVisibility(View.VISIBLE);

                                    // Annulation du téléchargement
                                    listener.cancelDownloadTask(messengerChat);

                                }else {

                                    holder.template_left_image_progress_box.setVisibility(View.VISIBLE);
                                    holder.template_left_image_retry_box.setVisibility(View.GONE);

                                    // Lancement du téléchargement
                                    listener.startDownloadMessageFile(messengerChat, IMAGE_DIRECTORY);
                                }

                            });
                            
                            break;

                        case TEXT_MESSAGE:

                            // GLOBAL VIEW
                            holder.global_left_hour = holder.template_left_text_hour;
                            holder.global_left_content = holder.left_text_message_content;

                            // Définition du contenu
                            holder.template_left_text_txt.setLinkText(messengerChat.getMessageText());

                            // Mise à jour du status du message : Lu
                            listener.setMessageStateToRead(messengerChat);

                            break;

                    }

                    // Définition globale de l'heure d'envoi du message
                    holder.global_left_hour.setText(messengerChat.getMessageSendingDate().split(" ")[3]);

                    if ((position + 1) < messengerChatList.size()){ // Si ce message n'est pas le dernier

                        if (messengerChatList.get(position + 1).getSenderId().equals(messengerChat.getSenderId())) {

                            // Et que le message suivant se trouve du même côté
                            // On applique la marge correspondante à cette situation
                            setMargins(holder.global_left_content, 0, 0, 80, 5);

                        }else {

                            // Et que le message suivant se trouve du côté opposé
                            // On applique la marge correspondante à cette situation
                            setMargins(holder.global_left_content, 0, 0, 80, 25);

                        }
                    }

                }

            }
        }

    }

    /**
     * Lanceur de téléchargement de document
     * @param progressBar / Barre de progression
     * @param messengerChat / message
     */
    private void startDocumentDownload(final CircleProgressBar progressBar, final MessengerChat messengerChat){

        // Initialisation de la barre de progression
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Lancement du téléchargement
        listener.startDownloadMessageFile(messengerChat, DOCUMENT_DIRECTORY);

        // Instanciation du gestionnaire de progression
        documentProgressHandler = new Handler();

        // Instanciation de l'exécutable de progression
        documentProgressChecker = () -> {
            try {

                // Récupération de la variable de progression
                String progress = getStringPreference(context, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId(), null);
                if (progress != null){

                    int intProgress = Integer.valueOf(progress.split("\\.")[0]);

                    // Mise à jour de la barre de progression
                    progressBar.setProgress(intProgress);

                    // Désactivation de l'exécutable de progression en fin de téléchargement
                    if (intProgress == 100){
                        documentProgressHandler.removeCallbacks(documentProgressChecker);

                        // Suppression de la variable de progression
                        removePreference(context, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId());
                    }
                }

            }finally {
                documentProgressHandler.postDelayed(documentProgressChecker, 500);
            }
        };

        // Lancement de l'exécutable de progression
        documentProgressChecker.run();

    }

    /**
     * Lanceur de téléchargement de fichiers audio
     * @param progressBar / Barre de progression
     * @param messengerChat / message
     */
    private void startAudioDownload(final CircleProgressBar progressBar, final MessengerChat messengerChat){

        // Initialisation de la barre de progression
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Lancement du téléchargement
        listener.startDownloadMessageFile(messengerChat, AUDIO_DIRECTORY);

        // Instanciation du gestionnaire de progression
        audioProgressHandler = new Handler();

        // Instanciation de l'exécutable de progression
        audioProgressChecker = () -> {
            try {

                // Récupération de la variable de progression
                String progress = getStringPreference(context, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId(), null);
                if (progress != null){

                    int intProgress = Integer.valueOf(progress.split("\\.")[0]);

                    // Mise à jour de la barre de progression
                    progressBar.setProgress(intProgress);

                    // Désactivation de l'exécutable de progression en fin de téléchargement
                    if (intProgress == 100){
                        audioProgressHandler.removeCallbacks(audioProgressChecker);

                        // Suppression de la variable de progression
                        removePreference(context, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId());
                    }
                }

            }finally {
                audioProgressHandler.postDelayed(audioProgressChecker, 500);
            }
        };

        // Lancement de l'exécutable de progression
        audioProgressChecker.run();

    }

    /**
     * Lanceur de téléchargement de vidéos
     * @param progressBar / Barre de progression
     * @param messengerChat / message
     */
    private void startVideoDownload(final CircleProgressBar progressBar, final MessengerChat messengerChat){

        // Initialisation de la barre de progression
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Lancement du téléchargement
        listener.startDownloadMessageFile(messengerChat, VIDEO_DIRECTORY);

        // Instanciation du gestionnaire de progression
        videoProgressHandler = new Handler();

        // Instanciation de l'exécutable de progression
        videoProgressChecker = () -> {
            try {

                // Récupération de la variable de progression
                String progress = getStringPreference(context, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId(), null);
                if (progress != null){

                    int intProgress = Integer.valueOf(progress.split("\\.")[0]);

                    // Mise à jour de la barre de progression
                    progressBar.setProgress(intProgress);

                    // Désactivation de l'exécutable de progression en fin de téléchargement
                    if (intProgress == 100){
                        videoProgressHandler.removeCallbacks(videoProgressChecker);

                        // Suppression de la variable de progression
                        removePreference(context, MESSENGER_DOWNLOAD_PREFS, messengerChat.getMessageId());
                    }
                }

            }finally {
                videoProgressHandler.postDelayed(videoProgressChecker, 500);
            }
        };

        // Lancement de l'exécutable de progression
        videoProgressChecker.run();

    }

    @Override
    public int getItemCount() {
        return messengerChatList.size();
    }

    public interface ChatsAdapterListener{

        void setMessageStateToSend(MessengerChat messengerChat);
        void setMessageStateToRead(MessengerChat messengerChat);
        void cancelUploadTask(MessengerChat messengerChat);
        void startUploadTask(MessengerChat messengerChat);
        void cancelDownloadTask(MessengerChat messengerChat);
        void startDownloadMessageFile(MessengerChat messengerChat, String path);
        void storeAudioPlayers(BasicAudioPlayer audioPlayer);
        void deleteMessage(String id, boolean lastMsg);
        void deleteMessageRecursively(String prevId, String id, boolean lastMsg);
        void openMessageDocument(MessengerChat messengerChat);
        void openMessageContact(MessengerChat messengerChat);
        void openMessageImage(MessengerChat messengerChat);
        void openMessageVideo(MessengerChat messengerChat);

    }

}
