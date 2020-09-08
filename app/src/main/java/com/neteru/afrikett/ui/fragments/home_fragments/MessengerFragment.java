package com.neteru.afrikett.ui.fragments.home_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.ChatBoxesAdapter;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxActivity;
import com.neteru.afrikett.ui.activities.messenger_activities.ChatBoxSearchActivity;
import com.neteru.afrikett.ui.activities.messenger_activities.ContactActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;
import static com.neteru.afrikett.core.utilities.Constants.USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessengerFragment extends Fragment {
    private Activity activity;
    private ConstraintLayout baseLayout;
    private DatabaseReference databaseReference;
    private List<MessengerNode> messengerNodes = new ArrayList<>();
    private ChatBoxesAdapter chatBoxesAdapter;
    private PullRefreshLayout swiper;
    private StateView stateView;
    private String id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_messenger, container, false);
        if (getActivity() != null) {
            activity = getActivity();
        }

        // Récupération du bouton flottant
        FloatingActionButton fab = activity.findViewById(R.id.fab);

        fab.setImageResource(R.mipmap.ic_create_white_24dp);

        // Ouverture de la liste de contacts
        fab.setOnClickListener(view -> {

            startActivity(new Intent(getContext(), ContactActivity.class));
            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        // Affichage du bouton flottant
        if (fab.getVisibility() != View.VISIBLE) {
            fab.setVisibility(View.VISIBLE);
            fab.setAnimation(getFadeInAnimation(getContext()));
        }

        // StateView
        stateView = root.findViewById(R.id.status_page);

        // Reference à la racine de la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        // Identifiant de l'utilisateur
        id = getLocalUserData(getContext()).getId();

        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        baseLayout = root.findViewById(R.id.fragment_messenger_base);
        swiper = root.findViewById(R.id.refresh);

        // Recharge des noeuds de discussion
        swiper.setOnRefreshListener(this::getChatBoxes);

        stateView.setOnStateButtonClicked(v -> {

            startActivity(new Intent(getContext(), ContactActivity.class));
            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Instanciation de l'adapteur
        chatBoxesAdapter = new ChatBoxesAdapter(getContext(), messengerNodes, R.layout.template_chatboxes, (targetId, isShowcase) -> {

            startActivity(new Intent(getContext(), ChatBoxActivity.class)
                    .putExtra("targetId", targetId)
                    .putExtra("captureBack", false)
                    .putExtra("targetType", isShowcase ? SHOWCASE : USER));

            activity.overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        recyclerView.setHasFixedSize(true);

        // Attachement de l'adapteur au recyclerView
        recyclerView.setAdapter(chatBoxesAdapter);

        // Récupération des boîtes de discussion
        getChatBoxes();

        activity.findViewById(R.id.toolbar_to_messenger_search).setOnClickListener(v -> {
            if (messengerNodes != null && !messengerNodes.isEmpty()){

                Intent mIntent = new Intent(getContext(), ChatBoxSearchActivity.class);
                // Envoi des données sérialisées
                mIntent.putExtra("messengerNodes", (Serializable) messengerNodes);
                startActivity(mIntent);

                activity.overridePendingTransition(0, 0);
            }
        });

        return root;
    }

    /**
     * Récupération des boîtes de discussion
     */
    private void getChatBoxes(){

        stateView.displayLoadingState();

        databaseReference
                .child("messengers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // S'il n'y a aucune donnée
                        if (dataSnapshot.getValue() == null){

                            changeMessengerViewState(false);

                            return;
                        }

                        // Instanciation de la liste temporaire
                        List<MessengerNode> messengerTempNodes = new ArrayList<>();

                        // Purge de la liste de noeuds
                        messengerNodes.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            MessengerNode node = snapshot.getValue(MessengerNode.class);

                            if (node != null){  // Si le noeud existe
                                if (node.getChats() != null){   // Si la boîte contient des conversations

                                    // Si l'utilisateur courant est un des intervenants de la boîte de discussion
                                    if (id.equals(node.getFirstId()) || id.equals(node.getSecondId())){

                                        messengerTempNodes.add(node);
                                    }

                                }
                            }
                        }

                        /* Arrangement des boîtes de conversation */
                        // Noeuds avec message en suspens
                        for (MessengerNode messengerNode: messengerTempNodes){
                            if (messengerNode.getPendingMsg()){
                                messengerNodes.add(messengerNode);
                            }
                        }

                        // Noeud sans message en suspens
                        for (MessengerNode messengerNode: messengerTempNodes){
                            if (!messengerNode.getPendingMsg()){
                                messengerNodes.add(messengerNode);
                            }
                        }

                        if (messengerNodes.isEmpty()){

                            changeMessengerViewState(false);

                            return;
                        }

                        changeMessengerViewState(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        stateView.hideStates();
                        swiper.setRefreshing(false);

                    }
                });
    }

    private void changeMessengerViewState(boolean state){
        if (state){
            stateView.hideStates();
            baseLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }else {
            stateView.displayState("welcome_messenger");
            baseLayout.setBackgroundColor(activity.getResources().getColor(R.color.whitesmoke));
        }

        chatBoxesAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
    }

}