package com.neteru.afrikett.ui.activities.showcase_activities.stories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

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
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.Story;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.GlideApp;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.utilities.OnSwipeTouchListener;
import com.neteru.afrikett.core.utilities.Timing;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;

public class StoriesViewerMultiSourceActivity extends AfrikettBaseActivity {

    private TouchImageView storyImg;
    private StoriesProgressView storiesProgressView;
    private DatabaseReference databaseReference;
    private int position;
    private int mPaletteColor;
    private List<Story> stories = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private ConstraintLayout rootView;
    private Window window;
    private Toolbar toolbar;
    private ImageView storyAuthorImg;
    private TextView storyAuthorName;
    private TextView storyPublishDate;
    private String userId;
    private String showcaseName;

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_viewer_multi_source);

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

        position = getIntent().getIntExtra("position", 0);
        stories = (List<Story>) getIntent().getSerializableExtra("stories");

        loadingDialog = new LoadingDialog(this);

        window = getWindow();

        userId = getLocalUserData(this).getId();

        // Reference base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

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
                        storiesProgressView.startAnimation(getFadeOutAnimation(StoriesViewerMultiSourceActivity.this));

                        toolbar.setVisibility(View.GONE);
                        toolbar.startAnimation(getFadeOutAnimation(StoriesViewerMultiSourceActivity.this));

                    }

                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    storiesProgressView.resume();
                    if (storiesProgressView.getVisibility() != View.VISIBLE){

                        storiesProgressView.setVisibility(View.VISIBLE);
                        storiesProgressView.startAnimation(getFadeInAnimation(StoriesViewerMultiSourceActivity.this));

                        toolbar.setVisibility(View.VISIBLE);
                        toolbar.startAnimation(getFadeInAnimation(StoriesViewerMultiSourceActivity.this));
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

                storiesProgressView.reverse();

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

                position += 1;

                storyImg.setVisibility(View.GONE);
                storyImg.startAnimation(getFadeOutAnimation(StoriesViewerMultiSourceActivity.this));

                loadImageOnSwipe(stories.get(position).getUrl());

                storyImg.setVisibility(View.VISIBLE);
                storyImg.startAnimation(getFadeInAnimation(StoriesViewerMultiSourceActivity.this));

            }

            @Override
            public void onPrev() {

                if (position - 1 < 0) return;

                position -= 1;

                storyImg.setVisibility(View.GONE);
                storyImg.startAnimation(getFadeOutAnimation(StoriesViewerMultiSourceActivity.this));

                loadImageOnSwipe(stories.get(position).getUrl());

                storyImg.setVisibility(View.VISIBLE);
                storyImg.startAnimation(getFadeInAnimation(StoriesViewerMultiSourceActivity.this));

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

        storiesProgressView.setStoriesCount(stories.size());
        storiesProgressView.setStoryDuration(10000L);

        loadShowcaseInfo(stories.get(position).getSource());

        storyPublishDate.setText(Timing.getInstance(StoriesViewerMultiSourceActivity.this, stories.get(position).getDate()).getTimePeriod());

        GlideApp.with(StoriesViewerMultiSourceActivity.this)
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
                            mPaletteColor = p.getMutedColor(ContextCompat.getColor(StoriesViewerMultiSourceActivity.this, R.color.black));

                            customizeComponentColor();
                        }

                        addViewer();

                        loadingDialog.dismiss();

                        storiesProgressView.startStories(position);

                        return false;
                    }
                })
                .into(storyImg);
    }

    /**
     * Chargement des images au swipe
     * @param source / source de l'image
     */
    private void loadImageOnSwipe(String source){

        loadShowcaseInfo(stories.get(position).getSource());

        storyPublishDate.setText(Timing.getInstance(StoriesViewerMultiSourceActivity.this, stories.get(position).getDate()).getTimePeriod());

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
                            mPaletteColor = p.getMutedColor(ContextCompat.getColor(StoriesViewerMultiSourceActivity.this, R.color.black));

                            customizeComponentColor();
                        }

                        addViewer();

                        return false;
                    }
                })
                .into(storyImg);

    }

    /**
     * Récupération des données de la vitrine
     * @param showcaseId / Identifiant de la vitrine
     */
    private void loadShowcaseInfo(String showcaseId){

        databaseReference
                .child("showcases")
                .child(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null) return;

                        showcaseName = showcase.getName();

                        storyAuthorName.setText(showcaseName);
                        if (showcase.getLogo().equals(DEFAULT)){

                            storyAuthorImg.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcaseName), Color.parseColor(showcase.getSecondaryColor())));

                        }else{

                            Glide
                                    .with(StoriesViewerMultiSourceActivity.this)
                                    .load(showcase.getLogo())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(storyAuthorImg);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

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

            databaseReference.child("stories").child(stories.get(position).getId()).child("viewers").setValue(viewers);
            databaseReference.child("stories").child(stories.get(position).getId()).child("views").setValue(viewers.size() - stories.get(position).getInitViews());

        }
    }

    @Override
    protected void onDestroy() {

        storiesProgressView.destroy();
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multi_source_stories_viewer_menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_view_showcase:

                startActivity(new Intent(this, SubscriberOverviewActivity.class)
                        .putExtra("showcaseId", stories.get(position).getSource()));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                break;

            case R.id.action_send_message:

                contactShowcase();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void contactShowcase() {

        // Génération du noeud de discussion
        final List<String> list = new ArrayList<>();
        list.add(userId);
        list.add(stories.get(position).getSource());
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

                                            if (list.get(0).equals(stories.get(position).getSource())){
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

        startActivity(new Intent(this, ChatBoxActivity.class)
                .putExtra("targetId", stories.get(position).getSource())
                .putExtra("captureBack", false)
                .putExtra("targetType", SHOWCASE));

        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

    }
}
