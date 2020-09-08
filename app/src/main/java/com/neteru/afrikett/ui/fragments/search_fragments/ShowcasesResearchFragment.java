package com.neteru.afrikett.ui.fragments.search_fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.SubscriptionAdapter;
import com.neteru.afrikett.core.enums.PostType;
import com.neteru.afrikett.core.interfaces.SearchDataListener;
import com.neteru.afrikett.core.interfaces.SearchQueryListener;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.ui.activities.others_activities.SearchActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowcasesResearchFragment extends Fragment implements SearchQueryListener, SearchDataListener {
    private List<Showcase> showcases = new ArrayList<>();
    private List<Showcase> searchShowcases = new ArrayList<>();
    private final static String TAG = "SHOWCASE_RESEARCH";
    private Activity activity;
    private SubscriptionAdapter adapter;
    private StateView stateView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_showcases_research, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        stateView = root.findViewById(R.id.stateview);

        if (getActivity() != null) { activity = getActivity(); }

        SearchActivity searchActivity = (SearchActivity) activity;

        searchActivity.setSearchQueryListener(this);
        searchActivity.setSearchDataListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        adapter = new SubscriptionAdapter(activity, searchShowcases, R.layout.template_subscription, showcase -> {

            startActivity(new Intent(activity, SubscriberOverviewActivity.class)
                    .putExtra("showcaseId", showcase.getId()));

            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onQueryTextChange(@NotNull String newText) {
        Log.d(TAG, TAG+":"+newText);

        if (showcases == null || showcases.isEmpty()) return;

        searchShowcases.clear();

        for (Showcase showcase: showcases){
            if (showcase.getName().contains(newText) || showcase.getDescription().contains(newText)){
                searchShowcases.add(showcase);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onQueryTextSubmit(@NotNull String query) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onShowcasesDataReady(@NotNull List<? extends Showcase> showcases) {
        this.showcases = (List<Showcase>) showcases;
    }

    @Override
    public void onPostsDataReady(@NotNull List<? extends Post> posts, @NotNull PostType postType) { }
}
