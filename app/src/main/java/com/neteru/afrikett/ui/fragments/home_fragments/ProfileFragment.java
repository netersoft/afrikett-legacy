package com.neteru.afrikett.ui.fragments.home_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.UserShowcaseAdapter;
import com.neteru.afrikett.core.adapters.SubscriptionAdapter;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.ui.activities.others_activities.settings.ProfileSettingsActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.AddActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.AdminOverviewActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.compactNumber;
import static com.neteru.afrikett.core.utilities.AppUtilities.cutLongText;
import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private Activity activity;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;
    private ImageView profilePic;
    private TextView profileSubscriptions;
    private TextView profileShowcases;
    private ImageButton manageSubscriptions;
    private ImageButton manageShowcases;
    private List<Showcase> showcaseList = new ArrayList<>();
    private List<Showcase> subscriptionList = new ArrayList<>();
    private UserShowcaseAdapter showcasesAdapter;
    private SubscriptionAdapter subscriptionsAdapter;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private PullRefreshLayout swiper;
    private StateView stateView_0;
    private StateView stateView_1;
    private LinearLayout linearLayout_0;
    private LinearLayout linearLayout_1;
    private int position;
    private String userId;
    private ValueEventListener userValueEventListener;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Photo de profil
        profilePic = root.findViewById(R.id.imgProfile);

        // Nombre d'abonnements
        profileSubscriptions = root.findViewById(R.id.subscriptionsProfile);

        // Nombre de vitrines
        profileShowcases = root.findViewById(R.id.showcasesProfile);

        // Bouton switcher des abonnements
        manageSubscriptions = root.findViewById(R.id.subscriptionsButton);

        // Bouton switcher des vitrines
        manageShowcases = root.findViewById(R.id.showcasesButton);

        // Layout des vitrines
        linearLayout_0 = root.findViewById(R.id.layout_0);

        // Layout des abonnements
        linearLayout_1 = root.findViewById(R.id.layout_1);

        // Recycler des vitrines
        RecyclerView recycler_0 = root.findViewById(R.id.recycler_0);

        // Recycler des abonnements
        RecyclerView recycler_1 = root.findViewById(R.id.recycler_1);

        // StateView des vitrines
        stateView_0 = root.findViewById(R.id.status_page_0);

        // StateView des abonnements
        stateView_1 = root.findViewById(R.id.status_page_1);

        // ScrollView
        nestedScrollView = root.findViewById(R.id.nestedScrollView);

        // Barre de progression
        progressBar = root.findViewById(R.id.progressBar);

        // Swiper de rechargement
        swiper = root.findViewById(R.id.swiper);

        if (getActivity() != null){ activity = getActivity(); }

        userId = getLocalUserData(activity).getId();

        // Reference de la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
        userDatabaseReference = databaseReference.child("users").child(userId);
                
        recycler_0.setLayoutManager(new LinearLayoutManager(activity));
        recycler_1.setLayoutManager(new LinearLayoutManager(activity));

        // Adapteur de la liste des abonnements
        subscriptionsAdapter = new SubscriptionAdapter(activity, subscriptionList, R.layout.template_subscription, showcase -> {

            startActivity(new Intent(activity, SubscriberOverviewActivity.class)
                    .putExtra("showcaseId", showcase.getId()));

            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        recycler_0.setHasFixedSize(true);

        recycler_0.setAdapter(subscriptionsAdapter);

        // Adapteur de la liste des vitrines
        showcasesAdapter = new UserShowcaseAdapter(activity, R.layout.template_manage_list, showcaseList, showcase -> {

            startActivity(new Intent(activity, AdminOverviewActivity.class)
                    .putExtra("showcaseId", showcase.getId()));

            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        recycler_1.setHasFixedSize(true);

        recycler_1.setAdapter(showcasesAdapter);

        // Position du switcher par défaut
        switcher(true);

        swiper.setOnRefreshListener(() -> {

            // Rechargement des data au swipe
            if (position == 0) {
                getSubscriptionsData();
            }else {
                getShowcasesData();
            }

        });

        // Switcher à droite (Vitrines)
        manageShowcases.setOnClickListener(view -> switcher(false));

        // Switcher à gauche (Abonnements)
        manageSubscriptions.setOnClickListener(view -> switcher(true));

        // Ouverture des paramètres profil
        root.findViewById(R.id.modifyProfile).setOnClickListener(view -> {

            startActivity(new Intent(activity, ProfileSettingsActivity.class));
            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        stateView_1.setOnStateButtonClicked(v -> {

            startActivity(new Intent(activity, AddActivity.class));
            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        // Initialisation...
        init();

        // Données vitrines
        getShowcasesData();

        // Données abonnements
        getSubscriptionsData();

        return root;
    }

    /**
     * Initialisation
     */
    private void init(){

        FloatingActionButton fab = activity.findViewById(R.id.fab);
        TextView profileName = activity.findViewById(R.id.toolbar_name);

        if (fab.getVisibility() != View.GONE) {
            fab.setVisibility(View.GONE);
            fab.setAnimation(getFadeOutAnimation(activity));
        }

        // Photo de profil de l'utilisateur
        if (getLocalUserData(activity).getProfileUrl().equals(DEFAULT)) {

            profilePic.setImageDrawable(TextDrawable.builder()
                    .buildRound(getFirstLetters(getLocalUserData(activity).getName()),
                                COLORS[getDigitFromString(getLocalUserData(activity).getName())]));

        }else {

            Glide
                    .with(activity)
                    .load(getLocalUserData(activity).getProfileUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic);
        }

        // Nom de l'utilisateur
        profileName.setText(cutLongText(getLocalUserData(activity).getName(), 21));

        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){

                    User user = dataSnapshot.getValue(User.class);

                    if (user == null) return;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        profileShowcases.setText(Html.fromHtml("<b>" + compactNumber(user.getNbShowcases()) + "</b> "+getString(R.string.showcases), Html.FROM_HTML_MODE_COMPACT));
                        profileSubscriptions.setText(Html.fromHtml("<b>" + compactNumber(user.getNbSubscriptions()) + "</b> "+getString(R.string.subscriptions), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        profileShowcases.setText(Html.fromHtml("<b>" + compactNumber(user.getNbShowcases()) + "</b> "+getString(R.string.showcases)));
                        profileSubscriptions.setText(Html.fromHtml("<b>" + compactNumber(user.getNbSubscriptions()) + "</b> "+getString(R.string.subscriptions)));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        // Récupération du nombre d'abonnements et de vitrines
        userDatabaseReference.addValueEventListener(userValueEventListener);

    }

    /**
     * Switcher
     * @param b / destination du switcher
     */
    private void switcher(boolean b){

        if (b){ // Switcher à gauche avec interchangement d'état de ces composants

            position = 0;

            linearLayout_1.setVisibility(View.GONE);
            linearLayout_0.setVisibility(View.VISIBLE);
            linearLayout_0.startAnimation(getFadeInAnimation(activity));

            manageShowcases.setColorFilter(getResources().getColor(R.color.gray));
            manageSubscriptions.setColorFilter(getResources().getColor(R.color.orange));

        }else{ // Switcher à droite avec interchangement d'état de ces composants

            position = 1;

            linearLayout_0.setVisibility(View.GONE);
            linearLayout_1.setVisibility(View.VISIBLE);
            linearLayout_1.startAnimation(getFadeInAnimation(activity));

            manageShowcases.setColorFilter(getResources().getColor(R.color.orange));
            manageSubscriptions.setColorFilter(getResources().getColor(R.color.gray));

        }
    }

    /**
     * Chargement des données des abonnements
     */
    private void getSubscriptionsData(){

        progressBar.setVisibility(View.VISIBLE);

        databaseReference
                .child("showcases")
                .orderByChild("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {

                            changeSubscriptionViewState(false);

                            return;
                        }

                        subscriptionList.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Showcase userShowcase = snapshot.getValue(Showcase.class);

                            if (userShowcase != null){
                                // On recupère uniquement les vitrines pour lesquelles l'utilisateur est abonné
                                if (userShowcase.getSubscribers() != null && userShowcase.getSubscribers().contains(userId)){

                                    subscriptionList.add(userShowcase);

                                }

                            }
                        }

                        changeSubscriptionViewState(!subscriptionList.isEmpty());

                        nestedScrollView.fullScroll(View.FOCUS_UP);
                        nestedScrollView.smoothScrollTo(0,0);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        progressBar.setVisibility(View.GONE);
                        swiper.setRefreshing(false);

                    }

                });

    }

    /**
     * Chargement des données des vitrines
     */
    private void getShowcasesData(){

        progressBar.setVisibility(View.VISIBLE);

        databaseReference
                .child("showcases")
                .orderByChild("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {

                            changeShowcaseViewState(false);

                            return;
                        }

                        showcaseList.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Showcase userShowcase = snapshot.getValue(Showcase.class);

                            if (userShowcase != null){
                                // On recupère uniquement les vitrines pour lesquelles l'utilisateur est administrateur
                                if (userShowcase.getOwners() != null && userShowcase.getOwners().contains(userId)){

                                    showcaseList.add(userShowcase);

                                }
                            }
                        }

                        changeShowcaseViewState(!showcaseList.isEmpty());

                        nestedScrollView.fullScroll(View.FOCUS_UP);
                        nestedScrollView.smoothScrollTo(0,0);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        progressBar.setVisibility(View.GONE);
                        swiper.setRefreshing(false);

                    }

        });

    }

    private void changeSubscriptionViewState(boolean state){
        if (state){
            stateView_0.hideStates();
        }else {
            stateView_0.displayState("no_subscription");
        }

        subscriptionsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        swiper.setRefreshing(false);
    }

    private void changeShowcaseViewState(boolean state){
        if (state){
            stateView_1.hideStates();
        }else {
            stateView_1.displayState("no_showcase");
        }

        showcasesAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        swiper.setRefreshing(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        
        userDatabaseReference.removeEventListener(userValueEventListener);
    }
}
