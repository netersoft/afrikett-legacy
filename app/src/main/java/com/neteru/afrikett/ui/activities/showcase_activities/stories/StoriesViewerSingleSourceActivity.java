package com.neteru.afrikett.ui.activities.showcase_activities.stories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import jp.shts.android.storiesprogressview.StoriesProgressView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Story;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.GlideApp;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.utilities.OnSwipeTouchListener;
import com.neteru.afrikett.core.utilities.Timing;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.MULTI;
import static com.neteru.afrikett.core.utilities.Constants.SINGLE;

public class StoriesViewerSingleSourceActivity extends AfrikettBaseActivity {

    private TouchImageView storyImg;
    private StoriesProgressView storiesProgressView;
    private DatabaseReference databaseReference;
    private int mode;
    private int position;
    private int mPaletteColor;
    private String userId;
    private String showcaseId;
    private String showcaseLogo;
    private String showcaseName;
    private String showcasePrimaryColor;
    private String showcaseSecondaryColor;
    private List<Story> stories = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private ConstraintLayout rootView;
    private Window window;
    private Toolbar toolbar;
    private ImageView storyAuthorImg;
    private TextView storyAuthorName;
    private TextView storyPublishDate;
    private TextView storyViewCounter;
    private LinearLayout storyViewCounterBox;
    private Boolean showViewCounter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_viewer_single_source);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.bringToFront();
        appBarLayout.invalidate();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rootView = findViewById(R.id.rootView);
        storyImg = findViewById(R.id.storyImg);
        storiesProgressView = findViewById(R.id.storyProgress);
        storyPublishDate = findViewById(R.id.storyPublishDate);
        storyAuthorName = findViewById(R.id.storyAuthorName);
        storyAuthorImg = findViewById(R.id.storyAuthorImg);
        storyViewCounter = findViewById(R.id.story_view_counter);
        storyViewCounterBox = findViewById(R.id.view_counter_box);

        // Variables de configuration
        mode = getIntent().getIntExtra("mode", SINGLE);
        position = getIntent().getIntExtra("position", 0);
        showcaseId = getIntent().getStringExtra("showcaseId");
        showcaseLogo = getIntent().getStringExtra("showcaseLogo");
        showcaseName = getIntent().getStringExtra("showcaseName");
        showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");
        showcaseSecondaryColor = getIntent().getStringExtra("showcaseSecondaryColor");
        showViewCounter = getIntent().getBooleanExtra("showViewCounter", false);

        loadingDialog = new LoadingDialog(this);

        window = getWindow();

        userId = getLocalUserData(this).getId();

        // Reference base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("stories");

        if (!showViewCounter){
            storyViewCounterBox.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        // Ecouteur de mouvements
        OnSwipeTouchListener onImgSwipeTouchListener = new OnSwipeTouchListener(this){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    storiesProgressView.pause();
                    if (storiesProgressView.getVisibility() == View.VISIBLE){

                        storiesProgressView.setVisibility(View.GONE);
                        storiesProgressView.startAnimation(getFadeOutAnimation(StoriesViewerSingleSourceActivity.this));

                        toolbar.setVisibility(View.GONE);
                        toolbar.startAnimation(getFadeOutAnimation(StoriesViewerSingleSourceActivity.this));

                        storyViewCounterBox.setVisibility(View.GONE);
                        storyViewCounterBox.startAnimation(getFadeOutAnimation(StoriesViewerSingleSourceActivity.this));
                    }

                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    storiesProgressView.resume();
                    if (storiesProgressView.getVisibility() != View.VISIBLE){

                        storiesProgressView.setVisibility(View.VISIBLE);
                        storiesProgressView.startAnimation(getFadeInAnimation(StoriesViewerSingleSourceActivity.this));

                        toolbar.setVisibility(View.VISIBLE);
                        toolbar.startAnimation(getFadeInAnimation(StoriesViewerSingleSourceActivity.this));

                        if (showViewCounter) {
                            storyViewCounterBox.setVisibility(View.VISIBLE);
                            storyViewCounterBox.startAnimation(getFadeInAnimation(StoriesViewerSingleSourceActivity.this));
                        }
                    }

                }

                return super.onTouch(view, motionEvent);
            }

            @Override
            public void onSwipeLeft() {

                storiesProgressView.skip();

                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {

                if (mode == SINGLE){
                    storiesProgressView.skip();
                }else {
                    storiesProgressView.reverse();
                }

                super.onSwipeRight();
            }

        };

        // Fixation de l'écouteur
        storyImg.setOnTouchListener(onImgSwipeTouchListener);

        // Barre de progression en avant plan
        storiesProgressView.bringToFront();

        // Ecouteur de cycle de la barre de progression
        storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {

                if (mode == MULTI) {

                    position += 1;

                    storyImg.setVisibility(View.GONE);
                    storyImg.startAnimation(getFadeOutAnimation(StoriesViewerSingleSourceActivity.this));

                    loadImageOnSwipe(stories.get(position).getUrl());

                    storyImg.setVisibility(View.VISIBLE);
                    storyImg.startAnimation(getFadeInAnimation(StoriesViewerSingleSourceActivity.this));

                }
            }

            @Override
            public void onPrev() {

                if (mode == MULTI) {

                    if (position - 1 < 0) return;

                    position -= 1;

                    storyImg.setVisibility(View.GONE);
                    storyImg.startAnimation(getFadeOutAnimation(StoriesViewerSingleSourceActivity.this));

                    loadImageOnSwipe(stories.get(position).getUrl());

                    storyImg.setVisibility(View.VISIBLE);
                    storyImg.startAnimation(getFadeInAnimation(StoriesViewerSingleSourceActivity.this));

                }

            }

            @Override
            public void onComplete() {

                finish();
                overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

            }
        });

        // Chargement des stories
        getStories();
    }

    /**
     * Chargement des stories de la vitrine indiquée
     */
    private void getStories() {

        loadingDialog.show();

        databaseReference
                .orderByChild("source")
                .equalTo(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            loadingDialog.dismiss();

                            finish();
                            overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
                            return;
                        }

                        stories.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            stories.add(snapshot.getValue(Story.class));
                        }

                        storiesProgressView.setStoriesCount(mode == SINGLE ? 1 : stories.size());
                        storiesProgressView.setStoryDuration(10000L);

                        storyAuthorName.setText(showcaseName);
                        if (showcaseLogo.equals(DEFAULT)){

                            storyAuthorImg.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcaseName), Color.parseColor(showcaseSecondaryColor)));

                        }else{

                            Glide
                                    .with(StoriesViewerSingleSourceActivity.this)
                                    .load(showcaseLogo)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(storyAuthorImg);
                        }
                        storyPublishDate.setText(Timing.getInstance(StoriesViewerSingleSourceActivity.this, stories.get(position).getDate()).getTimePeriod());
                        storyViewCounter.setText(String.valueOf(stories.get(position).getViews()));

                        View.OnClickListener onCounterClickListener = v -> {

                            startActivity(new Intent(StoriesViewerSingleSourceActivity.this, StoriesViewerListActivity.class)
                                    .putExtra("storyId", stories.get(position).getId())
                                    .putExtra("showcasePrimaryColor", showcasePrimaryColor));
                            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                        };

                        storyViewCounter.setOnClickListener(onCounterClickListener);
                        storyViewCounterBox.getChildAt(0).setOnClickListener(onCounterClickListener);

                        GlideApp.with(StoriesViewerSingleSourceActivity.this)
                                .asBitmap()
                                .load(stories.get(position).getUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            startPostponedEnterTransition();
                                        }
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            startPostponedEnterTransition();
                                        }
                                        if (resource != null) {
                                            Palette p = Palette.from(resource).generate();
                                            // Use generated instance
                                            mPaletteColor = p.getMutedColor(ContextCompat.getColor(StoriesViewerSingleSourceActivity.this, R.color.black));

                                            customizeComponentColor();
                                        }

                                        addViewer();

                                        loadingDialog.dismiss();

                                        storiesProgressView.startStories(mode == SINGLE ? 0 : position);

                                        return false;
                                    }
                                })
                                .into(storyImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * Chargement des images au swipe
     * @param source / source de l'image
     */
    private void loadImageOnSwipe(String source){

        storyPublishDate.setText(Timing.getInstance(StoriesViewerSingleSourceActivity.this, stories.get(position).getDate()).getTimePeriod());
        storyViewCounter.setText(String.valueOf(stories.get(position).getViews()));

        GlideApp.with(this)
                .asBitmap()
                .load(source)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPostponedEnterTransition();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPostponedEnterTransition();
                        }
                        if (resource != null) {
                            Palette p = Palette.from(resource).generate();
                            // Use generated instance
                            mPaletteColor = p.getMutedColor(ContextCompat.getColor(StoriesViewerSingleSourceActivity.this, R.color.black));

                            customizeComponentColor();
                        }

                        addViewer();

                        return false;
                    }
                })
                .into(storyImg);

    }

    /**
     * Personnalisation des couleurs
     */
    private void customizeComponentColor() {

        // Adaptation de la barre de status aux versions supérieures à LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(mPaletteColor);
        }

        // Couleur d'arrière-plan
        rootView.setBackgroundColor(mPaletteColor);
    }

    /**
     * Met à jour la base de viewers
     */
    private void addViewer(){
        List<String> viewers = stories.get(position).getViewers();
        if (viewers != null) {

            if (!viewers.contains(userId)) { viewers.add(userId); }

        }else {
            viewers = new ArrayList<>();
            viewers.add(userId);
        }

        if (!stories.get(position).getViewers().equals(viewers)){

            databaseReference.child(stories.get(position).getId()).child("viewers").setValue(viewers);
            databaseReference.child(stories.get(position).getId()).child("views").setValue(viewers.size() - stories.get(position).getInitViews());

        }
    }

    @Override
    protected void onDestroy() {

        storiesProgressView.destroy();
        super.onDestroy();

    }

}
