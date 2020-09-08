package com.neteru.afrikett.ui.activities.others_activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.PostAdapter;
import com.neteru.afrikett.core.enums.PostAdapterSource;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.posts.PostOverviewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.PRODUCT_AND_SERVICE;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;

public class FavoritesActivity extends AfrikettBaseActivity {
    private List<Post> posts = new ArrayList<>();
    private DatabaseReference databaseReference;
    private PullRefreshLayout refreshLayout;
    private PostAdapter adapter;
    private StateView stateView;
    private String userId;
    private Boolean cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        cart = getIntent().getBooleanExtra("cart", false);

        userId = getLocalUserData(this).getId();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        refreshLayout = findViewById(R.id.refresh);
        stateView = findViewById(R.id.stateview);

        refreshLayout.setOnRefreshListener(this::getFavorites);

        adapter = new PostAdapter(this, posts, R.layout.template_posts, PostAdapterSource.FAVORITES, new PostAdapter.PostAdapterListener() {
            @Override
            public void toSingleImageViewer(String url, String legend) {

                startActivity(new Intent(FavoritesActivity.this, ImageViewActivity.class)
                        .putExtra(ImageViewActivity.LEGEND, legend)
                        .putExtra(ImageViewActivity.URL, url));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void toMultipleImageViewer(ArrayList<String> urls, int position, String legend) {

                startActivity(new Intent(FavoritesActivity.this, ImageViewActivity.class)
                        .putExtra(ImageViewActivity.LEGEND, legend)
                        .putExtra(ImageViewActivity.POSITION, position)
                        .putStringArrayListExtra(ImageViewActivity.URL_LIST, urls));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void toPostOverview(Post post, Boolean keyboard) {

                startActivity(new Intent(FavoritesActivity.this, PostOverviewActivity.class)
                        .putExtra("id", post.getId())
                        .putExtra("keyboard", keyboard)
                        .putExtra("admin", false));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void openChatBox(Post post) {

                startActivity(new Intent(FavoritesActivity.this, ChatBoxActivity.class)
                        .putExtra("targetId", post.getShowcaseId())
                        .putExtra("captureBack", false)
                        .putExtra("targetType", SHOWCASE));

                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void deletePost(Post post) {

                databaseReference
                        .child("posts")
                        .child(post.getId())
                        .setValue(null);

            }

            @Override
            public void toShowcaseOverview(String showcaseId) {

                startActivity(new Intent(FavoritesActivity.this, SubscriberOverviewActivity.class)
                        .putExtra("showcaseId", showcaseId));

                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        getFavorites();

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(cart ? getString(R.string.added_title) : getString(R.string.saved_title));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skyblue));
        }
    }

    private void getFavorites() {

        if (!Connectivity.getInstance(this).isOnline()){

            stateView.displayState("error_connection");
            Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        stateView.displayLoadingState();

        databaseReference
                .child("favorites")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){

                            changeFavoritesViewState(false);
                            return;
                        }

                        List<String> postIdList = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            postIdList.add(snapshot.getValue(String.class));
                        }

                        if (postIdList.isEmpty()) {

                            changeFavoritesViewState(false);
                            return;
                        }

                        databaseReference
                                .child("posts")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getValue() == null) {

                                            changeFavoritesViewState(false);
                                            return;
                                        }

                                        posts.clear();

                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                                            Post post = snapshot.getValue(Post.class);

                                            if (post == null) return;

                                            if (postIdList.contains(post.getId())){

                                                if (cart){
                                                    if (post.getType() == PRODUCT_AND_SERVICE && post.getProductOrServicePrice() != null){
                                                        posts.add(post);
                                                    }
                                                }else {
                                                    if (post.getType() != PRODUCT_AND_SERVICE ||
                                                            (post.getType() == PRODUCT_AND_SERVICE && post.getProductOrServicePrice() == null)){
                                                        posts.add(post);
                                                    }
                                                }
                                            }
                                        }

                                        if (posts.isEmpty()){

                                            changeFavoritesViewState(false);
                                            return;
                                        }

                                        changeFavoritesViewState(true);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        changeFavoritesViewState(false);
                    }
                });

    }

    private void changeFavoritesViewState(boolean state){
        if (state){
            stateView.hideStates();
        }else {
            stateView.displayState(cart ? "no_addition" : "no_favorites");
        }

        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

}
