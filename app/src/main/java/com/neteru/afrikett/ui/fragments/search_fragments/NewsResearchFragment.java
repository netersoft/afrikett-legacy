package com.neteru.afrikett.ui.fragments.search_fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.PostAdapter;
import com.neteru.afrikett.core.enums.PostAdapterSource;
import com.neteru.afrikett.core.enums.PostType;
import com.neteru.afrikett.core.interfaces.SearchDataListener;
import com.neteru.afrikett.core.interfaces.SearchQueryListener;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.others_activities.SearchActivity;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.posts.PostOverviewActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsResearchFragment extends Fragment  implements SearchQueryListener, SearchDataListener {
    private DatabaseReference databaseReference;
    private List<Post> news = new ArrayList<>();
    private List<Post> searchNews = new ArrayList<>();
    private final static String TAG = "NEWS_RESEARCH";
    private Activity activity;
    private PostAdapter adapter;
    private StateView stateView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_news_research, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        stateView = root.findViewById(R.id.stateview);

        if (getActivity() != null) { activity = getActivity(); }

        SearchActivity searchActivity = (SearchActivity) activity;

        searchActivity.setSearchQueryListener(this);
        searchActivity.setSearchDataListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        databaseReference = searchActivity.getDatabaseReference();

        adapter = new PostAdapter(activity, searchNews, R.layout.template_posts, PostAdapterSource.RESEARCH, new PostAdapter.PostAdapterListener() {
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onQueryTextChange(@NotNull String newText) {
        Log.d(TAG, TAG+":"+newText);

        if (news == null || news.isEmpty()) return;

        searchNews.clear();

        for (Post post: news){
            if (post.getNewsTitle().contains(newText) || post.getNewsContent().contains(newText)){
                searchNews.add(post);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onQueryTextSubmit(@NotNull String query) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onPostsDataReady(@NotNull List<? extends Post> posts, @NotNull PostType postType) {
        if (postType == PostType.NEWS){
            news = (List<Post>) posts;
        }
    }

    @Override
    public void onShowcasesDataReady(@NotNull List<? extends Showcase> showcases) { }
}
