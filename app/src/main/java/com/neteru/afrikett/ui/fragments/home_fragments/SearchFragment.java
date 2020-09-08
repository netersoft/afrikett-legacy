package com.neteru.afrikett.ui.fragments.home_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.FragmentStoriesAdapter;
import com.neteru.afrikett.core.adapters.RandomPostsAdapter;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Story;
import com.neteru.afrikett.core.utilities.GridItemDecoration;
import com.neteru.afrikett.ui.activities.others_activities.SearchActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.posts.PostOverviewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.stories.StoriesViewerMultiSourceActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private Activity activity;
    private RecyclerView posts_recycler;
    private RecyclerView stories_recycler;
    private DatabaseReference databaseReference;
    private List<Post> posts = new ArrayList<>();
    private List<Story> stories = new ArrayList<>();
    private RandomPostsAdapter randomPostsAdapter;
    private FragmentStoriesAdapter fragmentStoriesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        if (getActivity() != null) { activity = getActivity(); }

        FloatingActionButton fab = activity.findViewById(R.id.fab);

        if (fab.getVisibility() != View.GONE) {
            fab.setVisibility(View.GONE);
            fab.setAnimation(getFadeOutAnimation(getContext()));
        }

        posts_recycler = root.findViewById(R.id.posts_recycler);
        stories_recycler = root.findViewById(R.id.stories_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        stories_recycler.setItemAnimator(new DefaultItemAnimator());
        stories_recycler.setLayoutManager(linearLayoutManager);
        stories_recycler.setNestedScrollingEnabled(false);

        StaggeredGridLayoutManager sGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        posts_recycler.addItemDecoration(new GridItemDecoration(10, 3));
        posts_recycler.setItemAnimator(new DefaultItemAnimator());
        posts_recycler.setLayoutManager(sGridLayoutManager);
        posts_recycler.setNestedScrollingEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        getStories();
        getPosts();

        return root;
    }

    private void getPosts() {

        databaseReference
                .child("posts")
                .orderByChild("notation")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) return;

                        posts.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            posts.add(snapshot.getValue(Post.class));
                        }

                        Collections.reverse(posts);

                        randomPostsAdapter = new RandomPostsAdapter(activity, R.layout.template_random_post, posts, (post, keyboard) -> {

                            startActivity(new Intent(activity, PostOverviewActivity.class)
                                    .putExtra("id", post.getId())
                                    .putExtra("keyboard", keyboard)
                                    .putExtra("admin", false));
                            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                        });

                        posts_recycler.setAdapter(randomPostsAdapter);
                        posts_recycler.setHasFixedSize(true);

                        randomPostsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getStories() {

        databaseReference
                .child("stories")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {

                            stories_recycler.setVisibility(View.GONE);
                            stories_recycler.startAnimation(getFadeOutAnimation(activity));
                            return;

                        }

                        stories.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            stories.add(snapshot.getValue(Story.class));
                        }

                        Collections.shuffle(stories);

                        List<Story> selectedStories = new ArrayList<>();

                        for (Story story: stories){

                            boolean addStory = true;
                            for (Story s: selectedStories){
                                if (story.getSource().equals(s.getSource())){
                                    addStory = false;
                                }
                            }
                            if (addStory){ selectedStories.add(story); }
                            if (selectedStories.size() > 13){ break; }

                        }

                        fragmentStoriesAdapter = new FragmentStoriesAdapter(activity, selectedStories, (position, stories) -> {

                            startActivity(new Intent(activity, StoriesViewerMultiSourceActivity.class)
                                              .putExtra("position", position)
                                              .putExtra("stories", (Serializable) stories));
                            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                        });

                        stories_recycler.setAdapter(fragmentStoriesAdapter);
                        stories_recycler.setHasFixedSize(true);

                        fragmentStoriesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        activity.getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        // Configuration du SearchView
        SearchView search = (SearchView) mSearch.getActionView();
        search.setQueryHint(getString(R.string.search_hint));
        search.setIconified(false);
        search.setIconifiedByDefault(true);
        search.clearFocus();
        search.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
        search.setSubmitButtonEnabled(false);

        // Customisation de l'EditText
        SearchView.SearchAutoComplete searchAutoComplete = search.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.dimgray));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.black));

        searchAutoComplete.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){

                startActivity(new Intent(activity, SearchActivity.class));
                activity.overridePendingTransition(0, 0);

            }
            return true;
        });

        // Customisation du bouton de fermeture
        final ImageView searchCloseButton = search.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_clear_blue_24dp));
        searchCloseButton.setEnabled(false);

        // Customisation de l'icône de recherche
        ImageView searchButton = search.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_search_blue_24dp));

        // Désactivation du bouton de fermeture
        search.setOnCloseListener(() -> true);

        // Observateur du champ de recherche
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
}
