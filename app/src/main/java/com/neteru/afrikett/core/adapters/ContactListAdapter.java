package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.RemoteContactModel;
import com.neteru.afrikett.core.models.RemoteDB.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
    private List<RemoteContactModel> remoteContactList;
    private DatabaseReference databaseReference;
    private Context context;
    private int rowLayout;
    private boolean toChatBox;
    private ContactListAdapterListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView contactBio, phoneNumber, contactName;
        private CardView contactBox;
        private ImageView profile;

        MyViewHolder(View view){
            super(view);

            profile = view.findViewById(R.id.contactImg);
            contactBox = view.findViewById(R.id.contactBox);
            contactName = view.findViewById(R.id.contactName);
            contactBio = view.findViewById(R.id.contactBio);
            phoneNumber = view.findViewById(R.id.contactPhoneNumber);
        }
    }

    public ContactListAdapter(Context ctx, List<RemoteContactModel> list, int row, ContactListAdapterListener l, boolean toChatBox){
        this.context = ctx;
        this.remoteContactList = list;
        this.rowLayout = row;
        this.listener = l;
        this.toChatBox = toChatBox;

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent,false));

        myViewHolder.contactBox.setOnClickListener(view -> {

            if (toChatBox) {

                setNode(remoteContactList.get(position).getId());

            }else {

                listener.onContactSelected(remoteContactList.get(position).getId());

            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final RemoteContactModel remoteContact = remoteContactList.get(position);

        holder.contactName.setText(remoteContact.getName());
        holder.phoneNumber.setText(remoteContact.getPhoneNumber());

        // Chargement photo de profil du contact
        databaseReference
                .child("users")
                .child(remoteContact.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){

                            User targetUser = dataSnapshot.getValue(User.class);
                            if (targetUser == null){
                                return;
                            }

                            // Chargement de la bio
                            holder.contactBio.setText(targetUser.getBio());
                            holder.contactBio.setTextColor(Color.DKGRAY);

                            // Chargement de la photo de profil
                            String profile = targetUser.getProfileUrl();
                            if (profile != null) {
                                if (profile.equals(DEFAULT)) {

                                    holder.profile.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(remoteContact.getName()), COLORS[getDigitFromString(remoteContact.getName())]));

                                } else {

                                    Glide
                                            .with(context)
                                            .load(profile)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(holder.profile);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return remoteContactList.size();
    }

    /**
     * Création du noeud de discussion
     * @param targetId / Identifiant de l'interlocuteur
     */
    private void setNode(final String targetId){

        // Génération du noeud de discussion
        final List<String> list = new ArrayList<>();
        list.add(getLocalUserData(context).getId());
        list.add(targetId);
        Collections.sort(list);

        // Vérification du noeud
        databaseReference
                .child("messengers")
                .child(list.get(0) + "&" + list.get(1))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null){ // S'il existe

                            // Ouverture du box
                            listener.onContactSelected(targetId);

                        }else { // Sinon

                            getFirstName(list.get(0), list.get(1));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Recupère le nom de contact correspondant à la position 0 dans le noeud
     * @param id_0 / Identifiant à la position 0
     * @param id_1 / Identifiant à la position 1
     */
    private void getFirstName(final String id_0, final String id_1){

        databaseReference
                .child("contacts")
                .child(id_1)
                .child(id_0)
                .child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Si le nom de contact existe
                        if (dataSnapshot.getValue() != null){

                            String name_0 = dataSnapshot.getValue(String.class);
                            getSecondName(id_0, id_1, name_0);

                        }else{ // Sinon

                            // On utilise le numéro de téléphone
                            databaseReference
                                    .child("users")
                                    .child(id_0)
                                    .child("phoneNumber")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null){

                                                String name_0 = dataSnapshot.getValue(String.class);
                                                getSecondName(id_0, id_1, name_0);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Recupère le nom de contact correspondant à la position 1 dans le noeud
     * @param id_0 / Identifiant à la position 0
     * @param id_1 / Identifiant à la position 1
     * @param name_0 / Nom correspondant à la position 0
     */
    private void getSecondName(final String id_0, final String id_1, final String name_0){

        databaseReference
                .child("contacts")
                .child(id_0)
                .child(id_1)
                .child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Si le nom de contact existe
                        if (dataSnapshot.getValue() != null){

                            String name_1 = dataSnapshot.getValue(String.class);
                            writeNode(id_0, id_1, name_0, name_1);

                        }else{ // Sinon

                            // On utilise le numéro de téléphone correspondant
                            databaseReference
                                    .child("users")
                                    .child(id_1)
                                    .child("phoneNumber")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null){

                                                String name_1 = dataSnapshot.getValue(String.class);
                                                writeNode(id_0, id_1, name_0, name_1);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Créateur de noeud
     * @param id_0 / Identifiant à la position 0
     * @param id_1 / Identifiant à la position 1
     * @param name_0 / Nom correspondant à la position 0
     * @param name_1 / Nom correspondant à la position 1
     */
    private void writeNode(String id_0, String id_1, String name_0, String name_1){

        // Noeud final
        final MessengerNode node = new MessengerNode(id_0, id_1, name_0, name_1, false);

        // Identifiant de la cible
        final String targetId = id_0.equals(getLocalUserData(context).getId()) ? id_1 : id_0;

        // Création du noeud avant ouverture du box
        databaseReference
                .child("messengers")
                .child(id_0 + "&" + id_1)
                .setValue(node)
                .addOnSuccessListener(aVoid -> listener.onContactSelected(targetId));
    }

    public interface ContactListAdapterListener{
        void onContactSelected(String targetId);
    }

}
