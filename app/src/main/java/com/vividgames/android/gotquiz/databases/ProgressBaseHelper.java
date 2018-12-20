package com.vividgames.android.gotquiz.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vividgames.android.gotquiz.LevelManager.LevelsSchema.*;
import com.vividgames.android.gotquiz.databases.ProgressDbSchema.ProgressTable;


public class ProgressBaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION=1;
    private static final String DATABASE_NAME="progressBase.db";

    public ProgressBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table "+ ProgressTable.NAME+"("+
        "_id integer primary key autoincrement, "+
        ProgressTable.Cols.NAME+", "+
        ProgressTable.Cols.STATUS +")");

        ContentValues values;
        for (int i = 1; i<=SmallLevel.QUANTITY+MediumLevel.QUANTITY+LargeLevel.QUANTITY; i++)
        {
            values=new ContentValues();
            values.put(ProgressTable.Cols.NAME, "Level "+i);
            values.put(ProgressTable.Cols.STATUS, i==1 ? StatusCodes.LEVEL_UNLOCKED : StatusCodes.LEVEL_LOCKED);

            db.insert(ProgressTable.NAME, null, values);
            Log.i("progress", "Level "+i);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
