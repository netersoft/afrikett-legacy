package com.neteru.afrikett.ui.activities.showcase_activities.overview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.OverviewStoriesAdapter;
import com.neteru.afrikett.core.libs.EmojiAndSocialTextView.EmojiAndSocialTextView;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.Report;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.Story;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.ViewPagerAdapter;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.stories.StoriesViewerSingleSourceActivity;
import com.neteru.afrikett.ui.fragments.showcase_fragments.AllFragment;
import com.neteru.afrikett.ui.fragments.showcase_fragments.EventsFragment;
import com.neteru.afrikett.ui.fragments.showcase_fragments.NewsFragment;
import com.neteru.afrikett.ui.fragments.showcase_fragments.ProductsAndServicesFragment;
import com.schibstedspain.leku.LocationPickerActivity;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.compactNumber;
import static com.neteru.afrikett.core.utilities.AppUtilities.generateKey;
import static com.neteru.afrikett.core.utilities.AppUtilities.getBoldString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setTextViewDrawableColor;
import static com.neteru.afrikett.core.utilities.Constants.AGRO;
import static com.neteru.afrikett.core.utilities.Constants.CATERING;
import static com.neteru.afrikett.core.utilities.Constants.COMMUNICATION;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EDUCATION_AND_TRAINING;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.ENTERTAINMENT;
import static com.neteru.afrikett.core.utilities.Constants.FASHION_AND_CLOTHING;
import static com.neteru.afrikett.core.utilities.Constants.FINANCE_AND_BANKING;
import static com.neteru.afrikett.core.utilities.Constants.HEALTH;
import static com.neteru.afrikett.core.utilities.Constants.HOTEL_BUSINESS;
import static com.neteru.afrikett.core.utilities.Constants.INFORMATION_SCIENCE;
import static com.neteru.afrikett.core.utilities.Constants.MULTI;
import static com.neteru.afrikett.core.utilities.Constants.OTHER;
import static com.neteru.afrikett.core.utilities.Constants.PROFESSIONAL_SERVICES;
import static com.neteru.afrikett.core.utilities.Constants.PUBLIC_SERVICES;
import static com.neteru.afrikett.core.utilities.Constants.RETAIL_SALE;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE_REPORT;
import static com.neteru.afrikett.core.utilities.Constants.TRADE_AND_DISTRIBUTION;
import static com.neteru.afrikett.core.utilities.Constants.TRANSPORTS_AND_LOGISTICS;

public class SubscriberOverviewActivity extends AfrikettBaseActivity {

    private String userId;
    private String showcaseId;
    private String showcaseName;
    private String showcaseLogo;
    private String showcasePrimaryColor;
    private String showcaseSecondaryColor;
    private DatabaseReference databaseReference;
    private DatabaseReference subscriptionDbReference;
    private DatabaseReference userSubscriptionDbReference;
    private ImageView banner;
    private ImageView logo;
    private ImageView fieldIndicator;
    private TextView name;
    private TextView address;
    private TextView email;
    private TextView phone;
    private TextView website;
    private TextView openingHours;
    private TextView subscribers;
    private TextView posts;
    private TextView field;
    private EmojiAndSocialTextView description;
    private RecyclerView storiesRecycler;
    private List<Story> stories = new ArrayList<>();
    private OverviewStoriesAdapter overviewStoriesAdapter;
    private TabLayout tabLayout;
    private TextView toolbarTitle;
    private TextView toolbarSubTitle;
    private String nbSubscribersStr;
    private String postStr;
    private LinearLayout fieldLayout;
    private Button subscribe_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_overview);

        userId = getLocalUserData(this).getId();
        showcaseId = getIntent().getStringExtra("showcaseId");

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
        subscriptionDbReference = databaseReference.child("showcases").child(showcaseId);
        userSubscriptionDbReference = databaseReference.child("users").child(userId).child("nbSubscriptions");

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewPager viewPager = findViewById(R.id.view_pager);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        toolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tabLayout = findViewById(R.id.tabs);

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(EMPTY);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {

            if (Math.abs(verticalOffset)- appBarLayout1.getTotalScrollRange() == 0)
            {
                //  Collapsed
                String collapsedSubTitle = nbSubscribersStr+"\t\t"+postStr;

                toolbarTitle.setText(showcaseName);
                toolbarSubTitle.setText(collapsedSubTitle);

            }
            else
            {
                //Expanded
                toolbarTitle.setText(EMPTY);
                toolbarSubTitle.setText(EMPTY);

            }
        });

        name = findViewById(R.id.showcase_name);
        logo = findViewById(R.id.showcase_logo);
        banner = findViewById(R.id.showcase_banner);
        field = findViewById(R.id.showcase_field);
        description = findViewById(R.id.showcase_description);
        storiesRecycler = findViewById(R.id.showcase_stories);
        fieldLayout = findViewById(R.id.showcase_field_layout);
        fieldIndicator = findViewById(R.id.showcase_field_indicator);

        address = findViewById(R.id.showcase_address);
        posts = findViewById(R.id.showcase_posts);
        email = findViewById(R.id.showcase_email);
        phone = findViewById(R.id.showcase_phone);
        website = findViewById(R.id.showcase_website);
        subscribers = findViewById(R.id.showcase_subscribers);
        openingHours = findViewById(R.id.showcase_opening_hours);

        subscribe_but = findViewById(R.id.showcase_subscribe);

        getNumberOfPosts();

        loadShowcase();

        loadStories();

        subscribe_but.setOnClickListener(v -> subscription());

        findViewById(R.id.showcase_contact).setOnClickListener(v -> contact());

    }

    private void contact() {

        // Génération du noeud de discussion
        final List<String> list = new ArrayList<>();
        list.add(userId);
        list.add(showcaseId);
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

                                            if (list.get(0).equals(showcaseId)){
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

        startActivity(new Intent(SubscriberOverviewActivity.this, ChatBoxActivity.class)
                .putExtra("targetId", showcaseId)
                .putExtra("captureBack", false)
                .putExtra("targetType", SHOWCASE));

        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

    }

    private void subscription() {

        subscriptionDbReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) return;

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null) return;

                        final List<String> subscribersList = showcase.getSubscribers();

                        if (subscribersList != null) {

                            if (subscribersList.contains(userId)) {

                                new AlertDialog.Builder(SubscriberOverviewActivity.this)
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

        setSubscribeButLook(increaseValue);

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

    private void setSubscribeButLook(boolean subscribed){

        if (subscribed){

            subscribe_but.setBackground(getResources().getDrawable(R.drawable.gray_button_bg_filled));
            subscribe_but.setTextColor(getResources().getColor(R.color.white));
            subscribe_but.setText(getString(R.string.subscribed));

        }else {

            subscribe_but.setText(getString(R.string.subscribe));
            subscribe_but.setTextColor(getResources().getColor(R.color.dimgray));
            subscribe_but.setBackground(getResources().getDrawable(R.drawable.gray_button_bg_stroke));

        }

    }

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AllFragment.newInstance(showcaseId, false), getString(R.string.all));
        adapter.addFragment(NewsFragment.newInstance(showcaseId, false), getString(R.string.news));
        adapter.addFragment(ProductsAndServicesFragment.newInstance(showcaseId, false), getString(R.string.products_and_services));
        adapter.addFragment(EventsFragment.newInstance(showcaseId, false), getString(R.string.events));

        viewPager.setAdapter(adapter);
    }

    private void getNumberOfPosts(){

        databaseReference
                .child("posts")
                .orderByChild("showcaseId")
                .equalTo(showcaseId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){

                            postStr = "0\t\t"+getString(R.string.posts);
                            posts.setText(getBoldString(postStr, 0, 1));

                            return;
                        }
                        List<Post> postList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            postList.add(snapshot.getValue(Post.class));
                        }

                        postStr = compactNumber(postList.size())+"\t\t"+getString(R.string.posts);
                        posts.setText(getBoldString(postStr, 0, String.valueOf(compactNumber(postList.size())).length()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShowcase() {

        databaseReference
                .child("showcases")
                .child(showcaseId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){
                            return;
                        }

                        final Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null){
                            return;
                        }

                        nbSubscribersStr = compactNumber(showcase.getNb_subscribers())+"\t\t"+getString(R.string.subscribers);

                        List<String> subscribersList = showcase.getSubscribers();

                        setSubscribeButLook(subscribersList != null && subscribersList.contains(userId));

                        showcaseName = showcase.getName();
                        showcaseLogo = showcase.getLogo();
                        showcasePrimaryColor = showcase.getPrimaryColor();
                        showcaseSecondaryColor = showcase.getSecondaryColor();

                        name.setText(showcaseName);
                        description.setLinkText(showcase.getDescription());
                        subscribers.setText(getBoldString(nbSubscribersStr, 0, String.valueOf(compactNumber(showcase.getNb_subscribers())).length()));

                        if (showcaseLogo.equals(DEFAULT)){

                            logo.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcaseName), Color.parseColor(showcaseSecondaryColor)));

                        }else{

                            Glide
                                    .with(SubscriberOverviewActivity.this)
                                    .load(showcaseLogo)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(logo);
                        }

                        if (showcase.getBanner().equals(DEFAULT)){

                            banner.setBackgroundColor(Color.parseColor(showcasePrimaryColor));

                        }else{

                            Glide
                                    .with(SubscriberOverviewActivity.this)
                                    .load(showcase.getBanner())
                                    .into(banner);
                        }

                        if (showcase.getField() != null && showcase.getField() != OTHER){

                            fieldIndicator.setColorFilter(Color.parseColor(showcaseSecondaryColor));
                            String value = EMPTY;

                            switch (showcase.getField()) {
                                case AGRO:
                                    value = getString(R.string.agro);
                                    break;
                                case INFORMATION_SCIENCE:
                                    value = getString(R.string.information_science);
                                    break;
                                case FASHION_AND_CLOTHING:
                                    value = getString(R.string.fashion_and_clothing);
                                    break;
                                case COMMUNICATION:
                                    value = getString(R.string.communication);
                                    break;
                                case ENTERTAINMENT:
                                    value = getString(R.string.entertainment);
                                    break;
                                case EDUCATION_AND_TRAINING:
                                    value = getString(R.string.education_and_training);
                                    break;
                                case FINANCE_AND_BANKING:
                                    value = getString(R.string.finance_and_banking);
                                    break;
                                case TRANSPORTS_AND_LOGISTICS:
                                    value = getString(R.string.transport_and_logistics);
                                    break;
                                case TRADE_AND_DISTRIBUTION:
                                    value = getString(R.string.trade_and_distribution);
                                    break;
                                case PUBLIC_SERVICES:
                                    value = getString(R.string.public_services);
                                    break;
                                case HOTEL_BUSINESS:
                                    value = getString(R.string.hotel_business);
                                    break;
                                case HEALTH:
                                    value = getString(R.string.health);
                                    break;
                                case PROFESSIONAL_SERVICES:
                                    value = getString(R.string.professional_services);
                                    break;
                                case RETAIL_SALE:
                                    value = getString(R.string.retail_sale);
                                    break;
                                case CATERING:
                                    value = getString(R.string.catering);
                                    break;
                                case OTHER:
                                    value = getString(R.string.other);
                                    break;
                            }

                            field.setText(value);

                        }else {
                            fieldLayout.setVisibility(View.GONE);
                        }

                        if (showcase.getShowcaseContactDetails() != null){

                            if (showcase.getShowcaseContactDetails().getEmail() != null){
                                setTextViewDrawableColor(email, Color.parseColor(showcasePrimaryColor));
                                email.setText(showcase.getShowcaseContactDetails().getEmail());
                                email.setOnClickListener(v -> {

                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                    emailIntent.setData(Uri.parse("mailto:"+showcase.getShowcaseContactDetails().getEmail()));
                                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));

                                });
                            }else {
                                email.setVisibility(View.GONE);
                            }

                            if (showcase.getShowcaseContactDetails().getNumber() != null){
                                setTextViewDrawableColor(phone, Color.parseColor(showcasePrimaryColor));
                                phone.setText(showcase.getShowcaseContactDetails().getNumber());
                                phone.setOnClickListener(v -> {

                                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", showcase.getShowcaseContactDetails().getNumber(), null));
                                    startActivity(phoneIntent);

                                });
                            }else {
                                phone.setVisibility(View.GONE);
                            }

                            if (showcase.getShowcaseContactDetails().getWebsite() != null){
                                setTextViewDrawableColor(website, Color.parseColor(showcasePrimaryColor));
                                website.setText(showcase.getShowcaseContactDetails().getWebsite());
                                website.setOnClickListener(v -> {

                                    String url = showcase.getShowcaseContactDetails().getWebsite();
                                    if (!url.startsWith("http://") && !url.startsWith("https://")){
                                        url = "http://" + url;
                                    }

                                    new FinestWebView
                                            .Builder(SubscriberOverviewActivity.this)
                                            .statusBarColor(Color.parseColor(showcasePrimaryColor))
                                            .toolbarColor(Color.parseColor(showcasePrimaryColor))
                                            .showIconClose(true)
                                            .showIconMenu(true)
                                            .showSwipeRefreshLayout(true)
                                            .swipeRefreshColor(Color.parseColor(showcaseSecondaryColor))
                                            .showDivider(false)
                                            .dividerColor(Color.parseColor(showcaseSecondaryColor))
                                            .showProgressBar(true)
                                            .progressBarColor(Color.parseColor(showcaseSecondaryColor))
                                            .showUrl(true)
                                            .showMenuRefresh(true)
                                            .showMenuFind(false)
                                            .showMenuShareVia(true)
                                            .showMenuCopyLink(true)
                                            .showMenuOpenWith(true)
                                            .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                                            .stringResRefresh(R.string.refresh)
                                            .stringResShareVia(R.string.share_via)
                                            .stringResCopyLink(R.string.copy_link)
                                            .stringResOpenWith(R.string.open_with)
                                            .backPressToClose(true)
                                            .toolbarScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
                                            .gradientDivider(false)
                                            .webViewAppCacheEnabled(true)
                                            .webViewJavaScriptEnabled(true)
                                            .urlColorRes(R.color.whitesmoke)
                                            .titleColorRes(R.color.white)
                                            .iconDefaultColorRes(R.color.white)
                                            .show(url);

                                });
                            }else {
                                website.setVisibility(View.GONE);
                            }

                            if (showcase.getShowcaseContactDetails().getLocation() != null){
                                setTextViewDrawableColor(address, Color.parseColor(showcasePrimaryColor));
                                address.setText(showcase.getShowcaseContactDetails().getLocation().getAddress());
                                address.setOnClickListener(v -> startActivity(
                                        new LocationPickerActivity.Builder()
                                                .withLocation(showcase.getShowcaseContactDetails().getLocation().getLatitude(),
                                                        showcase.getShowcaseContactDetails().getLocation().getLongitude())
                                                .withGeolocApiKey(getString(R.string.api_key))
                                                .withSatelliteViewHidden()
                                                .withGoogleTimeZoneEnabled()
                                                .withVoiceSearchHidden()
                                                .build(getApplicationContext())));
                            }else {
                                address.setVisibility(View.GONE);
                            }

                        }

                        if (showcase.getOpeningHours() != null){
                            setTextViewDrawableColor(openingHours, Color.parseColor(showcasePrimaryColor));

                            openingHours.setText(showcase.getOpeningHours().split("<br>")[0]
                                    .replace("</h5>", EMPTY)
                                    .replace("<h5>", EMPTY));

                            openingHours.setOnClickListener(v -> {

                                @SuppressLint("InflateParams")
                                View openingHoursView = LayoutInflater.from(SubscriberOverviewActivity.this).inflate(R.layout.layout_opening_hours, null);

                                TextView openingHoursText = openingHoursView.findViewById(R.id.opening_hours_text);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    openingHoursText.setText(Html.fromHtml(showcase.getOpeningHours(), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    openingHoursText.setText(Html.fromHtml(showcase.getOpeningHours()));
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(SubscriberOverviewActivity.this);
                                builder
                                        .setView(openingHoursView)
                                        .setCancelable(true)
                                        .show();

                            });

                        }else {
                            openingHours.setVisibility(View.GONE);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.parseColor(showcasePrimaryColor));
                        }

                        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(showcaseSecondaryColor));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    private void loadStories(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storiesRecycler.setLayoutManager(linearLayoutManager);

        databaseReference
                .child("stories")
                .orderByChild("source")
                .equalTo(showcaseId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        stories.clear();

                        if (dataSnapshot.getValue() != null){

                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                stories.add(snapshot.getValue(Story.class));
                            }

                        }

                        overviewStoriesAdapter = new OverviewStoriesAdapter(SubscriberOverviewActivity.this, stories, new OverviewStoriesAdapter.StoriesAdapterListener() {
                            @Override
                            public void toStoriesEditor() { }

                            @Override
                            public void toStoryViewer(int position, Story story) {

                                startActivity(
                                        new Intent(SubscriberOverviewActivity.this, StoriesViewerSingleSourceActivity.class)
                                                .putExtra("mode", MULTI)
                                                .putExtra("position", position)
                                                .putExtra("showViewCounter", false)
                                                .putExtra("showcaseId", showcaseId)
                                                .putExtra("showcaseName", showcaseName)
                                                .putExtra("showcaseLogo", showcaseLogo)
                                                .putExtra("showcasePrimaryColor", showcasePrimaryColor)
                                                .putExtra("showcaseSecondaryColor", showcaseSecondaryColor));
                                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                            }
                        });

                        storiesRecycler.setAdapter(overviewStoriesAdapter);
                        storiesRecycler.setHasFixedSize(true);

                        overviewStoriesAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public String getShowcaseId(){
        return showcaseId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscriber_overview_menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_report) {
            reportShowcase();
        }
        return super.onOptionsItemSelected(item);
    }

    private void reportShowcase() {

        new AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.report_showcase_title))
                .setMessage(getString(R.string.report_showcase_message))
                .setPositiveButton(getString(R.string.report), (dialogInterface, i) -> {

                    // Disparition de la boîte de dialogue courante
                    dialogInterface.dismiss();

                    // Boîte de commentaire
                    AlertDialog.Builder builder = new AlertDialog.Builder(SubscriberOverviewActivity.this);
                    builder.setTitle(R.string.report_showcase_title);

                    // Vue personnalisée
                    @SuppressLint("InflateParams")
                    View reportView = LayoutInflater.from(SubscriberOverviewActivity.this)
                            .inflate(R.layout.layout_report_comment, null);

                    final EditText comment = reportView.findViewById(R.id.comment);

                    builder
                            .setPositiveButton(getString(R.string.send), (dialogInterface1, i1) -> {

                                DatabaseReference reportDbReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("reports");

                                String key = reportDbReference.push().getKey();

                                // Enregistrement du signalement
                                reportDbReference
                                        .child(key != null ? key : generateKey(13))
                                        .setValue(new Report(key,
                                                             showcaseId,
                                                             showcaseName,
                                                             showcaseLogo,
                                                             showcasePrimaryColor,
                                                             SHOWCASE_REPORT,
                                                             userId,
                                                             comment.getText().toString()))

                                        .addOnSuccessListener(aVoid -> Toast.makeText(SubscriberOverviewActivity.this, getString(R.string.ur_request_will_be_processed), Toast.LENGTH_SHORT).show());


                            })
                            .setNegativeButton(getString(R.string.cancel), null)
                            .setView(reportView)
                            .show();

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}
