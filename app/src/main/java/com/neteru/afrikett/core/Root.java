package com.neteru.afrikett.core;

import android.content.Context;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.interfaces.LifeCycleDelegate;
import com.neteru.afrikett.core.utilities.AppLifecycleHandler;
import com.neteru.afrikett.core.libs.StateView.StateViewsBuilder;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Timing.getCurrentDate;

public class Root extends MultiDexApplication implements LifeCycleDelegate {

    // Etats de déconnexion et de connexion
    private final static int OFFLINE = 0, ONLINE = 1;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* Activation de la persistence dans la base de données */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        /* Installation des emoticônes */
        EmojiManager.install(new IosEmojiProvider());


        /* Initialisation de la librairie de gestion des fontes */
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/rmedium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        /* Initialisation du gestionnaire de cycle de l'application */
            // Instanciation du gestionnaire
            AppLifecycleHandler lifecycleHandler = new AppLifecycleHandler(this);
            // Attachement du gestionnaire à la classe racine de l'application
            registerLifecycleHandler(lifecycleHandler);


        /* Initialisation des StateViews */
        StateViewsBuilder
                .init(this)

                // StateView de bienvenue du fragment home
                .addState("welcome_home",
                        getString(R.string.welcome_home_state_title),
                        getString(R.string.welcome_home_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.welcome_home),
                        getString(R.string.begin)
                )

                // StateView de bienvenue du fragment messenger
                .addState("welcome_messenger",
                        getString(R.string.welcome_messenger_state_title),
                        getString(R.string.welcome_messenger_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.welcome_messenger),
                        getString(R.string.find_contacts)
                )

                // StateView de boîte à notifications vide
                .addState("no_notification",
                        getString(R.string.no_notification_state_title),
                        getString(R.string.no_notification_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.no_notification),
                        null
                )

                // StateView de liste de vitrines vide
                .addState("no_showcase",
                        getString(R.string.no_showcase_state_title),
                        getString(R.string.no_showcase_state_description),
                        null,
                        getString(R.string.begin)
                )

                // StateView de liste d'abonnements vide
                .addState("no_subscription",
                        getString(R.string.no_subscription_state_title),
                        getString(R.string.no_subscription_state_description),
                        null,
                        null
                )

                // StateView de liste de publications enregistrées vide
                .addState("no_favorites",
                        getString(R.string.no_favorites_state_title),
                        getString(R.string.no_favorites_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.no_favorites),
                        null
                )

                // StateView de liste de produits et services ajoutés vide
                .addState("no_addition",
                        getString(R.string.no_addition_state_title),
                        getString(R.string.no_addition_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.ic_cart),
                        null
                )

                // StateView de liste de viewers vide
                .addState("no_views_yet",
                        getString(R.string.no_views_yet_state_title),
                        null,
                        null,
                        null
                )

                // StateView de liste d'abonnés vide
                .addState("no_subscribers_yet",
                        getString(R.string.no_subscribers_yet_state_title),
                        null,
                        null,
                        null
                )

                // StateView de liste de story vide
                .addState("no_story",
                        getString(R.string.no_story_state_title),
                        getString(R.string.no_story_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.no_story),
                        null
                )

                // StateView pour des résultats de recherche non trouvés
                .addState("no_result_found",
                        getString(R.string.no_result_found_state_title),
                        getString(R.string.no_result_found_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.no_result_found),
                       null
                )

                // StateView pour aucun contact trouvé
                .addState("no_contact",
                        getString(R.string.no_contact_found_state_title),
                        getString(R.string.no_contact_found_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.no_contact),
                        getString(R.string.invite_friends)
                )

                // StateView pour aucune donnée disponible
                .addState("no_data_available",
                        getString(R.string.no_data_available_title),
                        null,
                        AppCompatResources.getDrawable(this, R.drawable.no_data_available),
                        null
                )

                // StateView pour les erreurs non déterminées
                .addState("error_occurred",
                        getString(R.string.error_occurred_state_title),
                        getString(R.string.error_occurred_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.error_occured),
                        getString(R.string.retry)
                )

                // StateView pour les erreurs de connexion
                .addState("error_connection",
                        getString(R.string.error_connection_state_title),
                        getString(R.string.error_connection_state_description),
                         AppCompatResources.getDrawable(this, R.drawable.error_connection),
                        getString(R.string.retry)
                )

                // StateView pour les erreurs de chargement de pages web
                .addState("error_page_loading",
                        getString(R.string.error_occurred_state_title),
                        getString(R.string.error_page_loading_state_description),
                        AppCompatResources.getDrawable(this, R.drawable.error_connection),
                        getString(R.string.retry),
                        getResources().getColor(R.color.silver)
                )

                .setButtonTextColor(getResources().getColor(R.color.white))
                .setButtonBackgroundColor(getResources().getColor(R.color.orange))
                .setIconSize(getResources().getDimensionPixelSize(R.dimen.state_views_icon_size));

    }


    /*
     * Photo Editor
     */
    public Context getContext() {
        return this;
    }

    /**
     * Application en arrière-plan
     */
    @Override
    public void onAppBackgrounded() {

        if (getLocalUserData(this).getId() == null){ return; }

        // Reference à l'utilisateur dans la base de données
        DatabaseReference databaseUserReference = FirebaseDatabase
                .getInstance()
                .getReference(DATABASE_ROOT)
                .child("users")
                .child(getLocalUserData(this).getId());

        // Status Déconnecté
        databaseUserReference
                .child("status")
                .setValue(OFFLINE);

        // Ecriture du dernier instant de connexion
        databaseUserReference
                .child("lastConnection")
                .setValue(getCurrentDate());

    }

    /**
     * Application en avant-plan
     */
    @Override
    public void onAppForegrounded() {

        if (getLocalUserData(this).getId() == null){ return; }

        // Status Connecté
        FirebaseDatabase
                .getInstance()
                .getReference(DATABASE_ROOT)
                .child("users")
                .child(getLocalUserData(this).getId())
                .child("status")
                .setValue(ONLINE);

    }

    /**
     * Attachement du gestionnaire de cycle de vie à la classe racine de l'application
     * @param lifeCycleHandler / gestionnaire de cycle de vie
     */
    private void registerLifecycleHandler(AppLifecycleHandler lifeCycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler);
        registerComponentCallbacks(lifeCycleHandler);
    }
}
