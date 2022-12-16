package com.gnid.social.pincee.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public abstract class BaseDB extends SQLiteOpenHelper {

    public BaseDB(@Nullable Context context) {
        super(context, null, null, 1);
    }

    @Override
    final public void onCreate(SQLiteDatabase db) {
        onCreateTable(db);
    }

    @Override
    final public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onDropTable(db, oldVersion, newVersion);
    }

    abstract void onCreateTable(SQLiteDatabase db);
    abstract void onDropTable(SQLiteDatabase db, int oldVersion, int newVersion);
}
