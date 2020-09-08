package com.neteru.afrikett.ui.activities.others_activities.ui_utilities;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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
import com.neteru.afrikett.core.adapters.ContactListAdapter;
import com.neteru.afrikett.core.models.RemoteDB.RemoteContactModel;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.libs.StateView.StateView;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class ContactChooserActivity extends AfrikettBaseActivity {
    private PullRefreshLayout refresh;
    private RecyclerView recyclerView;
    private List<RemoteContactModel> remoteContactList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ContactListAdapter contactListAdapter;
    private StateView stateView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_chooser);

        refresh = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        userId = getLocalUserData(this).getId();

        // Reference à la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        // StateView
        stateView = findViewById(R.id.status_page);

        refresh.setOnRefreshListener(this::loadUserContact);

        loadUserContact();

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
            Spannable title = new SpannableString(getString(R.string.choose_contact));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    private void loadUserContact() {

        databaseReference
                .child("contacts")
                .child(userId)
                .orderByChild("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){

                            stateView.displayState("no_contact");
                            refresh.setRefreshing(false);
                            return;
                        }

                        remoteContactList.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            RemoteContactModel remoteContact = snapshot.getValue(RemoteContactModel.class);

                            if (remoteContact != null){ remoteContactList.add(remoteContact); }
                        }

                        if (remoteContactList.isEmpty()){
                            stateView.displayState("no_contact");
                        }else {
                            stateView.hideStates();
                        }

                        contactListAdapter = new ContactListAdapter(ContactChooserActivity.this, remoteContactList, R.layout.template_contact_list, targetId -> {

                            setResult(RESULT_OK, getIntent().putExtra("selectedContact", targetId));

                            finish();
                            overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                        }, false);
                        recyclerView.setAdapter(contactListAdapter);
                        contactListAdapter.notifyDataSetChanged();

                        refresh.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        refresh.setRefreshing(false);
                    }
                });
    }
}
