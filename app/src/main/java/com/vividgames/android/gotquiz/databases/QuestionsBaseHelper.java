package com.vividgames.android.gotquiz.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class QuestionsBaseHelper extends SQLiteOpenHelper
{
    private static String DB_PATH="/data/data/com.vividgames.android.gotquiz/databases/";
    private static String DB_NAME="Questions.db";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public QuestionsBaseHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
        myContext=context;
    }

    //creates database in system folder, from where it can be accessed
    //(provided it hasn't been done already)
    public void createDataBase() throws IOException
    {
        boolean dbExists=checkDataBase();

        if (dbExists==false)
        {
            this.getReadableDatabase();

            try
            {
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    public void openDataBase() throws SQLException
    {
        String myPath=DB_PATH+DB_NAME;
        myDataBase=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close()
    {
        if(myDataBase!=null)
        {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVresion, int newVersion)
    {

    }

    public QuestionCursorWrapper queryQuestions(String table , String whereClause, String[] whereArgs)
    {
        Cursor cursor=myDataBase.query(
                table,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new QuestionCursorWrapper(cursor);
    }

    //checks if database already exists to avoid unnecessary re-copying
    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB=null;

        try
        {
            String myPath=DB_PATH+DB_NAME;
            checkDB=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e)
        {
            //database doesn't exist
        }
        if (checkDB!=null)
        {
            checkDB.close();
            return true;
        }
        else
        {
            return false;
        }
    }

    //creates empty database in system folder and overwrites it with data from Questions.db in assets folder
    private void copyDataBase() throws IOException
    {
        InputStream myInput=myContext.getAssets().open(DB_NAME);
        String outFileName=DB_PATH+DB_NAME;
        OutputStream myOutput=new FileOutputStream(outFileName);
        byte[] buffer=new byte[1024];
        int length;

        while ((length=myInput.read(buffer))>0)
        {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

}
