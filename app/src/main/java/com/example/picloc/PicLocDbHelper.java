package com.example.picloc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PicLocDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PicLocContract.PicLoc.TABLE_NAME + " (" +
                    PicLocContract.PicLoc._ID + " INTEGER PRIMARY KEY," +
                    PicLocContract.PicLoc.COLUMN_NAME_PHOTO + " BLOB," +
                    PicLocContract.PicLoc.COLUMN_NAME_LATITUDE + " DOUBLE," +
                    PicLocContract.PicLoc.COLUMN_NAME_LONGITUDE + " DOUBLE)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PicLocContract.PicLoc.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PicLoc.db";

    public PicLocDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
