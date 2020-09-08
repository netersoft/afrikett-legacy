package com.neteru.afrikett.core.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.neteru.afrikett.core.interfaces.LocalContactListener;
import com.neteru.afrikett.core.models.RemoteDB.LocalContactModel;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.removeCharSequencies;
import static com.neteru.afrikett.core.utilities.AppUtilities.removeStringRedundancies;

/**
 * Manager des contacts locaux
 */
public class LocalContact {

    private Activity activity;
    private LocalContactListener listener;
    private List<String> names, phoneNumbers;
    private List<LocalContactModel> localContactList;

    private LocalContact(Activity act){
        this.activity = act;
    }

    /**
     * Générateur d'instance
     * @param activity / Activité
     * @return instance de LocalContact
     */
    public static LocalContact getInstance(Activity activity){
        return new LocalContact(activity);
    }

    /**
     * Lance la récupération des contacts locaux
     * @param l / interface de notifications
     */
    public void getContacts(LocalContactListener l){

        this.listener = l;
        this.names = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
        this.localContactList = new ArrayList<>();

        // Lancement de la tâche asynchrone de récupération des contacts
        new ContactExtractor().execute();
    }

    /**
     * Extracteur de contacts
     */
    private void extractContacts(){

        // Instanciation de ContentResolver
        ContentResolver cr = activity.getContentResolver();

        // Récupération du curseur contenant les contacts
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) { // Si le nombre de contacts récupérés n'est pas nul

            while (cur.moveToNext()) { // Tant qu'un contact suivant existe

                // On récupère son identifiant
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));

                // Et son nom
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                // Si le contact contient un/des numéro(s) de téléphone
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur != null && pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        // Ajout du nom du contact
                        this.names.add(name);

                        // Formatage et ajout du numéro du contact
                        this.phoneNumbers.add(removeCharSequencies("\\-", phoneNo));

                        // Formatage et ajout de l'objet LocalContact
                        this.localContactList.add(new LocalContactModel(name, removeCharSequencies("\\-", phoneNo)));

                    }

                    if (pCur != null) {
                        pCur.close(); // Fermeture du curseur
                    }
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

    }

    /**
     * Suppresseur de doublons
     * @param input / liste d'entrées
     * @return liste de sorties sans doublons
     */
    private static List<LocalContactModel> removeLocalContactRedundancies(List<LocalContactModel> input){
        List<LocalContactModel> output = new ArrayList<>();

        if (input != null) {
            for (LocalContactModel l : input) {
                if (!output.contains(l)){
                    output.add(l);
                }
            }

            return output;
        }

        return null;
    }

    /**
     * Tâche d'extraction de contacts locaux
     */
    @SuppressLint("StaticFieldLeak")
    class ContactExtractor extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Notification de début de tâche
            listener.onGetContactStart();
        }

        @Override
        protected Void doInBackground(String... strings) {

            // Exécution de tâche
            extractContacts();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Suppression de doublons dans les listes récupérées
            names = removeStringRedundancies(names);
            phoneNumbers = removeStringRedundancies(phoneNumbers);
            localContactList = removeLocalContactRedundancies(localContactList);

            // Notification de fin de tâche avec fourniture des données extraites
            listener.onGetLocalContactCompleted(names, phoneNumbers, localContactList);
        }
    }

}
