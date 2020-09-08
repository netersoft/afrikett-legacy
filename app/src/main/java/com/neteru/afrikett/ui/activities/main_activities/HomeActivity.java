package com.neteru.afrikett.ui.activities.main_activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AccessHandler;
import com.neteru.afrikett.core.utilities.AppUtilities;
import com.neteru.afrikett.core.utilities.BottomNavigationBehavior;
import com.neteru.afrikett.ui.activities.others_activities.AboutActivity;
import com.neteru.afrikett.ui.activities.others_activities.FavoritesActivity;
import com.neteru.afrikett.ui.activities.others_activities.HelpActivity;
import com.neteru.afrikett.ui.activities.others_activities.SuggestionsActivity;
import com.neteru.afrikett.ui.activities.others_activities.settings.GeneralSettingsActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.AddActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.ManageActivity;
import com.neteru.afrikett.ui.fragments.home_fragments.HomeFragment;
import com.neteru.afrikett.ui.fragments.home_fragments.MessengerFragment;
import com.neteru.afrikett.ui.fragments.home_fragments.NotifFragment;
import com.neteru.afrikett.ui.fragments.home_fragments.ProfileFragment;
import com.neteru.afrikett.ui.fragments.home_fragments.SearchFragment;

import static com.neteru.afrikett.core.utilities.AppUtilities.compactNumber;
import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.EMAIL;

/**
 * Activité Centrale
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int id;
    private BottomNavigationView bottomNavigationView;
    private CoordinatorLayout.LayoutParams layoutParams;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView toolbar_menu;
    private ImageView toolbar_to_manage;
    private ImageView toolbar_to_settings;
    private ImageView toolbar_to_favorites_a;
    private ImageView toolbar_to_favorites_b;
    private ImageView drawerHeaderProfile;
    private TextView drawerHeaderName;
    private TextView drawerHeaderPhoneNumber;
    private TextView drawerHeaderShowcase;
    private TextView drawerHeaderSubscription;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ActionBarDrawerToggle toggle;
    private RelativeLayout mainTools;
    private RelativeLayout profileTools;
    private RelativeLayout messengerTools;
    private RelativeLayout notificationsTools;
    private LinearLayout navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Barre d'outils
        final Toolbar toolbar = findViewById(R.id.toolbar);

        // Volet de navigation
        drawer = findViewById(R.id.drawer_layout);

        // Vue de navigation latérale
        navigationView = findViewById(R.id.nav_view);

        // Vue de navigation
        bottomNavigationView = findViewById(R.id.navigation);

        // Photo de profil de la barre d'outils
        toolbar_menu = findViewById(R.id.toolbar_menu);

        // Action vers le gestionnaire de vitrines de la barre d'outils
        toolbar_to_manage = findViewById(R.id.toolbar_to_manage);

        // Action vers les paramètres de la barre d'outils
        toolbar_to_settings = findViewById(R.id.toolbar_to_settings);

        // Action vers la liste des posts favoris
        toolbar_to_favorites_a = findViewById(R.id.toolbar_to_favorites_a);
        toolbar_to_favorites_b = findViewById(R.id.toolbar_to_favorites_b);

        // Entête du volet de navigation
        navHeader = navigationView.getHeaderView(0).findViewById(R.id.usernavheader);

        // Photo de profil du volet de navigation latérale
        drawerHeaderProfile = navigationView.getHeaderView(0).findViewById(R.id.userpicture);

        // Nom de profil du volet de navigation latérale
        drawerHeaderName = navigationView.getHeaderView(0).findViewById(R.id.username);

        // Numéro de profil du volet de navigation latérale
        drawerHeaderPhoneNumber = navigationView.getHeaderView(0).findViewById(R.id.usernumber);

        // Nombre de vitrines du volet de navigation latérale
        drawerHeaderShowcase = navigationView.getHeaderView(0).findViewById(R.id.showcases);

        // Nombre d'abonnements du volet de navigation latérale
        drawerHeaderSubscription = navigationView.getHeaderView(0).findViewById(R.id.subscriptions);

        // Outils du fragment profil
        profileTools = findViewById(R.id.profile_tools);

        // Outils du fragment home
        mainTools = findViewById(R.id.main_tools);

        // Outils du fragment messenger
        messengerTools = findViewById(R.id.messenger_tools);

        // Outils du fragment notifications
        notificationsTools = findViewById(R.id.notifications_tools);

        // Chargement de la barre d'outils personnalisée
        setSupportActionBar(toolbar);

        // Configuration de la couleur des icônes de la barre d'outils
        if (toolbar.getOverflowIcon() != null){
            toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
        }

        // Desactivation du titre
        if (getSupportActionBar() != null){ getSupportActionBar().setDisplayShowTitleEnabled(false); }

        // Instance de l'authentificateur
        mAuth = FirebaseAuth.getInstance();

        // Reference vers la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("users");

        // Indicateur d'ouverture et fermeture de la barre de navigation latérale
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Personnalisation de la couleur de l'indicateur
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.skyblue));

        toggle.syncState();
        drawer.addDrawerListener(toggle);

        // Branchement de l'écouteur d'évenement "click" sur un item de la vue de navigation latérale
        navigationView.setNavigationItemSelectedListener(this);

        // Disparition / Apparition du BottomNavigationView au scroll
        layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();

        byDefault();

        // Chargement des informations profil
        setHeaderInfo();

        // Changement de la taille des items de la Bottom Navigation View
        setBottomNavigationItemSize();

        // Branchement de l'écouteur d'évenement "click" sur un item de la vue de la BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            // ID globale de l'item selectionné
            id = item.getItemId();

            // Camouflage par défaut de l'indicateur
            toggle.setDrawerIndicatorEnabled(true);

            // Gestion par défaut des différentes configuration de la barre d'outils
            if (mainTools.getVisibility() != View.VISIBLE) {
                profileTools.setVisibility(View.GONE);
                messengerTools.setVisibility(View.GONE);
                notificationsTools.setVisibility(View.GONE);
                mainTools.setVisibility(View.VISIBLE);
            }

            // Etat par défaut de l'entête de navigation
            if (navHeader.getVisibility() != View.VISIBLE){
                navHeader.setVisibility(View.VISIBLE);
            }

            // Etat par défaut du BottomNavigationView au scroll
            layoutParams.setBehavior(null);

            // Etat par défaut de la photo de la barre d'outils
            toolbar_menu.setVisibility(View.INVISIBLE);

            // Chargement des icones par défaut
            bottomNavigationViewItemDefault();

            switch (id){
                case R.id.nav_profile:

                    // Gestion de la barre d'outils propre au fragment profil
                    if (mainTools.getVisibility() == View.VISIBLE) {
                        mainTools.setVisibility(View.GONE);
                        messengerTools.setVisibility(View.GONE);
                        notificationsTools.setVisibility(View.GONE);
                        profileTools.setVisibility(View.VISIBLE);
                    }

                    // Gestion de l'entête de navigation propre au fragment profil
                    if (navHeader.getVisibility() == View.VISIBLE){
                        navHeader.setVisibility(View.GONE);
                    }

                    // Activation de la disparition / apparition du BottomNavigationView au scroll
                    // layoutParams.setBehavior(new BottomNavigationBehavior());

                    // Chargement du fragment profil et mise à jour de item actif du BottomNavigationView
                    loadFragment(new ProfileFragment());
                    item.setIcon(R.mipmap.ic_person_white_24dp);
                    break;

                case R.id.nav_search:

                    // Gestion de la barre d'outils propre au fragment recherche
                    if (mainTools.getVisibility() == View.VISIBLE) {
                        mainTools.setVisibility(View.GONE);
                    }

                    // Activation de la disparition / apparition du BottomNavigationView au scroll
                    // layoutParams.setBehavior(new BottomNavigationBehavior());

                    // Chargement du fragment recherche
                    loadFragment(new SearchFragment());
                    break;

                case R.id.nav_home:

                    // Activation de l'indicateur
                    toggle.setDrawerIndicatorEnabled(false);

                    toolbar_menu.setVisibility(View.VISIBLE);

                    // Activation de la disparition / apparition du BottomNavigationView au scroll
                    layoutParams.setBehavior(new BottomNavigationBehavior());

                    // Chargement du fragment home et mise à jour de item actif du BottomNavigationView
                    loadFragment(new HomeFragment());
                    item.setIcon(R.mipmap.ic_home_white_24dp).setChecked(true);
                    break;

                case R.id.nav_notif:

                    // Gestion de la barre d'outils propre au fragment notifications
                    if (mainTools.getVisibility() == View.VISIBLE) {
                        mainTools.setVisibility(View.GONE);
                        messengerTools.setVisibility(View.GONE);
                        profileTools.setVisibility(View.GONE);
                        notificationsTools.setVisibility(View.VISIBLE);
                    }

                    // Activation de la disparition / apparition du BottomNavigationView au scroll
                    // layoutParams.setBehavior(new BottomNavigationBehavior());

                    // Chargement du fragment notifications et mise à jour de item actif du BottomNavigationView
                    loadFragment(new NotifFragment());
                    item.setIcon(R.mipmap.ic_notifications_white_24dp);
                    break;

                case R.id.nav_messenger:

                    // Gestion de la barre d'outils propre au fragment messenger
                    if (mainTools.getVisibility() == View.VISIBLE) {
                        mainTools.setVisibility(View.GONE);
                        profileTools.setVisibility(View.GONE);
                        notificationsTools.setVisibility(View.GONE);
                        messengerTools.setVisibility(View.VISIBLE);
                    }

                    // Activation de la disparition / apparition du BottomNavigationView au scroll
                    // layoutParams.setBehavior(new BottomNavigationBehavior());

                    // Chargement du fragment messenger et mise à jour de item actif du BottomNavigationView
                    loadFragment(new MessengerFragment());
                    item.setIcon(R.mipmap.baseline_email_white_24);
                    break;
            }

            return true;
        });

        // Branchement des écouteurs d'évènements
        setOnClickListeners();

        // Redirection vers une activité
        redirectToActivity();

        // Redirection vers un fragment
        redirectToFragment();

        // Gestion d'accès à l'app
        AccessHandler.getInstance(HomeActivity.this).execute();
    }

    /**
     * Recupération et attribution des données de base
     */
    private void setHeaderInfo(){
        if (mAuth.getCurrentUser() != null){ // Si l'utilisateur est authentifié

             databaseReference
                 .child(AppUtilities.getLocalUserData(this).getId())
                 .addValueEventListener(new ValueEventListener() {

                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         User user = dataSnapshot.getValue(User.class);

                         if (user != null) { // Si les données utilisateurs existent

                             // Nom d'utilisateur
                             drawerHeaderName.setText(user.getName().trim());

                             // Numéro de téléphone ou Adresse email
                             if (user.getConnectedWith().equals(EMAIL)) {
                                 drawerHeaderPhoneNumber.setText(user.getEmail().trim());
                             } else{
                                 drawerHeaderPhoneNumber.setText(user.getNumber().trim());
                             }

                             if (user.getProfileUrl().equals(DEFAULT)) { // Si photo de profil par défaut

                                 // Chargement du TextDrawable de la barre latérale
                                 drawerHeaderProfile.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(user.getName()), COLORS[getDigitFromString(user.getName())]));

                             }else { // Sinon

                                 // Chargement de la photo de profil de la barre latérale
                                 Glide
                                         .with(HomeActivity.this)
                                         .load(user.getProfileUrl())
                                         .apply(RequestOptions.circleCropTransform())
                                         .into(drawerHeaderProfile);
                             }

                             // Chargement du nombre d'abonnements et du nombre de vitrines
                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                 drawerHeaderShowcase.setText(Html.fromHtml("<b>" + compactNumber(user.getNbShowcases()) + "</b> "+getString(R.string.showcases), Html.FROM_HTML_MODE_COMPACT));
                                 drawerHeaderSubscription.setText(Html.fromHtml("<b>" + compactNumber(user.getNbSubscriptions()) + "</b> "+getString(R.string.subscriptions), Html.FROM_HTML_MODE_COMPACT));
                             } else {
                                 drawerHeaderShowcase.setText(Html.fromHtml("<b>" + compactNumber(user.getNbShowcases()) + "</b> "+getString(R.string.showcases)));
                                 drawerHeaderSubscription.setText(Html.fromHtml("<b>" + compactNumber(user.getNbSubscriptions()) + "</b> "+getString(R.string.subscriptions)));
                             }
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
             });
        }
    }

    /**
     * Ecouteurs de Click
     */
    private void setOnClickListeners() {

        // Ecouteurs de la photo de profil de la barre d'outils
        toolbar_menu.setOnClickListener(view -> drawer.openDrawer(GravityCompat.START));

        // Ecouteurs de l'action vers le suggéreur de vitrines
        toolbar_to_manage.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, SuggestionsActivity.class));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
        });

        // Ecouteurs de l'action vers la liste des posts favoris
        toolbar_to_favorites_a.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, FavoritesActivity.class)
                                    .putExtra("cart", true));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
        });
        toolbar_to_favorites_b.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, FavoritesActivity.class)
                                    .putExtra("cart", false));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
        });

        // Ecouteurs de l'action vers les paramètres de la barre d'outils
        toolbar_to_settings.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, GeneralSettingsActivity.class));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
        });

        RelativeLayout toProfile = navigationView.getHeaderView(0).findViewById(R.id.toProfile);
        // Ecouteurs du raccourci profil du volet de navigation latérale
        toProfile.setOnClickListener(view -> {

            //Fermeture du volet
            drawer.closeDrawer(GravityCompat.START);

            new Handler().postDelayed(() -> {

                // Chargement du fragment profil
                if (id != R.id.nav_profile) {
                    bottomNavigationView.findViewById(R.id.nav_profile).performClick();
                }

            }, 300);

        });

    }

    /**
     * Customisation de la taille des items du BottomNavigationView
     */
    private void setBottomNavigationItemSize(){
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {

            final View iconView = bottomNavigationMenuView.getChildAt(i).findViewById(com.google.android.material.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

            // Changement de la hauteur des items
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);

            // Changement de la largeur des items
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);
            iconView.setLayoutParams(layoutParams);

        }
    }

    /**
     * Icones par defaut du BottomNavigationView
     */
    private void bottomNavigationViewItemDefault(){
        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setIcon(R.mipmap.outline_person_white_24);
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setIcon(R.mipmap.outline_home_white_24);
        bottomNavigationView.getMenu().findItem(R.id.nav_notif).setIcon(R.mipmap.outline_notifications_white_24);
        bottomNavigationView.getMenu().findItem(R.id.nav_messenger).setIcon(R.mipmap.outline_email_white_24);

    }

    /**
     * Configuration par défaut
     */
    private void byDefault(){

        // ID de l'item par défaut
        id = R.id.nav_home;

        // Etat par défaut de l'entête de navigation
        if (navHeader.getVisibility() != View.VISIBLE){
            navHeader.setVisibility(View.VISIBLE);
        }

        // Configuration par défaut de la barre d'outils
        if (mainTools.getVisibility() != View.VISIBLE) {
            profileTools.setVisibility(View.GONE);
            messengerTools.setVisibility(View.GONE);
            notificationsTools.setVisibility(View.GONE);
            mainTools.setVisibility(View.VISIBLE);
        }

        bottomNavigationViewItemDefault();

        // Désactivation de l'indicateur
        toggle.setDrawerIndicatorEnabled(false);

        // Activation de la disparition / apparition du BottomNavigationView au scroll
        layoutParams.setBehavior(new BottomNavigationBehavior());

        toolbar_menu.setVisibility(View.VISIBLE);

        // Chargement du fragment home et mise à jour de l'icône de l'item selectionné
        bottomNavigationView.getMenu().getItem(2).setIcon(R.mipmap.ic_home_white_24dp).setChecked(true);
        loadFragment(new HomeFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Mise à jour de l'icône de l'item par défaut au relancement de l'activité
        if (id == R.id.nav_home){ bottomNavigationView.getMenu().getItem(2).setIcon(R.mipmap.ic_home_white_24dp).setChecked(true); }

    }

    /**
     * Chargement des fragments
     * @param fragment / Le fragment à charger
     */
    private void loadFragment(Fragment fragment) {

        // Chargement de fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Invalidation du menu
        invalidateOptionsMenu();

    }

    /**
     * Capture de l'action Retour
     */
    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) { // Si volet ouvert

            // Le fermer
            drawer.closeDrawer(GravityCompat.START);

        } else { // Sinon
            if (id != R.id.nav_home){ // Si ID != ID par défaut

                byDefault(); // Retourner vers ID par défaut

            }else { // Sinon
                this.finish(); // Mettre fin à l'activité
            }
        }

    }

    /**
     * Capture des actions du drawer
     * @param item /
     * @return booleen
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Capture de click des items de la barre de navigation latérale
        switch (item.getItemId()){
            case R.id.nav_add:
                startActivity(new Intent(this, AddActivity.class));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.nav_manage:
                startActivity(new Intent(this, ManageActivity.class));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, GeneralSettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.nav_share:
                //Action de partage
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+"\n https://play.google.com/store/apps/details?id="+getPackageName()+"\n\n");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;
        }

        // Fermeture du volet de navigation
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Redirection vers une activité
     */
    private void redirectToActivity(){
        if (getIntent().hasExtra("redirectToActivity")){ // Si l'extra "redirectToActivity" existe
            try {

                Class<?> c = Class.forName(getIntent().getStringExtra("redirectToActivity"));
                startActivity(new Intent(this, c));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Redirection vers un fragment
     */
    private void redirectToFragment(){
        if (getIntent().hasExtra("redirectToFragment")){ // Si l'extra "redirectToFragment" existe

            View view = bottomNavigationView.findViewById(getIntent().getIntExtra("redirectToFragment", R.id.nav_home));
            view.performClick();

        }
    }

}
