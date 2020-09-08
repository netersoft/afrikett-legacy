package com.neteru.afrikett.ui.activities.launch_activities.splash;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.ui.activities.launch_activities.intro.IntroActivity;
import com.neteru.afrikett.ui.activities.launch_activities.start.phone.InfoActivity;
import com.neteru.afrikett.ui.activities.launch_activities.start.phone.GetPhoneNumberActivity;
import com.neteru.afrikett.ui.activities.main_activities.HomeActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.getInfoData;
import static com.neteru.afrikett.core.utilities.AppUtilities.getStringPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.isFirstLaunch;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Déclaration de l'Intent de redirection à partir du splashscreen
            Intent redirect;

            if (isFirstLaunch(SplashActivity.this)){ // S'il s'agit de la toute première ouverture
                                                                            // On redirige vers l'intro
                redirect = new Intent(SplashActivity.this, IntroActivity.class);

            }else{ // Sinon

                if (FirebaseAuth.getInstance().getCurrentUser() != null){ // Si l'utilisateur est quand même authentifié

                    // On vérifie l'existence de la variable préférentielle renfermant l'identifiant
                    // Si elle existe cela suppose que l'utilisateur est proprement loggé
                    // Sinon l'utilisateur a abandonné l'opération en cours de route
                    if (getStringPreference(SplashActivity.this, Constants.USER_PREFS, "id", null) != null ){

                        redirect = new Intent(SplashActivity.this, HomeActivity.class);

                    }else{

                        // On recupère alors les données laissées en cours de route et on les redirige
                        // Vers la page de renseignement d'informations supplémentaires du parcours phone
                        String[] infoActivityData = getInfoData(SplashActivity.this);
                        redirect = new Intent(SplashActivity.this, InfoActivity.class);
                        redirect.putExtra("nationalNumber", infoActivityData[0])
                                .putExtra("countryCode", infoActivityData[1])
                                .putExtra("number", infoActivityData[2])
                                .putExtra("country", infoActivityData[3]);

                    }

                }else {

                    // Sinon on redirige l'utilisateur vers l'activité de démarrage
                    redirect = new Intent(SplashActivity.this, GetPhoneNumberActivity.class);

                }
            }

            // On supprime toute trace du splashscreen dans l'historique des activités
            redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivityForResult(redirect, Constants.RANDOM_VALUE);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }, Constants.SHORT_DELAY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Non support de l'action Retour
        finish();
    }
}
