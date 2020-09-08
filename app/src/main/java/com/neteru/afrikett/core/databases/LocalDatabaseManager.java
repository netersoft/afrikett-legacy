package com.neteru.afrikett.core.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.neteru.afrikett.core.models.LocalDB.Recent;

public class LocalDatabaseManager extends OrmLiteSqliteOpenHelper {

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "afrikett.db";
    private final static String  TAG = "DB_MANAGER";

    public LocalDatabaseManager(Context ctx){
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Recent.class);
        }catch (Exception e){
            Log.e(TAG, "Erreur lors de la création des Tables - "+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Recent.class, true);
        }catch (Exception e){
            Log.e(TAG, "Erreur lors de la mise à jour des Tables - "+e);
        }
    }

}
