package com.vividgames.android.gotquiz;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vividgames.android.gotquiz.databases.ProgressBaseHelper;
import com.vividgames.android.gotquiz.databases.QuestionCursorWrapper;
import com.vividgames.android.gotquiz.databases.QuestionsBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.vividgames.android.gotquiz.databases.ProgressDbSchema.*;

public class LevelManager
{
    public static final class LevelsSchema
    {
        public static final class SmallLevel
        {
            public static final int QUANTITY=8;
            public static final int TYPE_CODE=1;
            public static final int QUESTIONS_PER_LEVEL=5;
        }

        public static final class MediumLevel
        {
            public static final int QUANTITY=4;
            public static final int TYPE_CODE=2;
            public static final int QUESTIONS_PER_LEVEL=10;
        }

        public static final class LargeLevel
        {
            public static final int QUANTITY=1;
            public static final int TYPE_CODE=3;
            public static final int QUESTIONS_PER_LEVEL=15;
        }

        public static final class StatusCodes
        {
            public static final int LEVEL_LOCKED=0;
            public static final int LEVEL_UNLOCKED=1;
            public static final int LEVEL_COMPLETED=2;
        }
    }

    private List<Level> mLevels=new ArrayList<>();
    private QuestionsBaseHelper mQuestionsBaseHelper;
    private SQLiteDatabase mProgressDataBase;
    private static LevelManager sLevelManager;
    private Context mContext;

    //initializes model layer (list of levels) and provides functions to read and modify it
    //uses Questions.db to set levels questions and Progress.db to save user progress (sic!)
    private LevelManager(Context context)
    {
        //creating list of level models according to LevelsSchema
        for (int i=1; i<=LevelsSchema.SmallLevel.QUANTITY; i++)
        {
            mLevels.add(new Level("Level "+i, i==1 ? LevelsSchema.StatusCodes.LEVEL_UNLOCKED : LevelsSchema.StatusCodes.LEVEL_LOCKED, LevelsSchema.SmallLevel.TYPE_CODE));
        }

        for (int i=1; i<=LevelsSchema.MediumLevel.QUANTITY; i++)
        {
            mLevels.add(new Level("Level "+(i+LevelsSchema.SmallLevel.QUANTITY), LevelsSchema.StatusCodes.LEVEL_LOCKED, LevelsSchema.MediumLevel.TYPE_CODE));
        }

        for (int i=1; i<=LevelsSchema.LargeLevel.QUANTITY; i++)
        {
            mLevels.add(new Level("Level "+(i+LevelsSchema.SmallLevel.QUANTITY+LevelsSchema.MediumLevel.QUANTITY), LevelsSchema.StatusCodes.LEVEL_LOCKED, LevelsSchema.LargeLevel.TYPE_CODE));
        }

        //creating database for saving progress and database for questions
        //using progress db to load save
        mContext=context;
        mProgressDataBase=new ProgressBaseHelper(mContext.getApplicationContext()).getWritableDatabase();
        syncProgress();
        mQuestionsBaseHelper=new QuestionsBaseHelper(mContext);
        try
        {
            mQuestionsBaseHelper.createDataBase();
        }
        catch (Exception e)
        {
            Log.d("Questions Db", "Failed to create Database");
        }
        try
        {
            mQuestionsBaseHelper.openDataBase();
        }
        catch (Exception e)
        {
            Log.d("Questions Db", "Failed to open Database");
        }

    }

    public static LevelManager get(Context context)
    {
        if (sLevelManager==null)
        {
            sLevelManager=new LevelManager(context);
        }
        return sLevelManager;
    }

    public List<Level> getLevels()
    {
        return mLevels;
    }
    public Level getLevel(UUID id)
    {
        for (int i=0; i<mLevels.size(); i++)
        {
            if (mLevels.get(i).getId().equals(id))
            {
                return mLevels.get(i);
            }
        }
        return null;
    }

    public String getLevelStatusString(Level level)
    {
        Resources resources=mContext.getResources();
        String string;

        switch (level.getLevelStatus())
        {
            case LevelsSchema.StatusCodes.LEVEL_LOCKED : string=resources.getString(R.string.locked_status);
            break;
            case LevelsSchema.StatusCodes.LEVEL_UNLOCKED : string=resources.getString(R.string.unlocked_status);
            break;
            case LevelsSchema.StatusCodes.LEVEL_COMPLETED : string=resources.getString(R.string.completed_status);
            break;

            default: string=resources.getString(R.string.locked_status);
        }
        return string;
    }

    public void setQuestions(Level level)
    {

        List<Question> questions;
        int questionsToSet;

        switch (level.getLevelType())
        {
            case LevelsSchema.SmallLevel.TYPE_CODE : questionsToSet=LevelsSchema.SmallLevel.QUESTIONS_PER_LEVEL;
                break;
            case LevelsSchema.MediumLevel.TYPE_CODE : questionsToSet=LevelsSchema.MediumLevel.QUESTIONS_PER_LEVEL;
                break;
            case LevelsSchema.LargeLevel.TYPE_CODE : questionsToSet=LevelsSchema.LargeLevel.QUESTIONS_PER_LEVEL ;
                break;
            default : questionsToSet=LevelsSchema.SmallLevel.QUESTIONS_PER_LEVEL;
        }

        questions=new ArrayList<>();
        String questionsTableName=level.getLevelName().replaceAll("\\s","");
        for (int i=1; i<=questionsToSet; i++)
        {
            QuestionCursorWrapper questionCursorWrapper=mQuestionsBaseHelper.queryQuestions(questionsTableName, "_id = ?", new String[]{Integer.toString(i)});
            try
            {
                if (questionCursorWrapper.getCount()!=0)
                {
                    questionCursorWrapper.moveToFirst();
                    questions.add(questionCursorWrapper.getQuestion());
                }
            }
            finally
            {
                questionCursorWrapper.close();
            }
        }
        level.setQuestions(questions);
    }

    //changes status of given level to completed and status of next level (if exists) to unlocked
    //returns true if completed level was the last one and game is over
    public boolean onLevelCompleted(UUID completedLvlId)
    {
        for (int i=0; i<mLevels.size(); i++)
        {
            if (mLevels.get(i).getId().equals(completedLvlId))
            {
                updateDbLevelStatus(getLevel(completedLvlId).getLevelName(), LevelsSchema.StatusCodes.LEVEL_COMPLETED);
                syncProgress(completedLvlId);
                if(i+1<mLevels.size())
                {
                    updateDbLevelStatus(mLevels.get(mLevels.indexOf(getLevel(completedLvlId))+1).getLevelName(), LevelsSchema.StatusCodes.LEVEL_UNLOCKED);
                    syncProgress(mLevels.get(mLevels.indexOf(getLevel(completedLvlId))+1).getId());
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        return false;
    }

    //saves users progress to database, called after completing a level
    private void updateDbLevelStatus(String levelName, int statusCode)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(ProgressTable.Cols.STATUS, statusCode);
        mProgressDataBase.update(ProgressTable.NAME, contentValues, ProgressTable.Cols.NAME+" = ?",new String[] {levelName});
    }

    //reads progress from database and updates model layer for particular level (after user makes progress)
    private void syncProgress(UUID levelId)
    {
        // extracting level status from db
        String nameInDb=getLevel(levelId).getLevelName();
        int status=LevelsSchema.StatusCodes.LEVEL_LOCKED;
        Cursor cursor=mProgressDataBase.query(ProgressTable.NAME, new String[] {ProgressTable.Cols.STATUS},
                ProgressTable.Cols.NAME+" = ?", new String[] {nameInDb},
                null, null, null);

        try
        {
            if (cursor.getCount()!=0)
            {
                cursor.moveToFirst();
                status=cursor.getInt(cursor.getColumnIndex(ProgressTable.Cols.STATUS));
            }
        }
        finally
        {
            cursor.close();
        }
        getLevel(levelId).setLevelStatus(status);
    }

    //reads progress from database for initializing model layer of all levels (after game is reloaded)
    private void syncProgress()
    {

        Cursor cursor=mProgressDataBase.query(ProgressTable.NAME, null,
                null, null,
                null, null, ProgressTable.Cols.NAME);

        try
        {
            if (cursor.getCount()!=0)
            {
                cursor.moveToFirst();
                for (int i=0; i<mLevels.size(); i++)
                {
                    int status=cursor.getInt(cursor.getColumnIndex(ProgressTable.Cols.STATUS));
                    int id=cursor.getInt(cursor.getColumnIndex("_id"));
                    mLevels.get(id-1).setLevelStatus(status);
                    cursor.moveToNext();
                }
            }
        }
        finally
        {
            cursor.close();
        }
    }
}
