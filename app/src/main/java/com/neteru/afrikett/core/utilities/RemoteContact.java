package com.neteru.afrikett.core.utilities;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.core.interfaces.RemoteContactListener;
import com.neteru.afrikett.core.models.RemoteDB.LocalContactModel;
import com.neteru.afrikett.core.models.RemoteDB.RemoteContactModel;
import com.neteru.afrikett.core.models.RemoteDB.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

/**
 * Manager des contacts distants
 */
public class RemoteContact {
    private Activity activity;
    private DatabaseReference databaseReference;
    private List<RemoteContactModel> remoteContactList;
    private RemoteContactListener listener;
    private static String TAG = "REMOTE";

    private RemoteContact(Activity activity){
        this.activity = activity;

        this.databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
    }

    /**
     * Générateur d'instance
     * @param a / Activité
     * @return instance de RemoteContact
     */
    public static RemoteContact getInstance(Activity a){
        return new RemoteContact(a);
    }

    /**
     * Synchronisation des contacts
     * @param localList / liste de contacts locaux
     * @param l / interface de notifications
     */
    public void setRemoteContact(final List<LocalContactModel> localList, RemoteContactListener l){
        this.listener = l;

        // Récupération de la liste des utilisateurs
        this.databaseReference
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<User> userList = new ArrayList<>();
                remoteContactList = new ArrayList<>();

                if (dataSnapshot.getValue() != null){

                    // Notification : fin de récupération de la liste des utilisateurs
                    listener.onGetUsersCompleted();

                    // Récupération de la liste d'utilisateurs excepté l'utilisateur courant
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);

                        if (user != null && !user.getId().equals(getLocalUserData(activity).getId()))
                                                { userList.add(user); }
                    }

                    // Tri sélectif de la liste d'utilisateurs
                    // Croisement de la liste d'utilisateurs avec la liste des contacts
                    for (User u: userList){
                        for (LocalContactModel localContact: localList){
                            String firstVariant = "+"+getLocalUserData(activity).getCountryCode()+localContact.getPhoneNumber(),
                                    secondVariant = "00"+getLocalUserData(activity).getCountryCode()+localContact.getPhoneNumber();

                            if (localContact.getPhoneNumber().equals(u.getNationalNumber())
                                    || firstVariant.equals(u.getNationalNumber())
                                    || secondVariant.equals(u.getNationalNumber())){

                                remoteContactList.add(new RemoteContactModel(u.getId(), localContact.getName(), u.getNationalNumber()));

                            }
                        }
                    }

                    if (remoteContactList != null && !remoteContactList.isEmpty()){

                        // Notification : fin du tri sélectif
                        listener.onGetRemoteContactCompleted(remoteContactList);

                        // Enregistrement de la liste de contacts distants
                        writeRemoteContact();

                    }else {
                        listener.onGetAnything();
                    }

                }else {

                    // Notification : Echec de tâche
                    listener.onTaskFailure();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());

                // Notification : Echec de tâche
                listener.onTaskFailure();
            }
        });
    }

    /**
     * Enregistrement de la liste de contacts distants
     */
    private void writeRemoteContact(){

        HashMap<String, RemoteContactModel> data = new HashMap<>();

        for (RemoteContactModel remoteContact: remoteContactList){
            data.put(remoteContact.getId(), remoteContact);
        }

        this.databaseReference
            .child("contacts")
            .child(getLocalUserData(activity).getId())
            .setValue(data)
            .addOnSuccessListener(aVoid -> {

                // Notification : Fin de tâche
                listener.onTaskCompleted();
            });

    }
}
