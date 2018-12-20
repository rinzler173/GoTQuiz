package com.vividgames.android.gotquiz;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class LevelViewModel extends BaseObservable
{
    private Level mLevel;
    private LevelManager mLevelManager;
    private LevelMenuFragment mLevelMenuFragment;

    public LevelViewModel(LevelManager levelManager, LevelMenuFragment levelMenuFragment)
    {
        mLevelManager=levelManager;
        mLevelMenuFragment=levelMenuFragment;
    }

    @Bindable
    public String getTitle()
    {
        return mLevel.getLevelName();
    }

    @Bindable
    public String getStatus()
    {
        return mLevelManager.getLevelStatusString(mLevel);
    }

    @Bindable
    public boolean isPlayable()
    {

        if (mLevel.getLevelStatus()==LevelManager.LevelsSchema.StatusCodes.LEVEL_LOCKED)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public void setLevel(Level level)
    {
        mLevel = level;
        notifyChange();
    }

    public void onButtonClicked()
    {
        mLevelMenuFragment.startLevel(mLevel);
    }
}
