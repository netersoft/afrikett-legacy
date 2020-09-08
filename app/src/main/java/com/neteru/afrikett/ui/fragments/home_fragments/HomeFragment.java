package com.neteru.afrikett.ui.fragments.home_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.neteru.afrikett.core.utilities.OnSwipeTouchListener;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.others_activities.SuggestionsActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.posts.PostOverviewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private DatabaseReference databaseReference;
    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    private PullRefreshLayout refreshLayout;
    private Activity activity;
    private StateView stateView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if (getActivity() != null) { activity = getActivity(); }

        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        ConstraintLayout baseLayout = root.findViewById(R.id.fragment_home_base);
        refreshLayout = root.findViewById(R.id.refresh);
        stateView = root.findViewById(R.id.stateview);
        
        FloatingActionButton fab = activity.findViewById(R.id.fab);

        if (fab.getVisibility() != View.GONE) {
            fab.setVisibility(View.GONE);
            fab.setAnimation(getFadeOutAnimation(activity));
        }

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(activity){
            @Override
            public void onSwipeLeft() {

                startActivity(new Intent(activity, SuggestionsActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void onSwipeRight() { }
        };

        stateView.setOnStateButtonClicked(v -> {

            startActivity(new Intent(activity, SuggestionsActivity.class));
            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        baseLayout.setOnTouchListener(onSwipeTouchListener);
        refreshLayout.setOnRefreshListener(this::getPosts);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        // Configuration du cache du recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        adapter = new PostAdapter(activity, posts, R.layout.template_posts, PostAdapterSource.HOME, new PostAdapter.PostAdapterListener() {
            @Override
            public void toSingleImageViewer(String url, String legend) {

                startActivity(new Intent(activity, ImageViewActivity.class)
                        .putExtra(ImageViewActivity.LEGEND, legend)
                        .putExtra(ImageViewActivity.URL, url));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void toMultipleImageViewer(ArrayList<String> urls, int position, String legend) {

                startActivity(new Intent(activity, ImageViewActivity.class)
                        .putExtra(ImageViewActivity.LEGEND, legend)
                        .putExtra(ImageViewActivity.POSITION, position)
                        .putStringArrayListExtra(ImageViewActivity.URL_LIST, urls));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void toPostOverview(Post post, Boolean keyboard) {

                startActivity(new Intent(activity, PostOverviewActivity.class)
                        .putExtra("id", post.getId())
                        .putExtra("keyboard", keyboard)
                        .putExtra("admin", false));
                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void openChatBox(Post post) {

                startActivity(new Intent(activity, ChatBoxActivity.class)
                        .putExtra("targetId", post.getShowcaseId())
                        .putExtra("captureBack", false)
                        .putExtra("targetType", SHOWCASE));

                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

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

                startActivity(new Intent(activity, SubscriberOverviewActivity.class)
                        .putExtra("showcaseId", showcaseId));

                activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }
        });

        recyclerView.setAdapter(adapter);

        getPosts();

        return root;
    }

    private void getPosts() {

        stateView.displayLoadingState();

        databaseReference
                .child("posts")
                .orderByChild("postDate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            changeHomeViewState(false);
                            return;
                        }

                        posts.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            posts.add(snapshot.getValue(Post.class));
                        }

                        changeHomeViewState(!posts.isEmpty());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        changeHomeViewState(false);
                    }
                });

    }

    private void changeHomeViewState(boolean state){
        if (state){
            stateView.hideStates();
        }else {
            stateView.displayState("welcome_home");
        }

        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
