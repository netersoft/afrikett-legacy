package com.neteru.afrikett.core.utilities;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.neteru.afrikett.core.interfaces.LifeCycleDelegate;

/**
 * Gestionnaire de cycle de vie de l'application
 */
public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    // Indicateur d'état d'avant plan
    private boolean appInForeground = false;

    // Interface de changement d'état
    private LifeCycleDelegate lifeCycleDelegate;

    public AppLifecycleHandler(LifeCycleDelegate lifeCycleDelegate) {
        this.lifeCycleDelegate = lifeCycleDelegate;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * En reprise du cycle de vie de l'application
     * @param activity / activité en avant-plan
     */
    @Override
    public void onActivityResumed(Activity activity) {
        // Si l'application est en avant-plan
        if (!appInForeground) {

            // On change l'état de l'indcateur
            appInForeground = true;
            // On appel la méthode correspondante à cet état
            this.lifeCycleDelegate.onAppForegrounded();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onTrimMemory(int level) {
        // Si l'UI est en arrière-plan
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {

            // On change l'état de l'indicateur
            appInForeground = false;
            // On appel la méthode correspondante à cet état
            lifeCycleDelegate.onAppBackgrounded();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}



