package com.neteru.afrikett.ui.fragments.home_fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.libs.StateView.StateView;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifFragment extends Fragment {
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_notif, container, false);

        if (getActivity() != null) { activity = getActivity(); }

        FloatingActionButton fab = activity.findViewById(R.id.fab);

        if (fab.getVisibility() != View.GONE) {
            fab.setVisibility(View.GONE);
            fab.setAnimation(getFadeOutAnimation(getContext()));
        }

        // StateView
        StateView stateView = root.findViewById(R.id.status_page);

        // Ecouteurs de l'action d'ouverture du popup menu des notifications
        activity.findViewById(R.id.toolbar_to_open_notifications_menu)
                      .setOnClickListener(v -> {

                          // Instance du menu popup
                          PopupMenu popup = new PopupMenu(activity, v);
                          // Chargement du menu popup
                          popup.getMenuInflater()
                                  .inflate(R.menu.notifications_menu, popup.getMenu());

                          // Ecouteur de click sur les items menu
                          popup.setOnMenuItemClickListener(item -> true);

                          popup.show(); // Ouverture du menu popup

                      });

        stateView.displayState("no_notification");

        return root;
    }

}
