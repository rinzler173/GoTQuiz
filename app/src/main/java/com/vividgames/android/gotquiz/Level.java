package com.vividgames.android.gotquiz;

import java.util.List;
import java.util.UUID;

public class Level
{
    private String mLevelName;
    private int mLevelStatus;
    private int mLevelType;
    private UUID mId;
    private List<Question> mQuestions;

    public Level(String levelName, int levelStatus, int levelType)
    {
        mLevelName = levelName;
        mLevelStatus = levelStatus;
        mLevelType=levelType;
        mId=UUID.randomUUID();
    }

    public String getLevelName()
    {
        return mLevelName;
    }

    public int getLevelStatus()
    {
        return mLevelStatus;
    }

    public void setLevelStatus(int levelStatus)
    {
        mLevelStatus = levelStatus;
    }

    public void setQuestions(List<Question> questions)
    {
        mQuestions = questions;
    }

    public int getLevelType()
    {
        return mLevelType;
    }

    public UUID getId()
    {
        return mId;
    }

    public Question getQuestion(int index)
    {
        return mQuestions.get(index);
    }

    public List<Question> getQuestions()
    {
        return mQuestions;
    }
}
