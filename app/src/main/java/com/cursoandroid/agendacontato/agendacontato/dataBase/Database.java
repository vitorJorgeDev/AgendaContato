package com.cursoandroid.agendacontato.agendacontato.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vitor on 24/02/17.
 */

public class Database extends SQLiteOpenHelper{

    public Database(Context context){
        super(context, "AGENDA", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ScriptSQL.getCreateContato());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

