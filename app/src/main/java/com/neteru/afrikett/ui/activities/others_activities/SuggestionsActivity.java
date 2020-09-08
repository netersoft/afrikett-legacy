package com.neteru.afrikett.ui.activities.others_activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.SubscriptionAdapter;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.SubscriberOverviewActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class SuggestionsActivity extends AfrikettBaseActivity {
    private List<Showcase> suggestionList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private SubscriptionAdapter adapter;
    private StateView stateView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        userId = getLocalUserData(this).getId();

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        stateView = findViewById(R.id.stateview);
        RecyclerView recyclerView = findViewById(R.id.recycler);

        adapter = new SubscriptionAdapter(this, suggestionList, R.layout.template_subscription, showcase -> {

            startActivity(new Intent(SuggestionsActivity.this, SubscriberOverviewActivity.class)
                    .putExtra("showcaseId", showcase.getId()));

            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        getSuggestions();

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.showcases_to_discover));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skyblue));
        }
    }

    private void getSuggestions() {

        stateView.displayLoadingState();

        databaseReference
                .child("showcases")
                .orderByChild("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {

                            changeSuggestionViewState(false);
                            return;
                        }

                        suggestionList.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Showcase showcase = snapshot.getValue(Showcase.class);

                            if (showcase != null){

                                if (showcase.getOwners() != null && !showcase.getOwners().contains(userId)){

                                    suggestionList.add(showcase);

                                }

                            }
                        }

                        if (suggestionList.isEmpty()){

                            changeSuggestionViewState(false);
                            return;
                        }

                        Collections.shuffle(suggestionList);

                        changeSuggestionViewState(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });


    }

    private void changeSuggestionViewState(boolean state){
        if (state){
            stateView.hideStates();
        }else {
            stateView.displayState("no_data_available");
        }

        adapter.notifyDataSetChanged();
    }
}
