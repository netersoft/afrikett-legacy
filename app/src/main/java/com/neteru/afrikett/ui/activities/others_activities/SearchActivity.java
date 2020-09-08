package com.neteru.afrikett.ui.activities.others_activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.enums.PostType;
import com.neteru.afrikett.core.interfaces.SearchDataListener;
import com.neteru.afrikett.core.interfaces.SearchQueryListener;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.ViewPagerAdapter;
import com.neteru.afrikett.ui.fragments.search_fragments.EventsResearchFragment;
import com.neteru.afrikett.ui.fragments.search_fragments.NewsResearchFragment;
import com.neteru.afrikett.ui.fragments.search_fragments.ProductsAndServicesResearchFragment;
import com.neteru.afrikett.ui.fragments.search_fragments.ShowcasesResearchFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.EVENT;
import static com.neteru.afrikett.core.utilities.Constants.NEWS;
import static com.neteru.afrikett.core.utilities.Constants.PRODUCT_AND_SERVICE;

public class SearchActivity extends AppCompatActivity {
    private SearchQueryListener searchQueryListener;
    private SearchDataListener searchDataListener;
    private DatabaseReference databaseReference;
    private SearchView search;
    private List<Post> news = new ArrayList<>();
    private List<Post> events = new ArrayList<>();
    private List<Post> productsAndServices = new ArrayList<>();
    private List<Showcase> showcases = new ArrayList<>();
    private String globalNewText = EMPTY;
    private String globalQuery = EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        setTabLayoutIcons(tabLayout);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:

                        setSearchHint(getString(R.string.search_product_or_service));
                        searchDataListener.onPostsDataReady(productsAndServices, PostType.PRODUCT_AND_SERVICE);
                        searchQueryListener.onQueryTextChange(globalNewText);
                        searchQueryListener.onQueryTextSubmit(globalQuery);
                        break;

                    case 1:

                        setSearchHint(getString(R.string.search_showcase));
                        searchDataListener.onShowcasesDataReady(showcases);
                        searchQueryListener.onQueryTextChange(globalNewText);
                        searchQueryListener.onQueryTextSubmit(globalQuery);
                        break;

                    case 2:

                        setSearchHint(getString(R.string.search_event));
                        searchDataListener.onPostsDataReady(events, PostType.EVENT);
                        searchQueryListener.onQueryTextChange(globalNewText);
                        searchQueryListener.onQueryTextSubmit(globalQuery);
                        break;

                    case 3:

                        setSearchHint(getString(R.string.search_news));
                        searchDataListener.onPostsDataReady(news, PostType.NEWS);
                        searchQueryListener.onQueryTextChange(globalNewText);
                        searchQueryListener.onQueryTextSubmit(globalQuery);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (getSupportActionBar() != null){
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setElevation(0f);

        }

        getShowcases();

        getPosts();

    }

    @SuppressWarnings("ConstantConditions")
    private void setTabLayoutIcons(TabLayout tabLayout) {

        if (tabLayout.getTabCount() < 4) return;

        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_shopping_basket_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_store_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_event_white_24dp);
        tabLayout.getTabAt(3).setIcon(R.mipmap.ic_list_white_24dp);

    }

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProductsAndServicesResearchFragment(), EMPTY);
        adapter.addFragment(new ShowcasesResearchFragment(), EMPTY);
        adapter.addFragment(new EventsResearchFragment(), EMPTY);
        adapter.addFragment(new NewsResearchFragment(), EMPTY);

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        // Configuration du SearchView
        search = (SearchView) mSearch.getActionView();
        search.setIconified(false);
        search.setIconifiedByDefault(true);
        search.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
        setSearchHint(getString(R.string.search_product_or_service));

        // Customisation de la couleur du hint
        SearchView.SearchAutoComplete searchAutoComplete = search.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.dimgray));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.black));

        // Customisation du bouton de fermeture
        final ImageView searchCloseButton = search.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_blue_24dp));

        // Customisation de l'icône de recherche
        ImageView searchButton = search.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search_blue_24dp));

        // Désactivation du bouton de fermeture
        search.setOnCloseListener(() -> {

            if (search.getQuery().toString().isEmpty()){

                finish();

            }

            return true;
        });

        // Observateur du champ de recherche
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                globalQuery = query;

                if (searchQueryListener != null){
                    searchQueryListener.onQueryTextSubmit(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                globalNewText = newText;

                if (searchQueryListener != null){
                    searchQueryListener.onQueryTextChange(newText);
                }
                return false;
            }
        });

        return true;
    }

    public void setSearchQueryListener(SearchQueryListener listener){
        searchQueryListener = listener;
    }

    public void setSearchDataListener(SearchDataListener listener){
        searchDataListener = listener;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    private void setSearchHint(String hint){
        search.setQueryHint(hint);
    }

    private void getShowcases(){

        databaseReference
                .child("showcases")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        showcases.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            showcases.add(snapshot.getValue(Showcase.class));
                        }

                        searchDataListener.onShowcasesDataReady(showcases);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getPosts(){

        databaseReference
                .child("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        news.clear();
                        events.clear();
                        productsAndServices.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Post post = snapshot.getValue(Post.class);

                            if (post == null) return;

                            switch (post.getType()){
                                case NEWS:
                                    news.add(post);
                                    break;

                                case EVENT:
                                    events.add(post);
                                    break;

                                case PRODUCT_AND_SERVICE:
                                    productsAndServices.add(post);
                                    break;
                            }
                        }

                        searchDataListener.onPostsDataReady(news, PostType.NEWS);
                        searchDataListener.onPostsDataReady(events, PostType.EVENT);
                        searchDataListener.onPostsDataReady(productsAndServices, PostType.PRODUCT_AND_SERVICE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        overridePendingTransition(0, 0);

        return true;
    }
}
