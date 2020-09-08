package com.neteru.afrikett.ui.activities.launch_activities.start.phone;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.AppUtilities;
import com.neteru.afrikett.core.utilities.Constants;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.ui.activities.main_activities.HomeActivity;

import static com.neteru.afrikett.core.utilities.AppUtilities.capitalize;
import static com.neteru.afrikett.core.utilities.AppUtilities.setLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.PHONE;

public class InfoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private EditText username, email;
    private String country, countryCode, nationalNumber, number;
    private Button saveBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Instance de l'authentificateur
        mAuth = FirebaseAuth.getInstance();

        //Reference vers la base de données
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("users");

        //Si ces paramètres sont fournis cela suppose qu'on vient direcement du splash
        if (getIntent() != null){
            country = getIntent().getStringExtra("country");
            number = getIntent().getStringExtra("number");
            nationalNumber = getIntent().getStringExtra("nationalNumber");
            countryCode = getIntent().getStringExtra("countryCode");
        }

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        saveBut = findViewById(R.id.save);

        // Etat par défaut du bouton d'enregistrement
        saveBut.setEnabled(false);

        // Observateur champ de texte nom d'utilisateur
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    saveBut.setEnabled(false);
                }else {
                    saveBut.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveBut.setOnClickListener(view -> {

            if (username.getText().toString().isEmpty()) { // Si le nom n'est pas fourni
                return;
            }

            if (!email.getText().toString().isEmpty()){ // On vérifie si l'email l'est aussi

                if (!AppUtilities.validateMail(email.getText().toString().trim())){ //S'il l'est on vérifie sa validité

                    Toast.makeText(InfoActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Enregistrement de l'utilisateur
                setUser();

            }else {

                // Enregistrement de l'utilisateur
                setUser();
            }


        });

    }

    /**
     * Enregistrement des données utilisateur
     */
    public void setUser(){
        if (mAuth.getCurrentUser() != null && mAuth.getUid() != null){ // Si l'utilisateur est authentifié

            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.setMsg(getString(R.string.be_patient));
            loadingDialog.show();

            //On recupère son identifiant
            String id = mAuth.getUid();

            //On sélectionne le constructeur
            final User user = new User(
                    id,
                    capitalize(username.getText().toString().trim()),
                    nationalNumber,
                    countryCode,
                    number,
                    country,
                    email.getText().toString().trim(),
                    PHONE
            );

            // On inscrit les info dans la base
            databaseReference
                    .child(id)
                    .setValue(user)
                    .addOnSuccessListener(aVoid -> {

                        // On inscrit les données dans des variables préférentielles locales
                        setLocalUserData(InfoActivity.this, user);

                        // On redirige vers l'activité principale
                        startActivityForResult(new Intent(InfoActivity.this, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), Constants.RANDOM_VALUE);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    })
                    .addOnFailureListener(Throwable::printStackTrace)
                    .addOnCompleteListener(task -> {
                        if (loadingDialog.isShowing())
                            loadingDialog.dismiss();
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Non support de l'action Retour
        finish();
    }
}
