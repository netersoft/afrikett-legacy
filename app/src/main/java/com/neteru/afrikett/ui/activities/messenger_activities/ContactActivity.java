package com.neteru.afrikett.ui.activities.messenger_activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.neteru.afrikett.core.interfaces.LocalContactListener;
import com.neteru.afrikett.core.interfaces.RemoteContactListener;
import com.neteru.afrikett.core.libs.StateView.StateView;
import com.neteru.afrikett.core.models.RemoteDB.LocalContactModel;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.RemoteContactModel;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.utilities.LocalContact;
import com.neteru.afrikett.core.utilities.RemoteContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getBooleanPreference;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.AppUtilities.setBooleanPreference;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.MESSENGER_PREFS;

public class ContactActivity extends AfrikettBaseActivity {
    private LocalContact localContact;
    private RemoteContact remoteContact;
    private static String GET_CONTACTS_PREF = "getContacts";
    private DatabaseReference databaseReference;
    private List<RemoteContactModel> remoteContactList = new ArrayList<>();
    private ContactListAdapter contactListAdapter;
    private RecyclerView recyclerView;
    private PullRefreshLayout swiper;
    private LoadingDialog loadingDialog;
    private StateView stateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Instance des contacts locaux
        localContact = LocalContact.getInstance(this);

        // Instance des contacts distants
        remoteContact = RemoteContact.getInstance(this);

        // StateView
        stateView = findViewById(R.id.status_page);

        loadingDialog = new LoadingDialog(this);

        // Reference à la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);

        recyclerView = findViewById(R.id.recycler);
        swiper = findViewById(R.id.refresh);

        // Recharge avec les contacts distants
        swiper.setOnRefreshListener(this::getRemoteContact);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (!getBooleanPreference(this, MESSENGER_PREFS, GET_CONTACTS_PREF, false)){

            // Si les contacts n'ont jamais été synchronisés
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, Constants.REQUEST_READ_PERMISSION);

        }else{

            getRemoteContact();

        }

        if (getSupportActionBar() != null) {
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.contact));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);


        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skyblue));
        }
    }

    /**
     * Recherche de contacts
     * @param menu / Menu de recherche
     * @return booleen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) mSearch.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setIconified(true);
        searchView.setIconifiedByDefault(true);

        // Customisation de l'EditText
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.dimgray));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.black));

        // Customisation du bouton de fermeture
        final ImageView searchCloseButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_blue_24dp));
        searchCloseButton.setEnabled(true);

        // Customisation de l'icône de recherche
        ImageView searchButton = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search_blue_24dp));

        searchView.setOnCloseListener(() -> {

            searchView.setBackgroundColor(getResources().getColor(R.color.white));
            // Recharger les données par défaut lors de la fermeture de la recherche
            getRemoteContact();

            return false;
        });

        searchView.setOnSearchClickListener(v -> searchView.setBackgroundColor(getResources().getColor(R.color.whitesmoke)));

        // Ecouteur d'évènement "Changement de texte"
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // A chaque changement de texte recharger la liste courante des contacts
                List<RemoteContactModel> searchContactList = new ArrayList<>();

                if (remoteContactList != null){ // Si la liste de contact n'est pas vide
                    for (RemoteContactModel r: remoteContactList){
                        if (r.getName().toLowerCase().contains(s.toLowerCase()) || r.getPhoneNumber().toLowerCase().contains(s.toLowerCase())){
                            // Rassembler les contacts correspondant à la recherche
                            searchContactList.add(r);
                        }
                    }

                    // Ensuite rappeler l'adapteur de la liste de contact en lui passant la nouvelle liste correspondant à la recherche
                    contactListAdapter = new ContactListAdapter(ContactActivity.this, searchContactList, R.layout.template_contact_list, targetId -> {

                        startActivity(new Intent(ContactActivity.this, ChatBoxActivity.class)
                                .putExtra("targetId", targetId)
                                .putExtra("captureBack", true)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                    }, true);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(contactListAdapter);
                    contactListAdapter.notifyDataSetChanged();

                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_invite: // Inviter un ami via un message textuel

                sendInvitation();
                break;

            case R.id.action_contact: // Ouvrir la liste des contacts de l'utilisateur

                startActivity(new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI));

                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.action_add: // Ouvrir la page d'ajout de contact de l'utilisateur

                startActivity(new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI));

                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                break;

            case R.id.action_refresh: // Recharger la liste de contacts de l'utilisateur à partir des données locales

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, Constants.REQUEST_READ_PERMISSION);
                break;

            case R.id.action_help: // Aide

                @SuppressLint("InflateParams")
                View textLayout = LayoutInflater.from(this).inflate(R.layout.layout_textview, null);

                TextView textView = textLayout.findViewById(R.id.textView);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    textView.setText(Html.fromHtml(getString(R.string.contact_help), Html.FROM_HTML_MODE_COMPACT));
                }else{
                    textView.setText(Html.fromHtml(getString(R.string.contact_help)));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(getString(R.string.help))
                        .setCancelable(true)
                        .setView(textLayout)
                        .create()
                        .show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_READ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Extraction des contacts locaux...
                localContact.getContacts(new LocalContactListener() {

                    /**
                     * Début d'opération
                     */
                    @Override
                    public void onGetContactStart() {
                        loadingDialog.show();
                        loadingDialog.setMsg(getString(R.string.retrieving_contacts));

                        Log.d("MESSENGER", "START...");
                    }

                    /**
                     * Fin d'opération
                     * @param nameList / liste des noms de contacts
                     * @param phoneNumberList / liste des numéros de contacts
                     * @param localContactList / liste des objets LocalContact
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onGetLocalContactCompleted(@NonNull List<String> nameList, @NonNull List<String> phoneNumberList, @NonNull List<? extends LocalContactModel> localContactList) {

                        // Lancement de l'opération de synchronisation de contact
                        setRemoteContact((List<LocalContactModel>) localContactList);

                        if (loadingDialog.isShowing()) {
                            loadingDialog.setMsg(getString(R.string.synchronization));
                        }

                        // On évite la répétition de l'opération
                        setBooleanPreference(ContactActivity.this, MESSENGER_PREFS, GET_CONTACTS_PREF, true);
                    }
                });

            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Synchronisateur de contact
     * @param list / liste de contacts à synchroniser
     */
    private void setRemoteContact(List<LocalContactModel> list){

        // Synchronisation des contacts...
        remoteContact.setRemoteContact(list, new RemoteContactListener() {

            /**
             * Fin de tâche : Récupération liste des utilisateurs
             */
            @Override
            public void onGetUsersCompleted() { }

            /**
             * Fin de tâche : Préparation de la liste de contacts distants
             * @param list / liste de contacts distants
             */
            @Override
            public void onGetRemoteContactCompleted(@NonNull List<? extends RemoteContactModel> list) {

                // Mise à jour du nom du contact dans les noeuds
                for (final RemoteContactModel rcm: list){

                    databaseReference
                            .child("messengers")
                            .child(getNode(rcm.getId()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null){
                                        return;
                                    }

                                    MessengerNode messengerNode = dataSnapshot.getValue(MessengerNode.class);

                                    if (messengerNode == null){
                                        return;
                                    }

                                    String targetNameKey = messengerNode.getFirstId().equals(getLocalUserData(ContactActivity.this).getId())
                                            ? "secondName"
                                            : "firstName";

                                    databaseReference
                                            .child("messengers")
                                            .child(getNode(rcm.getId()))
                                            .child(targetNameKey)
                                            .setValue(rcm.getName());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

            }

            /**
             * Fin de tâche : Enregistrement des contacts distants
             */
            @Override
            public void onTaskCompleted() {

                if (loadingDialog.isShowing()){ loadingDialog.setMsg(getString(R.string.contacts_loading)); }

                // Récupération des contacts distants
                getRemoteContact();
            }

            /**
             * Liste de contacts vide
             */
            @Override
            public void onGetAnything() {

                loadingDialog.dismiss();
                stateView.displayState("no_contact");
                stateView.setOnStateButtonClicked(v -> sendInvitation());

            }

            /**
             * Echec de tâche
             */
            @Override
            public void onTaskFailure() {

                loadingDialog.dismiss();
                stateView.displayState("error_occurred");
                stateView.setOnStateButtonClicked(v -> ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Constants.REQUEST_READ_PERMISSION));

            }
        });

    }

    /**
     * Générateur de noeud de discussion
     * @return noeud de discussion
     */
    private String getNode(String targetId){
        final List<String> list = new ArrayList<>();
        list.add(getLocalUserData(this).getId());
        list.add(targetId);
        Collections.sort(list);

        return list.get(0) + "&" + list.get(1);
    }

    /**
     * Envoie une invitation
     */
    private void sendInvitation(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_message)+"https://play.google.com/store/apps/details?id="+getPackageName()+"\n\n");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_a_friend)));

        overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

    }

    /**
     * Récupération des contacts distants
     */
    private void getRemoteContact(){

        databaseReference
                .child("contacts")
                .child(getLocalUserData(this).getId())
                .orderByChild("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null){

                            stateView.hideStates();

                            remoteContactList = new ArrayList<>();

                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                RemoteContactModel remoteContact = snapshot.getValue(RemoteContactModel.class);

                                if (remoteContact != null){ remoteContactList.add(remoteContact); }
                            }

                            contactListAdapter = new ContactListAdapter(ContactActivity.this, remoteContactList, R.layout.template_contact_list, targetId -> {

                                startActivity(new Intent(ContactActivity.this, ChatBoxActivity.class)
                                                    .putExtra("targetId", targetId)
                                                    .putExtra("captureBack", true)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                            }, true);
                            recyclerView.setAdapter(contactListAdapter);
                            contactListAdapter.notifyDataSetChanged();

                        }else {

                            stateView.displayState("no_contact");
                            stateView.setOnStateButtonClicked(v -> sendInvitation());

                        }

                        loadingDialog.dismiss();
                        swiper.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        loadingDialog.dismiss();
                        swiper.setRefreshing(false);

                    }
                });

    }
}
