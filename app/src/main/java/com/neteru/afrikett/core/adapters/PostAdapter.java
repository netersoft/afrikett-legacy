package com.neteru.afrikett.core.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.enums.PostAdapterSource;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Report;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.Timing;
import com.neteru.afrikett.ui.activities.showcase_activities.posts.PostOverviewActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.neteru.afrikett.core.utilities.AppUtilities.compactNumber;
import static com.neteru.afrikett.core.utilities.AppUtilities.dpToPx;
import static com.neteru.afrikett.core.utilities.AppUtilities.generateKey;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setTextViewDrawableColor;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.EVENT;
import static com.neteru.afrikett.core.utilities.Constants.NEWS;
import static com.neteru.afrikett.core.utilities.Constants.POST_REPORT;
import static com.neteru.afrikett.core.utilities.Constants.PRODUCT_AND_SERVICE;
import static com.neteru.afrikett.core.utilities.Constants.SHORT_DELAY;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private DatabaseReference databaseReference;
    private PostAdapterListener listener;
    private AlertDialog ratingDialog;
    private List<Post> posts;
    private Context context;
    private int rowLayout;
    private String userId;
    private PostAdapterSource postAdapterSource;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout preview;
        private LinearLayout singleImgBox;
        private LinearLayout doubleImgBox;
        private LinearLayout tripleImgBox;
        private TableLayout quadrupleImgBox;
        private ImageView singleFirstImg;
        private ImageView doubleFirstImg;
        private ImageView doubleSecondImg;
        private ImageView tripleFirstImg;
        private ImageView tripleSecondImg;
        private ImageView tripleThirdImg;
        private ImageView quadrupleFirstImg;
        private ImageView quadrupleSecondImg;
        private ImageView quadrupleThirdImg;
        private ImageView quadrupleFourthImg;
        private TextView mask;

        private RelativeLayout info;
        private LinearLayout productAndService;
        private LinearLayout event;
        private LinearLayout news;
        private TextView productOrServiceName;
        private TextView productOrServicePrice;
        private TextView productOrServiceDescription;
        private TextView eventName;
        private TextView eventLocation;
        private TextView eventDate;
        private TextView eventHour;
        private TextView eventAbout;
        private TextView newsTitle;
        private TextView newsContent;
        private TextView newsDate;
        private ImageView productOrServiceAvailability;

        private ImageView logo;
        private ImageView options;
        private TextView name;

        private ImageView rating;
        private ImageView comment;
        private ImageView save;
        private ImageView share;
        private ImageView contact;
        private TextView ratingCounter;
        private TextView commentCounter;

        MyViewHolder(View view) {
            super(view);

            preview = view.findViewById(R.id.preview);

            singleImgBox = view.findViewById(R.id.a);
            doubleImgBox = view.findViewById(R.id.b);
            tripleImgBox = view.findViewById(R.id.c);
            quadrupleImgBox = view.findViewById(R.id.d);

            singleFirstImg = view.findViewById(R.id.aa);

            doubleFirstImg = view.findViewById(R.id.ba);
            doubleSecondImg = view.findViewById(R.id.bb);

            tripleFirstImg = view.findViewById(R.id.ca);
            tripleSecondImg = view.findViewById(R.id.cba);
            tripleThirdImg = view.findViewById(R.id.cbba);

            quadrupleFirstImg = view.findViewById(R.id.da);
            quadrupleSecondImg = view.findViewById(R.id.dba);
            quadrupleThirdImg = view.findViewById(R.id.dbb);
            quadrupleFourthImg = view.findViewById(R.id.dbc);

            mask = view.findViewById(R.id.cbbb);

            logo = view.findViewById(R.id.logo);
            name = view.findViewById(R.id.name);
            options = view.findViewById(R.id.options);

            info = view.findViewById(R.id.info);

            productAndService = view.findViewById(R.id.product_and_service);
            event = view.findViewById(R.id.event);
            news = view.findViewById(R.id.news);

            productOrServiceName = view.findViewById(R.id.product_or_service_name);
            productOrServiceAvailability = view.findViewById(R.id.product_or_service_availability);
            productOrServicePrice = view.findViewById(R.id.product_or_service_price);
            productOrServiceDescription = view.findViewById(R.id.product_or_service_description);

            eventName = view.findViewById(R.id.event_name);
            eventLocation = view.findViewById(R.id.event_location);
            eventDate = view.findViewById(R.id.event_date);
            eventHour = view.findViewById(R.id.event_hour);
            eventAbout = view.findViewById(R.id.event_about);

            newsTitle = view.findViewById(R.id.news_title);
            newsContent = view.findViewById(R.id.news_content);
            newsDate = view.findViewById(R.id.news_date);

            save = view.findViewById(R.id.action_save);
            share = view.findViewById(R.id.action_share);
            contact = view.findViewById(R.id.action_contact);
            rating = view.findViewById(R.id.action_rating);
            comment = view.findViewById(R.id.action_comment);
            ratingCounter = view.findViewById(R.id.rating_counter);
            commentCounter = view.findViewById(R.id.comment_counter);
        }

    }

    public PostAdapter(Context context, List<Post> posts, int rowLayout, PostAdapterSource postAdapterSource, PostAdapterListener listener){
        this.context = context;
        this.posts = posts;
        this.rowLayout = rowLayout;
        this.listener = listener;
        this.postAdapterSource = postAdapterSource;

        userId = getLocalUserData(context).getId();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        final MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent,false));

        final Post currentPost = posts.get(position);

        if (currentPost.getPreviews() != null && !currentPost.getPreviews().isEmpty()){

            String legend = EMPTY;

            switch (currentPost.getType()){
                case PRODUCT_AND_SERVICE:
                    legend = currentPost.getProductOrServiceDescription();
                    break;

                case EVENT:
                    legend = currentPost.getEventAbout();
                    break;

                case NEWS:
                    legend = currentPost.getNewsContent();
                    break;
            }

            final String l = legend;

            switch (currentPost.getPreviews().size()){
                case 1:
                    myViewHolder.singleFirstImg.setOnClickListener(v -> listener.toSingleImageViewer(currentPost.getPreviews().get(0), l));
                    break;

                case 2:
                    myViewHolder.doubleFirstImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 0, l));
                    myViewHolder.doubleSecondImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 1, l));
                    break;

                case 3:
                    myViewHolder.tripleFirstImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 0, l));
                    myViewHolder.tripleSecondImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 1, l));
                    myViewHolder.tripleThirdImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 2, l));
                    break;

                case 4:
                    myViewHolder.quadrupleFirstImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 0, l));
                    myViewHolder.quadrupleSecondImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 1, l));
                    myViewHolder.quadrupleThirdImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 2, l));
                    myViewHolder.quadrupleFourthImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 3, l));
                    break;

                default:
                    myViewHolder.tripleFirstImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 0, l));
                    myViewHolder.tripleSecondImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 1, l));
                    myViewHolder.tripleThirdImg.setOnClickListener(v -> listener.toMultipleImageViewer(currentPost.getPreviews(), 2, l));

            }
        }

        View.OnClickListener ratingClickListener = v -> {

            if (!Connectivity.getInstance(context).isOnline()){

                Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            final DatabaseReference assessorDbReference = databaseReference
                    .child("posts")
                    .child(currentPost.getId())
                    .child("assessors")
                    .child(userId);

            @SuppressLint("InflateParams")
            View ratingView = LayoutInflater.from(context).inflate(R.layout.layout_rating, null);

            MaterialRatingBar ratingBar = ratingView.findViewById(R.id.rating_bar);
            ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {

                assessorDbReference
                        .setValue(rating);

                new Handler()
                        .postDelayed(() -> ratingDialog.dismiss(), SHORT_DELAY / 2);

            });

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setCancelable(true)
                    .setView(ratingView);

            ratingDialog = builder.create();

            assessorDbReference
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null){

                                ratingDialog.show();
                                return;
                            }

                            assessorDbReference
                                    .setValue(null)
                                    .addOnSuccessListener(aVoid -> {

                                        myViewHolder.rating.setImageResource(R.mipmap.ic_star_border_white_24dp);
                                        myViewHolder.rating.setColorFilter(ContextCompat.getColor(context, R.color.dimgray), PorterDuff.Mode.SRC_IN);

                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        },

        commentClickListener = v -> listener.toPostOverview(currentPost, true);

        myViewHolder.save.setOnClickListener(v -> {

            if (!Connectivity.getInstance(context).isOnline()){

                Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            final DatabaseReference favoritesDbReference = databaseReference
                                                            .child("favorites")
                                                            .child(userId)
                                                            .child(currentPost.getId());

            favoritesDbReference
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null){

                                favoritesDbReference
                                        .setValue(currentPost.getId())
                                        .addOnSuccessListener(aVoid -> Toast.makeText(context, context.getString(R.string.added), Toast.LENGTH_SHORT).show());

                            }else {
                                favoritesDbReference
                                        .setValue(null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        });

        myViewHolder.options.setOnClickListener(v -> {

            if (!Connectivity.getInstance(context).isOnline()){

                Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder optionsDialog = new AlertDialog.Builder(context);

            String[] optionsDialogItems;
            if (postAdapterSource == PostAdapterSource.ADMIN) {
                optionsDialogItems = new String[]{context.getString(R.string.about), context.getString(R.string.delete_post)};
            }else {
                optionsDialogItems = new String[]{context.getString(R.string.i_am_interested), context.getString(R.string.report_post)};
            }

            optionsDialog.setItems(optionsDialogItems, (dialog, which) -> {

                switch (which){
                    case 0:

                        if (postAdapterSource == PostAdapterSource.ADMIN) {
                            // Dialogue des info relatives à la publication
                            if (userId.equals(currentPost.getAuthorId())) {

                                new AlertDialog.Builder(context)
                                        .setTitle(context.getString(R.string.about))
                                        .setMessage(context.getString(R.string.published_on_by_you, currentPost.getPostDate()))
                                        .setPositiveButton(R.string.ok, null)
                                        .show();

                            } else {
                                databaseReference
                                        .child("users")
                                        .child(currentPost.getAuthorId() != null ? currentPost.getAuthorId() : EMPTY)
                                        .child("name")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                String byStr;
                                                if (dataSnapshot.getValue() == null) {
                                                    byStr = context.getString(R.string.dash);
                                                } else {
                                                    byStr = dataSnapshot.getValue(String.class);
                                                }

                                                new AlertDialog.Builder(context)
                                                        .setTitle(context.getString(R.string.about))
                                                        .setMessage(context.getString(R.string.published_on_by, currentPost.getPostDate(), byStr))
                                                        .setPositiveButton(R.string.ok, null)
                                                        .show();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                        }else {

                            new AlertDialog.Builder(context)
                                    .setTitle(context.getString(R.string.i_am_interested))
                                    .setMessage(context.getString(R.string.notify_your_interest_to_the_author))
                                    .setPositiveButton(R.string.yes, (dialog1, which1) -> {

                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();

                        }
                        break;

                    case 1:

                        if (postAdapterSource == PostAdapterSource.ADMIN){

                            // Dialogue de confirmation de l'opération
                            new AlertDialog.Builder(context)
                                    .setTitle(context.getString(R.string.confirmation))
                                    .setMessage(context.getString(R.string.delete_post_confirm_msg))
                                    .setPositiveButton(R.string.yes, (dialog1, which1) -> listener.deletePost(currentPost))
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();

                        }else {

                            new AlertDialog
                                    .Builder(context)
                                    .setTitle(context.getString(R.string.report_post_title))
                                    .setMessage(context.getString(R.string.report_post_message))
                                    .setPositiveButton(context.getString(R.string.report), (dialogInterface, i) -> {

                                        // Disparition de la boîte de dialogue courante
                                        dialogInterface.dismiss();

                                        // Boîte de commentaire
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle(R.string.report_post_title);

                                        // Vue personnalisée
                                        @SuppressLint("InflateParams")
                                        View reportView = LayoutInflater.from(context)
                                                .inflate(R.layout.layout_report_comment, null);

                                        final EditText comment = reportView.findViewById(R.id.comment);

                                        builder
                                                .setPositiveButton(context.getString(R.string.send), (dialogInterface1, i1) -> {

                                                    DatabaseReference reportDbReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("reports");

                                                    String key = reportDbReference.push().getKey(), name = EMPTY;

                                                    switch (currentPost.getType()){
                                                        case PRODUCT_AND_SERVICE:
                                                            name = currentPost.getProductOrServiceName();
                                                            break;

                                                        case EVENT:
                                                            name = currentPost.getEventName();
                                                            break;

                                                        case NEWS:
                                                            name = currentPost.getNewsTitle();
                                                            break;
                                                    }

                                                    // Enregistrement du signalement
                                                    reportDbReference
                                                            .child(key != null ? key : generateKey(13))
                                                            .setValue(new Report(key,
                                                                    currentPost.getId(),
                                                                    name,
                                                                    currentPost.getPreviews() != null ? currentPost.getPreviews().get(0) : EMPTY,
                                                                    currentPost.getPostDate(),
                                                                    POST_REPORT,
                                                                    userId,
                                                                    comment.getText().toString()))

                                                            .addOnSuccessListener(aVoid -> Toast.makeText(context, context.getString(R.string.ur_request_will_be_processed), Toast.LENGTH_SHORT).show());


                                                })
                                                .setNegativeButton(context.getString(R.string.cancel), null)
                                                .setView(reportView)
                                                .show();

                                    })
                                    .setNegativeButton(context.getString(R.string.cancel), null)
                                    .show();

                        }

                        break;
                }
            });

            optionsDialog.show();
        });

        myViewHolder.share.setOnClickListener(v -> {

            if (!Connectivity.getInstance(context).isOnline()){

                Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            PostOverviewActivity.sharePost(context, currentPost);
        });

        myViewHolder.contact.setVisibility(postAdapterSource == PostAdapterSource.ADMIN ? View.GONE : View.VISIBLE);
        myViewHolder.contact.setOnClickListener(v -> {

            if (!Connectivity.getInstance(context).isOnline()){

                Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            // Génération du noeud de discussion
            final List<String> list = new ArrayList<>();
            list.add(userId);
            list.add(currentPost.getShowcaseId());
            Collections.sort(list);

            // Vérification du noeud
            databaseReference
                    .child("messengers")
                    .child(list.get(0) + "&" + list.get(1))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null){ // S'il existe

                                listener.openChatBox(currentPost);

                            }else { // Sinon

                                databaseReference
                                        .child("users")
                                        .child(userId)
                                        .child("name")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null) return;

                                                String userName = dataSnapshot.getValue(String.class);

                                                databaseReference
                                                        .child("showcases")
                                                        .child(currentPost.getShowcaseId())
                                                        .child("name")
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.getValue() == null) return;

                                                                String showcaseName = dataSnapshot.getValue(String.class);

                                                                // Noeud final
                                                                MessengerNode node;
                                                                if (list.get(0).equals(currentPost.getShowcaseId())){

                                                                    node = new MessengerNode(list.get(0), list.get(1), showcaseName, userName, true);

                                                                }else {

                                                                    node = new MessengerNode(list.get(0), list.get(1), userName, showcaseName, true);

                                                                }

                                                                // Création du noeud avant ouverture du box
                                                                databaseReference
                                                                        .child("messengers")
                                                                        .child(list.get(0) + "&" + list.get(1))
                                                                        .setValue(node)
                                                                        .addOnSuccessListener(aVoid -> listener.openChatBox(currentPost));

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

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
        });

        myViewHolder.info.setOnClickListener(v -> listener.toPostOverview(currentPost, false));

        myViewHolder.rating.setOnClickListener(ratingClickListener);
        myViewHolder.ratingCounter.setOnClickListener(ratingClickListener);

        myViewHolder.comment.setOnClickListener(commentClickListener);
        myViewHolder.commentCounter.setOnClickListener(commentClickListener);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {

        final Post currentPost = posts.get(position);

        if (currentPost.getPreviews() == null || currentPost.getPreviews().isEmpty()){

            myViewHolder.preview.setVisibility(View.GONE);

        }else {

            myViewHolder.singleImgBox.setVisibility(View.GONE);
            myViewHolder.doubleImgBox.setVisibility(View.GONE);
            myViewHolder.tripleImgBox.setVisibility(View.GONE);
            myViewHolder.quadrupleImgBox.setVisibility(View.GONE);

            switch (currentPost.getPreviews().size()){
                case 1:
                    myViewHolder.singleImgBox.setVisibility(View.VISIBLE);

                    ViewGroup.LayoutParams params = myViewHolder.singleImgBox.getLayoutParams();
                    params.height = dpToPx(400);
                    params.width = MATCH_PARENT;
                    myViewHolder.singleImgBox.setLayoutParams(params);

                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(0))
                            .into(myViewHolder.singleFirstImg);
                    break;

                case 2:
                    myViewHolder.doubleImgBox.setVisibility(View.VISIBLE);

                    params = myViewHolder.doubleImgBox.getLayoutParams();
                    params.height = dpToPx(375);
                    params.width = MATCH_PARENT;
                    myViewHolder.doubleImgBox.setLayoutParams(params);

                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(0))
                            .into(myViewHolder.doubleFirstImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(1))
                            .into(myViewHolder.doubleSecondImg);
                    break;

                case 3:
                    myViewHolder.tripleImgBox.setVisibility(View.VISIBLE);
                    myViewHolder.mask.setVisibility(View.GONE);

                    params = myViewHolder.tripleImgBox.getLayoutParams();
                    params.height = dpToPx(350);
                    params.width = MATCH_PARENT;
                    myViewHolder.tripleImgBox.setLayoutParams(params);

                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(0))
                            .into(myViewHolder.tripleFirstImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(1))
                            .into(myViewHolder.tripleSecondImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(2))
                            .into(myViewHolder.tripleThirdImg);
                    break;

                case 4:
                    myViewHolder.quadrupleImgBox.setVisibility(View.VISIBLE);

                    params = myViewHolder.quadrupleImgBox.getLayoutParams();
                    params.height = dpToPx(300);
                    params.width = MATCH_PARENT;
                    myViewHolder.quadrupleImgBox.setLayoutParams(params);

                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(0))
                            .into(myViewHolder.quadrupleFirstImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(1))
                            .into(myViewHolder.quadrupleSecondImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(2))
                            .into(myViewHolder.quadrupleThirdImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(3))
                            .into(myViewHolder.quadrupleFourthImg);
                    break;

                default:
                    myViewHolder.tripleImgBox.setVisibility(View.VISIBLE);
                    myViewHolder.mask.setVisibility(View.VISIBLE);

                    params = myViewHolder.tripleImgBox.getLayoutParams();
                    params.height = dpToPx(325);
                    params.width = MATCH_PARENT;
                    myViewHolder.tripleImgBox.setLayoutParams(params);

                    String maskStr = "+"+(currentPost.getPreviews().size() - 3);
                    myViewHolder.mask.setText(maskStr);

                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(0))
                            .into(myViewHolder.tripleFirstImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(1))
                            .into(myViewHolder.tripleSecondImg);
                    Glide
                            .with(context)
                            .load(currentPost.getPreviews().get(2))
                            .into(myViewHolder.tripleThirdImg);

            }
        }

        myViewHolder.productAndService.setVisibility(View.GONE);
        myViewHolder.event.setVisibility(View.GONE);
        myViewHolder.news.setVisibility(View.GONE);

        switch (currentPost.getType()){
            case PRODUCT_AND_SERVICE:
                myViewHolder.productAndService.setVisibility(View.VISIBLE);

                if (currentPost.getProductOrServicePrice() != null && !currentPost.getProductOrServicePrice().isEmpty()){
                    myViewHolder.productOrServicePrice.setVisibility(View.VISIBLE);
                    String price = currentPost.getProductOrServicePrice()+"\t\t"+context.getString(R.string.fcfa);

                    myViewHolder.productOrServicePrice.setText(price);
                }else {
                    myViewHolder.productOrServicePrice.setVisibility(View.GONE);
                }

                myViewHolder.productOrServiceName.setText(currentPost.getProductOrServiceName());
                myViewHolder.productOrServiceDescription.setText(currentPost.getProductOrServiceDescription());

                myViewHolder.productOrServiceAvailability
                        .setColorFilter(ContextCompat.getColor(context, currentPost.getProductOrServiceAvailability() ? R.color.limegreen : R.color.red),
                                PorterDuff.Mode.SRC_IN);

                break;

            case EVENT:
                myViewHolder.event.setVisibility(View.VISIBLE);

                myViewHolder.eventName.setText(currentPost.getEventName());
                myViewHolder.eventAbout.setText(currentPost.getEventAbout());
                myViewHolder.eventLocation.setText(currentPost.getEventLocation());
                myViewHolder.eventDate.setText(currentPost.getEventDate());
                setTextViewDrawableColor(myViewHolder.eventDate, R.color.colorAccent);

                if (currentPost.getEventHour() != null){
                    myViewHolder.eventHour.setText(currentPost.getEventHour());
                    setTextViewDrawableColor(myViewHolder.eventHour, R.color.colorAccent);
                }else {
                    myViewHolder.eventHour.setVisibility(View.GONE);
                }

                break;

            case NEWS:
                myViewHolder.news.setVisibility(View.VISIBLE);

                myViewHolder.newsTitle.setText(currentPost.getNewsTitle());
                myViewHolder.newsContent.setText(currentPost.getNewsContent());
                myViewHolder.newsDate.setText(Timing.getInstance(context, currentPost.getPostDate()).getTimePeriod());
                break;
        }

        databaseReference
                .child("showcases")
                .child(currentPost.getShowcaseId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){ return; }

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null){ return; }

                        if (showcase.getLogo().equals(DEFAULT)){

                            myViewHolder.logo.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcase.getName()), Color.parseColor(showcase.getSecondaryColor())));

                        }else{

                            Glide
                                    .with(context)
                                    .load(showcase.getLogo())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(myViewHolder.logo);
                        }

                        myViewHolder.name.setText(showcase.getName());

                        View.OnClickListener toShowcaseOverview = v -> {
                            if (postAdapterSource != PostAdapterSource.ADMIN && postAdapterSource != PostAdapterSource.SUBSCRIBER){
                                listener.toShowcaseOverview(showcase.getId());
                            }
                        };

                        myViewHolder.name.setOnClickListener(toShowcaseOverview);
                        myViewHolder.logo.setOnClickListener(toShowcaseOverview);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference
                .child("posts")
                .child(currentPost.getId())
                .child("assessors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            myViewHolder.ratingCounter.setText(EMPTY);
                            return;
                        }

                        List<Float> values = new ArrayList<>();
                        Float sum = 0f;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Float value = snapshot.getValue(Float.class);

                            if (value == null){ return; }

                            sum += value;
                            values.add(value);
                        }

                        final String notation = String.valueOf(sum / values.size());

                        if (myViewHolder.ratingCounter.getText() != null && myViewHolder.ratingCounter.getText().toString().equals(notation)){
                            return;
                        }

                        databaseReference
                                .child("posts")
                                .child(currentPost.getId())
                                .child("notation")
                                .setValue(notation)
                                .addOnSuccessListener(aVoid -> myViewHolder.ratingCounter.setText(notation.substring(0,3)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

        databaseReference
                .child("posts")
                .child(currentPost.getId())
                .child("assessors")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        myViewHolder.rating.setImageResource(R.mipmap.ic_star_white_24dp);
                        myViewHolder.rating.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference
                .child("comments")
                .child(currentPost.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            myViewHolder.commentCounter.setText(EMPTY);
                            return;
                        }

                        myViewHolder.commentCounter.setText(compactNumber(String.valueOf(dataSnapshot.getChildrenCount())));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference
                .child("favorites")
                .child(userId)
                .child(currentPost.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            if (currentPost.getType() == PRODUCT_AND_SERVICE && currentPost.getProductOrServicePrice() != null){
                                myViewHolder.save.setImageResource(R.mipmap.ic_shopping_cart_white_24dp);
                            }else {
                                myViewHolder.save.setImageResource(R.mipmap.ic_bookmark_border_white_24dp);
                            }
                            myViewHolder.save.setColorFilter(ContextCompat.getColor(context, R.color.dimgray), android.graphics.PorterDuff.Mode.SRC_IN);

                            return;
                        }

                        if (currentPost.getType() == PRODUCT_AND_SERVICE && currentPost.getProductOrServicePrice() != null){
                            myViewHolder.save.setImageResource(R.mipmap.ic_shopping_cart_white_24dp);
                        }else {
                            myViewHolder.save.setImageResource(R.mipmap.ic_bookmark_white_24dp);
                        }
                        myViewHolder.save.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public interface PostAdapterListener{
        void toSingleImageViewer(String url, String legend);
        void toMultipleImageViewer(ArrayList<String> urls, int position, String legend);
        void toPostOverview(Post post, Boolean keyboard);
        void openChatBox(Post post);
        void deletePost(Post post);
        void toShowcaseOverview(String showcaseId);
    }
}
