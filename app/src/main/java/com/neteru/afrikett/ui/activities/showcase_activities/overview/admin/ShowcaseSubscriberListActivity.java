package com.neteru.afrikett.ui.activities.showcase_activities.overview.admin;

import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.UserListAdapter;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class ShowcaseSubscriberListActivity extends AfrikettBaseActivity {
    private DatabaseReference databaseReference;
    private UserListAdapter adapter;
    private List<String> users = new ArrayList<>();
    private String showcaseId;
    private StateView stateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_subscriber_list);

        showcaseId = getIntent().getStringExtra("showcaseId");
        String showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("showcases");

        stateView = findViewById(R.id.stateview);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserListAdapter(this, users);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        getSubscribers();

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
            Spannable title = new SpannableString(getString(R.string.subscribers));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(showcasePrimaryColor));
        }
    }

    private void getSubscribers() {

        stateView.displayLoadingState();

        databaseReference
                .child(showcaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){

                            stateView.displayState("no_subscribers_yet");
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        Showcase showcase = dataSnapshot.getValue(Showcase.class);

                        if (showcase == null) return;
                        users.clear();

                        for (String id : showcase.getSubscribers()){
                            if (!showcase.getOwners().contains(id)){
                                users.add(id);
                            }
                        }

                        if (users.isEmpty()){
                            stateView.displayState("no_subscribers_yet");
                        }else {
                            stateView.hideStates();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
