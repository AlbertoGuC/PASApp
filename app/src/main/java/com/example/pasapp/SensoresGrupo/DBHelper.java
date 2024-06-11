package com.example.pasapp.SensoresGrupo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pas.db";
    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TablaDatosContract.TablaEntry.TABLE_NAME + " (" +
                TablaDatosContract.TablaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TablaDatosContract.TablaEntry.COLUMN_SENSOR + " TEXT NOT NULL, " +
                TablaDatosContract.TablaEntry.COLUMN_MEDIDA1 + " REAL NOT NULL, " +
                TablaDatosContract.TablaEntry.COLUMN_MEDIDA2 + " REAL, " +
                TablaDatosContract.TablaEntry.COLUMN_MEDIDA3 + " REAL );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TablaDatosContract.TablaEntry.TABLE_NAME);
        onCreate(db);
    }
}
