package com.neteru.afrikett.core.utilities;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;

import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class AccessHandler {
    private Context context;
    private String versionName;
    private Integer versionCode;
    private DatabaseReference databaseReference;
    private DatabaseReference userDbReference;

    private AccessHandler(Context context){

        this.context = context;

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
        userDbReference = databaseReference.child("users").child(getLocalUserData(context).getId());

        versionName = context.getResources().getString(R.string.app_version_name);
        versionCode = context.getResources().getInteger(R.integer.app_version_code);
    }

    public static AccessHandler getInstance(Context context){
        return new AccessHandler(context);
    }

    public void execute(){
        updateUserAppVersion();
    }

    private void updateUserAppVersion(){
        userDbReference.child("appVersionName").setValue(versionName);
        userDbReference.child("appVersionCode").setValue(versionCode);

        checkUserAccess();
    }

    private void checkUserAccess(){

        userDbReference
                .child("pass")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        Boolean pass = dataSnapshot.getValue(Boolean.class);

                        if (pass == null) return;

                        if (pass){

                            checkForObsolescence();

                        }else {
                            showDialog(context.getString(R.string.blocked_account), context.getString(R.string.access_to_your_account_is_restricted));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void checkForObsolescence(){

        databaseReference
                .child("LAST-SUPPORTED-VERSION")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        Integer version = dataSnapshot.getValue(Integer.class);

                        if (version == null) return;

                        if (versionCode < version){
                            showDialog(context.getString(R.string.outdated_version), context.getString(R.string.this_version_is_outdated));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void showDialog(String title, String msg){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setPositiveButton(R.string.ok, (dialogInterface, i) -> {

            Activity activity = (Activity) context;
            activity.finish();

        });
        alertDialog.show();

    }
}
