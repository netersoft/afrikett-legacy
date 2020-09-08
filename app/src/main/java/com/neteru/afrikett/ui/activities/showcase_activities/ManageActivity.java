package com.neteru.afrikett.ui.activities.showcase_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.UserShowcaseAdapter;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.AppUtilities;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.utilities.OnSwipeTouchListener;
import com.neteru.afrikett.ui.activities.showcase_activities.overview.AdminOverviewActivity;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class ManageActivity extends AfrikettBaseActivity {
    private List<Showcase> showcaseList = new ArrayList<>();
    private UserShowcaseAdapter adapter;
    private DatabaseReference databaseReference;
    private LoadingDialog loadingDialog;
    private PullRefreshLayout refresh;
    private StateView stateView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        refresh = findViewById(R.id.refresh);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        stateView = findViewById(R.id.status_page);

        refresh.setOnRefreshListener(this::getData);

        loadingDialog = new LoadingDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("showcases");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new UserShowcaseAdapter(ManageActivity.this, R.layout.template_manage_list, showcaseList, showcase -> {

            startActivity(new Intent(ManageActivity.this, AdminOverviewActivity.class)
                    .putExtra("showcaseId", showcase.getId()));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        // Chargement des données
        getData();

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() { }

            @Override
            public void onSwipeRight() {

                finish();
                overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

            }
        };

        recyclerView.setOnTouchListener(onSwipeTouchListener);

        stateView.setOnStateButtonClicked(v -> {

            startActivity(new Intent(ManageActivity.this, AddActivity.class));
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.manage_ur_showcase));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    /**
     * Chargement des données
     */
    private void getData(){

        loadingDialog.show();

        databaseReference
                .orderByChild("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {

                    loadingDialog.dismiss();
                    refresh.setRefreshing(false);
                    stateView.displayState("no_showcase");

                    return;
                }

                showcaseList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Showcase userShowcase = snapshot.getValue(Showcase.class);

                    if (userShowcase != null){
                        for (String s : userShowcase.getOwners()){
                            if (s.equals(AppUtilities.getLocalUserData(ManageActivity.this).getId())){

                                showcaseList.add(userShowcase);

                            }
                        }
                    }
                }

                if (showcaseList.isEmpty()){

                    loadingDialog.dismiss();
                    refresh.setRefreshing(false);
                    stateView.displayState("no_showcase");

                    return;
                }

                stateView.hideStates();

                adapter.notifyDataSetChanged();

                loadingDialog.dismiss();
                refresh.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                loadingDialog.dismiss();
                refresh.setRefreshing(false);

            }

        });
    }
}
