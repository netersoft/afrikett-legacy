package com.neteru.afrikett.ui.activities.messenger_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.ChatBoxesAdapter;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.SHOWCASE;
import static com.neteru.afrikett.core.utilities.Constants.USER;

public class ChatBoxSearchActivity extends AppCompatActivity {
    private List<MessengerNode> messengerNodes;
    private RecyclerView recyclerView;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_search);

        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        messengerNodes = (List<MessengerNode>) getIntent().getSerializableExtra("messengerNodes");

        setMessengerNodes(messengerNodes);

        if (getSupportActionBar() != null){
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

    private void setMessengerNodes(List<MessengerNode> messengerNodes){
        // Instanciation de l'adapteur
        ChatBoxesAdapter chatBoxesAdapter = new ChatBoxesAdapter(this, messengerNodes, R.layout.template_chatboxes, (targetId, isShowcase) -> {

            startActivity(new Intent(this, ChatBoxActivity.class)
                    .putExtra("targetId", targetId)
                    .putExtra("captureBack", false)
                    .putExtra("targetType", isShowcase ? SHOWCASE : USER));

            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

        });

        // Attachement de l'adapteur au recyclerView
        recyclerView.setAdapter(chatBoxesAdapter);
        chatBoxesAdapter.notifyDataSetChanged();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        // Configuration du SearchView
        final SearchView search = (SearchView) mSearch.getActionView();
        search.setQueryHint(getString(R.string.search_chatbox_hint));
        search.setIconified(false);
        search.setIconifiedByDefault(true);
        search.setBackgroundColor(getResources().getColor(R.color.whitesmoke));

        // Customisation de la couleur du hint
        SearchView.SearchAutoComplete searchAutoComplete = search.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.dimgray));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.black));

        // Customisation du bouton de fermeture
        final ImageView searchCloseButton = search.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_blue_24dp));

        // Customisation de l'icône de recherche
        ImageView searchButton = search.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search_blue_24dp));

        // Désactivation du bouton de fermeture
        search.setOnCloseListener(() -> {

            if (search.getQuery().toString().isEmpty()){

                finish();

            }else {
                setMessengerNodes(messengerNodes);
            }

            return true;
        });

        // Observateur du champ de recherche
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // A chaque changement de texte recharger la liste courante des contacts
                List<MessengerNode> searchMessengerNodes = new ArrayList<>();

                if (messengerNodes != null){ // Si la liste de contact n'est pas vide
                    for (MessengerNode m: messengerNodes){
                        // Nom de la cible
                        String targetName = m.getFirstId().equals(getLocalUserData(ChatBoxSearchActivity.this).getId())
                                ? m.getSecondName()
                                : m.getFirstName();

                        if (targetName.toLowerCase().contains(newText.toLowerCase())
                                || m.getLastMsg().toLowerCase().contains(newText.toLowerCase())){

                            // Rassembler les contacts correspondant à la recherche
                            searchMessengerNodes.add(m);
                        }
                    }

                    // Ensuite rappeler l'adapteur de la liste de contact en lui passant la nouvelle liste correspondant à la recherche
                    setMessengerNodes(searchMessengerNodes);

                }
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        overridePendingTransition(0, 0);

        return true;
    }
}
