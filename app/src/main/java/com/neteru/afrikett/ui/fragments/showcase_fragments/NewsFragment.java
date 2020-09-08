package com.neteru.afrikett.ui.fragments.showcase_fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.PostAdapter;
import com.neteru.afrikett.core.enums.PostAdapterSource;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.posts.PostOverviewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.NEWS;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private DatabaseReference databaseReference;
    private List<Post> posts = new ArrayList<>();
    private String showcaseId;
    private SwipeRefreshLayout refreshLayout;
    private TextView noPostView;
    private Activity activity;
    private Boolean admin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_overview_posts, container, false);

        if (getActivity() != null){
            activity = getActivity();
        }

        if (getArguments() != null){
            admin = getArguments().getBoolean("admin");
            showcaseId = getArguments().getString("showcaseId");
        }

        recyclerView = root.findViewById(R.id.recycler);
        refreshLayout = root.findViewById(R.id.refresh);
        noPostView = root.findViewById(R.id.no_post);

        refreshLayout.setOnRefreshListener(this::loadNewsPosts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        // Configuration du cache du recyclerView
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        adapter = new PostAdapter(getContext(), posts, R.layout.template_posts, admin ? PostAdapterSource.ADMIN : PostAdapterSource.SUBSCRIBER, new PostAdapter.PostAdapterListener() {
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
                        .putExtra("admin", admin));
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
            public void toShowcaseOverview(String showcaseId) { }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        loadShowcaseData();

        loadNewsPosts();

        return root;
    }

    private void loadShowcaseData(){

        databaseReference
                .child("showcases")
                .child(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){ return; }

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase != null) {
                            refreshLayout.setColorSchemeColors(Color.parseColor(showcase.getSecondaryColor()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void loadNewsPosts() {

        databaseReference
                .child("posts")
                .orderByChild("showcaseId")
                .equalTo(showcaseId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            showNoPostView(true);

                            adapter.notifyDataSetChanged();

                            if (refreshLayout.isRefreshing()){ refreshLayout.setRefreshing(false); }
                            return;
                        }

                        posts.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                            Post post = snapshot.getValue(Post.class);
                            if (post != null && post.getType() == NEWS){
                                posts.add(post);
                            }

                        }

                        showNoPostView(posts.isEmpty());

                        adapter.notifyDataSetChanged();

                        if (refreshLayout.isRefreshing()){ refreshLayout.setRefreshing(false); }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (refreshLayout.isRefreshing()){ refreshLayout.setRefreshing(false); }
                    }
                });

    }

    private void showNoPostView(boolean show){
        if (show){

            recyclerView.setVisibility(View.GONE);
            noPostView.setVisibility(View.VISIBLE);

        }else {

            recyclerView.setVisibility(View.VISIBLE);
            noPostView.setVisibility(View.GONE);

        }
    }

    public static NewsFragment newInstance(String showcaseId, Boolean admin){
        Bundle args = new Bundle();
        args.putBoolean("admin", admin);
        args.putString("showcaseId", showcaseId);
        NewsFragment newsFragment = new NewsFragment();
        newsFragment.setArguments(args);

        return newsFragment;
    }
}
