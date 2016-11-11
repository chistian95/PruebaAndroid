package com.example.dam32_corral.coches;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dam32-Corral on 20/10/2016.
 *
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE propietarios(" +
                        "dni varchar(9) PRIMARY KEY," +
                        "nombre varchar(20)," +
                        "edad int)"
        );

        db.execSQL(
                "CREATE TABLE coches(" +
                        "matricula varchar(8) PRIMARY KEY," +
                        "marca varchar(20)," +
                        "potencia int," +
                        "dni varchar(9)," +
                        "FOREIGN KEY(dni) REFERENCES propietarios(dni))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
