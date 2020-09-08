package com.neteru.afrikett.ui.activities.showcase_activities.posts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.CommentsAdapter;
import com.neteru.afrikett.core.models.RemoteDB.Comment;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Report;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.Timing;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.neteru.afrikett.core.utilities.AppUtilities.capitalize;
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
import static com.neteru.afrikett.core.utilities.Constants.PICTURE_DOWNLOAD_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.POST_REPORT;
import static com.neteru.afrikett.core.utilities.Constants.PRODUCT_AND_SERVICE;
import static com.neteru.afrikett.core.utilities.Constants.SHORT_DELAY;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;
import static com.neteru.afrikett.core.utilities.Constants.USER;

public class PostOverviewActivity extends AfrikettBaseActivity {

    private String id;
    private String[] optionsDialogItems;
    private String showcaseName;

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
    private ImageView contact;
    private ImageView save;
    private ImageView share;
    private TextView ratingCounter;
    private TextView commentCounter;

    private DatabaseReference databaseReference;
    private AlertDialog ratingDialog;
    private CommentsAdapter adapter;
    private RecyclerView recycler;
    private TextView noComment;
    private Post currentPost;
    private String userId;
    private String legend;
    private TextView title;
    private Boolean keyboard;
    private Boolean admin;
    private List<Comment> commentList = new ArrayList<>();
    private InputMethodManager imm;

    private EmojiEditText emojiEditText;
    private ConstraintLayout rootView;
    private EmojiPopup emojiPopup;
    private ImageButton emojiBut;
    private ImageButton sendBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_overview);

        id = getIntent().getStringExtra("id");
        keyboard = getIntent().getBooleanExtra("keyboard", false);
        admin = getIntent().getBooleanExtra("admin", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            ActionBar actionBar = getSupportActionBar();

            // Customisation de la couleur du bouton retour de la boîte d'outils
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);

            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        title = findViewById(R.id.title);
        preview = findViewById(R.id.preview);
        noComment = findViewById(R.id.no_comment);
        recycler = findViewById(R.id.comments_recycler);

        singleImgBox = findViewById(R.id.a);
        doubleImgBox = findViewById(R.id.b);
        tripleImgBox = findViewById(R.id.c);
        quadrupleImgBox = findViewById(R.id.d);

        singleFirstImg = findViewById(R.id.aa);

        doubleFirstImg = findViewById(R.id.ba);
        doubleSecondImg = findViewById(R.id.bb);

        tripleFirstImg = findViewById(R.id.ca);
        tripleSecondImg = findViewById(R.id.cba);
        tripleThirdImg = findViewById(R.id.cbba);

        quadrupleFirstImg = findViewById(R.id.da);
        quadrupleSecondImg = findViewById(R.id.dba);
        quadrupleThirdImg = findViewById(R.id.dbb);
        quadrupleFourthImg = findViewById(R.id.dbc);

        mask = findViewById(R.id.cbbb);

        logo = findViewById(R.id.logo);
        name = findViewById(R.id.name);
        options = findViewById(R.id.options);

        productAndService = findViewById(R.id.product_and_service);
        event = findViewById(R.id.event);
        news = findViewById(R.id.news);

        productOrServiceName = findViewById(R.id.product_or_service_name);
        productOrServiceAvailability = findViewById(R.id.product_or_service_availability);
        productOrServicePrice = findViewById(R.id.product_or_service_price);
        productOrServiceDescription = findViewById(R.id.product_or_service_description);

        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.event_location);
        eventDate = findViewById(R.id.event_date);
        eventHour = findViewById(R.id.event_hour);
        eventAbout = findViewById(R.id.event_about);

        newsTitle = findViewById(R.id.news_title);
        newsContent = findViewById(R.id.news_content);
        newsDate = findViewById(R.id.news_date);

        save = findViewById(R.id.action_save);
        share = findViewById(R.id.action_share);
        rating = findViewById(R.id.action_rating);
        contact = findViewById(R.id.action_contact);
        comment = findViewById(R.id.action_comment);
        ratingCounter = findViewById(R.id.rating_counter);
        commentCounter = findViewById(R.id.comment_counter);

        emojiBut = findViewById(R.id.comment_emoji_button);
        sendBut = findViewById(R.id.comment_send_button);
        emojiEditText = findViewById(R.id.comment_message_field);
        rootView = findViewById(R.id.root);

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        userId = getLocalUserData(this).getId();

        ViewCompat.setNestedScrollingEnabled(recycler, false);

        getData();
    }
    
    private void getData(){
        databaseReference
                .child("posts")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        currentPost = dataSnapshot.getValue(Post.class);
                        
                        setListeners();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    
    private void setListeners(){

        if (keyboard){

            emojiEditText.requestFocus();
            imm.showSoftInput(emojiEditText, InputMethodManager.SHOW_IMPLICIT);

        }else {
            emojiEditText.clearFocus();
        }

        // Constructeur du popup Emoji
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiPopupShownListener(() -> {

                    emojiBut.setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, R.color.skyblue), PorterDuff.Mode.MULTIPLY);
                    emojiBut.setImageResource(R.mipmap.ic_keyboard_white_24dp);

                })
                .setOnEmojiPopupDismissListener(() -> {

                    emojiBut.setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, R.color.skyblue), PorterDuff.Mode.MULTIPLY);
                    emojiBut.setImageResource(R.mipmap.ic_mood_white_24dp);

                })
                .build(emojiEditText);

        // Switcher emoji/keyboard
        emojiBut.setOnClickListener(view -> emojiPopup.toggle());

        sendBut.setOnClickListener(v -> {
            if (emojiEditText.getText() != null && !emojiEditText.getText().toString().isEmpty()){

                if (!Connectivity.getInstance(PostOverviewActivity.this).isOnline()){

                    Toast.makeText(PostOverviewActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
                    return;
                }

                sendComment(emojiEditText.getText().toString());
            }
        });

        if (currentPost.getPreviews() != null && !currentPost.getPreviews().isEmpty()){

            legend = getPostDescription(currentPost);

            switch (currentPost.getPreviews().size()){
                case 1:
                    singleFirstImg.setOnClickListener(v -> toSingleImageViewer(currentPost.getPreviews().get(0), legend));
                    break;

                case 2:
                    doubleFirstImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 0, legend));
                    doubleSecondImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 1, legend));
                    break;

                case 3:
                    tripleFirstImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 0, legend));
                    tripleSecondImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 1, legend));
                    tripleThirdImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 2, legend));
                    break;

                case 4:
                    quadrupleFirstImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 0, legend));
                    quadrupleSecondImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 1, legend));
                    quadrupleThirdImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 2, legend));
                    quadrupleFourthImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 3, legend));
                    break;

                default:
                    tripleFirstImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 0, legend));
                    tripleSecondImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 1, legend));
                    tripleThirdImg.setOnClickListener(v -> toMultipleImageViewer(currentPost.getPreviews(), 2, legend));

            }
        }

        View.OnClickListener ratingClickListener = v -> {

            if (!Connectivity.getInstance(PostOverviewActivity.this).isOnline()){

                Toast.makeText(PostOverviewActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            final DatabaseReference assessorDbReference = databaseReference
                    .child("posts")
                    .child(currentPost.getId())
                    .child("assessors")
                    .child(userId);

            @SuppressLint("InflateParams")
            View ratingView = LayoutInflater.from(PostOverviewActivity.this).inflate(R.layout.layout_rating, null);

            MaterialRatingBar ratingBar = ratingView.findViewById(R.id.rating_bar);
            ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {

                assessorDbReference
                        .setValue(rating);

                new Handler()
                        .postDelayed(() -> ratingDialog.dismiss(), SHORT_DELAY / 2);

            });

            AlertDialog.Builder builder = new AlertDialog.Builder(PostOverviewActivity.this);
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

                                        rating.setImageResource(R.mipmap.ic_star_border_white_24dp);
                                        rating.setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, R.color.dimgray), PorterDuff.Mode.SRC_IN);

                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        },

        commentClickListener = v -> {

            emojiEditText.requestFocus();
            imm.showSoftInput(emojiEditText, InputMethodManager.SHOW_IMPLICIT);

        };

        save.setOnClickListener(v -> {

            if (!Connectivity.getInstance(PostOverviewActivity.this).isOnline()){

                Toast.makeText(PostOverviewActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
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
                                        .addOnSuccessListener(aVoid -> Toast.makeText(PostOverviewActivity.this, PostOverviewActivity.this.getString(R.string.added), Toast.LENGTH_SHORT).show());

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

        options.setOnClickListener(v -> {

            if (!Connectivity.getInstance(PostOverviewActivity.this).isOnline()){

                Toast.makeText(PostOverviewActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder optionsDialog = new AlertDialog.Builder(PostOverviewActivity.this);

            optionsDialog.setItems(optionsDialogItems, (dialog, which) -> {

                switch (which) {
                    case 0:
                        if (admin) {
                            aboutPost(currentPost);
                        }else {
                            reportPost(currentPost);
                        }
                        break;

                    case 1:
                        if (admin){
                            deletePost(currentPost);
                        }else {
                            subscription();
                        }
                        break;

                    case 2:
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.i_am_interested))
                                .setMessage(getString(R.string.notify_your_interest_to_the_author))
                                .setPositiveButton(R.string.yes, (dialog1, which1) -> {

                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                        break;
                }
            });

            optionsDialog.show();
        });

        share.setOnClickListener(v -> {

            if (!Connectivity.getInstance(PostOverviewActivity.this).isOnline()){

                Toast.makeText(PostOverviewActivity.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            sharePost(this, currentPost);
        });

        contact.setVisibility(admin ? View.GONE : View.VISIBLE);
        contact.setOnClickListener(v -> contactShowcase());

        rating.setOnClickListener(ratingClickListener);
        ratingCounter.setOnClickListener(ratingClickListener);

        comment.setOnClickListener(commentClickListener);
        commentCounter.setOnClickListener(commentClickListener);

        loadData();
    }

    private void loadData() {

        if (currentPost.getPreviews() == null || currentPost.getPreviews().isEmpty()){

            preview.setVisibility(View.GONE);

        }else {

            singleImgBox.setVisibility(View.GONE);
            doubleImgBox.setVisibility(View.GONE);
            tripleImgBox.setVisibility(View.GONE);
            quadrupleImgBox.setVisibility(View.GONE);

            switch (currentPost.getPreviews().size()){
                case 1:
                    singleImgBox.setVisibility(View.VISIBLE);

                    ViewGroup.LayoutParams params = singleImgBox.getLayoutParams();
                    params.height = dpToPx(350);
                    params.width = MATCH_PARENT;
                    singleImgBox.setLayoutParams(params);

                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(0))
                            .into(singleFirstImg);
                    break;

                case 2:
                    doubleImgBox.setVisibility(View.VISIBLE);

                    params = doubleImgBox.getLayoutParams();
                    params.height = dpToPx(325);
                    params.width = MATCH_PARENT;
                    doubleImgBox.setLayoutParams(params);

                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(0))
                            .into(doubleFirstImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(1))
                            .into(doubleSecondImg);
                    break;

                case 3:
                    tripleImgBox.setVisibility(View.VISIBLE);
                    mask.setVisibility(View.GONE);

                    params = tripleImgBox.getLayoutParams();
                    params.height = dpToPx(300);
                    params.width = MATCH_PARENT;
                    tripleImgBox.setLayoutParams(params);

                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(0))
                            .into(tripleFirstImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(1))
                            .into(tripleSecondImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(2))
                            .into(tripleThirdImg);
                    break;

                case 4:
                    quadrupleImgBox.setVisibility(View.VISIBLE);

                    params = quadrupleImgBox.getLayoutParams();
                    params.height = dpToPx(250);
                    params.width = MATCH_PARENT;
                    quadrupleImgBox.setLayoutParams(params);

                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(0))
                            .into(quadrupleFirstImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(1))
                            .into(quadrupleSecondImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(2))
                            .into(quadrupleThirdImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(3))
                            .into(quadrupleFourthImg);
                    break;

                default:
                    tripleImgBox.setVisibility(View.VISIBLE);
                    mask.setVisibility(View.VISIBLE);

                    params = tripleImgBox.getLayoutParams();
                    params.height = dpToPx(275);
                    params.width = MATCH_PARENT;
                    tripleImgBox.setLayoutParams(params);

                    String maskStr = "+"+(currentPost.getPreviews().size() - 3);
                    mask.setText(maskStr);

                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(0))
                            .into(tripleFirstImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(1))
                            .into(tripleSecondImg);
                    Glide
                            .with(PostOverviewActivity.this)
                            .load(currentPost.getPreviews().get(2))
                            .into(tripleThirdImg);

            }
        }

        productAndService.setVisibility(View.GONE);
        event.setVisibility(View.GONE);
        news.setVisibility(View.GONE);

        switch (currentPost.getType()){
            case PRODUCT_AND_SERVICE:
                productAndService.setVisibility(View.VISIBLE);

                if (currentPost.getProductOrServicePrice() != null && !currentPost.getProductOrServicePrice().isEmpty()){
                    productOrServicePrice.setVisibility(View.VISIBLE);
                    String price = currentPost.getProductOrServicePrice()+"\t\t"+PostOverviewActivity.this.getString(R.string.fcfa);

                    productOrServicePrice.setText(price);
                }else {
                    productOrServicePrice.setVisibility(View.GONE);
                }

                productOrServiceName.setText(currentPost.getProductOrServiceName());
                productOrServiceDescription.setText(currentPost.getProductOrServiceDescription());

                productOrServiceAvailability
                        .setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, currentPost.getProductOrServiceAvailability() ? R.color.limegreen : R.color.red),
                                PorterDuff.Mode.SRC_IN);

                title.setText(getString(R.string.product_or_service_title));
                break;

            case EVENT:
                event.setVisibility(View.VISIBLE);

                eventName.setText(currentPost.getEventName());
                eventAbout.setText(currentPost.getEventAbout());
                eventLocation.setText(currentPost.getEventLocation());
                eventDate.setText(currentPost.getEventDate());
                setTextViewDrawableColor(eventDate, R.color.colorAccent);

                if (currentPost.getEventHour() != null){
                    eventHour.setText(currentPost.getEventHour());
                    setTextViewDrawableColor(eventHour, R.color.colorAccent);
                }else {
                    eventHour.setVisibility(View.GONE);
                }

                title.setText(getString(R.string.event_title));
                break;

            case NEWS:
                news.setVisibility(View.VISIBLE);

                newsTitle.setText(currentPost.getNewsTitle());
                newsContent.setText(currentPost.getNewsContent());
                newsDate.setText(Timing.getInstance(PostOverviewActivity.this, currentPost.getPostDate()).getTimePeriod());

                title.setText(getString(R.string.news_title));
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

                            logo.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcase.getName()), Color.parseColor(showcase.getSecondaryColor())));

                        }else{

                            Glide
                                    .with(PostOverviewActivity.this)
                                    .load(showcase.getLogo())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(logo);
                        }

                        showcaseName = showcase.getName();
                        name.setText(showcaseName);

                        View.OnClickListener onClickListener = v -> {

                            if (!admin){

                                startActivity(new Intent(PostOverviewActivity.this, SubscriberOverviewActivity.class)
                                        .putExtra("showcaseId", showcase.getId()));

                                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                            }

                        };

                        logo.setOnClickListener(onClickListener);
                        name.setOnClickListener(onClickListener);

                        if (admin){
                            optionsDialogItems = new String[]{getString(R.string.about), getString(R.string.delete_post)};
                        }else{
                            if (showcase.getSubscribers() != null && showcase.getSubscribers().contains(userId)){

                                optionsDialogItems = new String[]{getString(R.string.report_post), getString(R.string.unsubscribe), getString(R.string.i_am_interested)};

                            }else{

                                optionsDialogItems = new String[]{getString(R.string.report_post), getString(R.string.subscribe), getString(R.string.i_am_interested)};

                            }
                        }
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

                            ratingCounter.setText(EMPTY);
                            return;
                        }

                        List<Float> values = new ArrayList<>();
                        Float sum = 0f;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Float value = snapshot.getValue(Float.class);

                            if (value != null) {
                                sum += value;
                                values.add(value);
                            }
                        }

                        final String notation = String.valueOf(sum / values.size());

                        if (ratingCounter.getText() != null && ratingCounter.getText().toString().equals(notation)){
                            return;
                        }

                        databaseReference
                                .child("posts")
                                .child(currentPost.getId())
                                .child("notation")
                                .setValue(notation)
                                .addOnSuccessListener(aVoid -> ratingCounter.setText(notation.substring(0,3)));
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

                        rating.setImageResource(R.mipmap.ic_star_white_24dp);
                        rating.setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);

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
                            commentCounter.setText(EMPTY);
                            return;
                        }

                        commentCounter.setText(compactNumber(String.valueOf(dataSnapshot.getChildrenCount())));
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
                                save.setImageResource(R.mipmap.ic_shopping_cart_white_24dp);
                            }else {
                                save.setImageResource(R.mipmap.ic_bookmark_border_white_24dp);
                            }
                            save.setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, R.color.dimgray), PorterDuff.Mode.SRC_IN);

                            return;
                        }

                        if (currentPost.getType() == PRODUCT_AND_SERVICE && currentPost.getProductOrServicePrice() != null){
                            save.setImageResource(R.mipmap.ic_shopping_cart_white_24dp);
                        }else {
                            save.setImageResource(R.mipmap.ic_bookmark_white_24dp);
                        }
                        save.setColorFilter(ContextCompat.getColor(PostOverviewActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        loadComments();

    }

    private void loadComments() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        // Configuration du cache du recyclerView
        recycler.setItemViewCacheSize(20);
        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter = new CommentsAdapter(this, commentList, userId, currentPost.getShowcaseId(), databaseReference, comment -> {

        });
        recycler.setAdapter(adapter);

        databaseReference
                .child("comments")
                .child(currentPost.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            noComment.setVisibility(View.VISIBLE);
                            return;
                        }

                        noComment.setVisibility(View.GONE);

                        commentList.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            commentList.add(snapshot.getValue(Comment.class));
                        }

                        adapter.notifyDataSetChanged();

                        // Scroll au dernier message
                        scrollMyViewToBottom();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendComment(String content){

        DatabaseReference commentRef = databaseReference.child("comments").child(currentPost.getId());

        String key = commentRef.push().getKey();

        if (key == null){ return; }

        sendBut.setEnabled(false);

        Comment comment;
        if (admin){
            comment = new Comment(key, currentPost.getId(), currentPost.getShowcaseId(), currentPost.getShowcaseId(), SHOWCASE, content);
        }else {
            comment = new Comment(key, currentPost.getId(), currentPost.getShowcaseId(), userId, USER, content);
        }

        commentRef
                .child(key)
                .setValue(comment)
                .addOnCompleteListener(task -> {
                    sendBut.setEnabled(true);

                    if (task.isSuccessful()){
                        emojiEditText.setText(EMPTY);
                    }
                });

    }

    /**
     * Scroller en bas de liste
     */
    private void scrollMyViewToBottom() {
        recycler.smoothScrollToPosition(commentList.size() - 1);
    }

    private void toSingleImageViewer(String url, String legend) {

        startActivity(new Intent(this, ImageViewActivity.class)
                .putExtra(ImageViewActivity.LEGEND, legend)
                .putExtra(ImageViewActivity.URL, url));
        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

    }

    private void toMultipleImageViewer(ArrayList<String> previews, int position, String legend) {

        startActivity(new Intent(this, ImageViewActivity.class)
                .putExtra(ImageViewActivity.LEGEND, legend)
                .putExtra(ImageViewActivity.POSITION, position)
                .putStringArrayListExtra(ImageViewActivity.URL_LIST, previews));
        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

    }

    private void deletePost(final Post currentPost) {

        // Dialogue de confirmation de l'opération
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.delete_post_confirm_msg))
                .setPositiveButton(R.string.yes, (dialog, which) -> databaseReference
                        .child("posts")
                        .child(currentPost.getId())
                        .setValue(null)
                        .addOnSuccessListener(aVoid -> finish()))
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    private void reportPost(Post currentPost){

        new AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.report_post_title))
                .setMessage(getString(R.string.report_post_message))
                .setPositiveButton(getString(R.string.report), (dialogInterface, i) -> {

                    // Disparition de la boîte de dialogue courante
                    dialogInterface.dismiss();

                    // Boîte de commentaire
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostOverviewActivity.this);
                    builder.setTitle(R.string.report_post_title);

                    // Vue personnalisée
                    @SuppressLint("InflateParams")
                    View reportView = LayoutInflater.from(PostOverviewActivity.this)
                                                    .inflate(R.layout.layout_report_comment, null);

                    final EditText comment = reportView.findViewById(R.id.comment);

                    builder
                            .setPositiveButton(getString(R.string.send), (dialogInterface1, i1) -> {

                                DatabaseReference reportDbReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("reports");

                                String key = reportDbReference.push().getKey(), name = getPostTitle(currentPost);

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

                                        .addOnSuccessListener(aVoid -> Toast.makeText(PostOverviewActivity.this, getString(R.string.ur_request_will_be_processed), Toast.LENGTH_SHORT).show());


                            })
                            .setNegativeButton(getString(R.string.cancel), null)
                            .setView(reportView)
                            .show();

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void aboutPost(Post currentPost) {

        // Dialogue des info relatives à la publication
        if (userId.equals(currentPost.getAuthorId())) {

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.about))
                    .setMessage(getString(R.string.published_on_by_you, currentPost.getPostDate()))
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
                                byStr = getString(R.string.dash);
                            } else {
                                byStr = dataSnapshot.getValue(String.class);
                            }

                            new AlertDialog.Builder(PostOverviewActivity.this)
                                    .setTitle(getString(R.string.about))
                                    .setMessage(getString(R.string.published_on_by, currentPost.getPostDate(), byStr))
                                    .setPositiveButton(R.string.ok, null)
                                    .show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void subscription() {

        databaseReference
                .child("showcases")
                .child(currentPost.getShowcaseId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) return;

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null) return;

                        final List<String> subscribersList = showcase.getSubscribers();

                        if (subscribersList != null) {

                            if (subscribersList.contains(userId)) {

                                new AlertDialog.Builder(PostOverviewActivity.this)
                                        .setTitle(getString(R.string.unsubscribe))
                                        .setMessage(getString(R.string.unsubscribe_from_showcase) + showcase.getName())
                                        .setPositiveButton(R.string.yes, (dialog, which) -> {

                                            subscribersList.remove(userId);
                                            updateSubscriptionValues(false, subscribersList);

                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();

                            } else {

                                subscribersList.add(userId);

                                updateSubscriptionValues(true, subscribersList);

                            }

                        }else {

                            List<String> tempList = new ArrayList<>();
                            tempList.add(userId);
                            updateSubscriptionValues(true, tempList);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void updateSubscriptionValues(boolean increaseValue, List<String> subscribersList){

        DatabaseReference subscriptionDbReference = databaseReference.child("showcases").child(currentPost.getShowcaseId()),
                          userSubscriptionDbReference = databaseReference.child("users").child(userId).child("nbSubscriptions");

        setSubscribeItemStr(increaseValue);

        subscriptionDbReference.child("subscribers").setValue(subscribersList);
        subscriptionDbReference.child("nb_subscribers").setValue(subscribersList.size());

        userSubscriptionDbReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) return;

                        Integer nb = dataSnapshot.getValue(Integer.class);

                        if (nb != null){

                            if (increaseValue){
                                userSubscriptionDbReference.setValue(nb + 1);
                            }else if(nb > 0){
                                userSubscriptionDbReference.setValue(nb - 1);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void setSubscribeItemStr(boolean subscribed){

        if (subscribed){

            optionsDialogItems = new String[]{getString(R.string.report_post), getString(R.string.unsubscribe), getString(R.string.i_am_interested)};

        }else {

            optionsDialogItems = new String[]{getString(R.string.report_post), getString(R.string.subscribe), getString(R.string.i_am_interested)};

        }

    }

    private void contactShowcase() {

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

                            openChatBox();

                        }else { // Sinon

                            databaseReference
                                    .child("users")
                                    .child(userId)
                                    .child("name")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null) return;

                                            if (list.get(0).equals(currentPost.getShowcaseId())){
                                                writeNode(list.get(0), list.get(1), showcaseName, dataSnapshot.getValue(String.class));
                                            }else {
                                                writeNode(list.get(0), list.get(1), dataSnapshot.getValue(String.class), showcaseName);
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

    }

    private void writeNode(String id_0, String id_1, String name_0, String name_1){

        // Noeud final
        final MessengerNode node = new MessengerNode(id_0, id_1, name_0, name_1, true);

        // Création du noeud avant ouverture du box
        databaseReference
                .child("messengers")
                .child(id_0 + "&" + id_1)
                .setValue(node)
                .addOnSuccessListener(aVoid -> openChatBox());
    }

    private void openChatBox(){

        startActivity(new Intent(PostOverviewActivity.this, ChatBoxActivity.class)
                .putExtra("targetId", currentPost.getShowcaseId())
                .putExtra("captureBack", false)
                .putExtra("targetType", SHOWCASE));

        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

    }

    public static void sharePost(Context context, Post post){

        Random random = new Random();
        String title = getPostTitle(post);
        String description = getPostDescription(post);

        if (post.getPreviews() != null) {

            Glide.with(context)
                    .asBitmap()
                    .load(post.getPreviews().get(random.nextInt(post.getPreviews().size())))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            String imageFileName = "IMG_"+ Calendar.getInstance().getTimeInMillis()+".jpg";
                            File storageDir = new File(PICTURE_DOWNLOAD_DIRECTORY);
                            boolean success = false;

                            if (!storageDir.exists()){success = storageDir.mkdirs();}

                            if (success) {
                                File imageFile = new File(storageDir, imageFileName);
                                try {

                                    OutputStream fOutput = new FileOutputStream(imageFile);
                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, fOutput);
                                    fOutput.close();

                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    File f = new File(imageFile.getAbsolutePath());
                                    Uri contentUri = Uri.fromFile(f);
                                    mediaScanIntent.setData(contentUri);
                                    context.sendBroadcast(mediaScanIntent);

                                    if (contentUri != null) {
                                        // Construct a ShareIntent with link to image
                                        Intent shareIntent = new Intent();
                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, capitalize(title) + "\n\n" + description);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        shareIntent.setType("image/*");
                                        // Launch sharing dialog for image
                                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_the_post)));
                                    } else {
                                        // ...sharing failed, handle error
                                        Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                    Toast.makeText(context, R.string.error_occurred, Toast.LENGTH_SHORT).show();

                                }
                            }

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        }else {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, capitalize(title) + "\n\n" + description);
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_the_post)));

        }
    }

    public static String getPostTitle(Post post){
        String title = EMPTY;

        switch (post.getType()){
            case PRODUCT_AND_SERVICE:
                title = post.getProductOrServiceName();
                break;

            case EVENT:
                title = post.getEventName();
                break;

            case NEWS:
                title = post.getNewsTitle();
                break;
        }

        return title;
    }

    public static String getPostDescription(Post post){
        String description = EMPTY;

        switch (post.getType()){
            case PRODUCT_AND_SERVICE:
                description = post.getProductOrServiceDescription();
                break;

            case EVENT:
                description = post.getEventAbout();
                break;

            case NEWS:
                description = post.getNewsContent();
                break;
        }

        return description;
    }
}
