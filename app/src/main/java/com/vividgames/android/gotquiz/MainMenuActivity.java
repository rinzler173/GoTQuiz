package com.vividgames.android.gotquiz;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;

public class MainMenuActivity extends SingleFragmentActivity
{
    private MediaPlayer mMenuSoundPlayer;
    private boolean mPlaySound;

    public static Intent newIntent(Context packageContext)
    {
        Intent intent=new Intent(packageContext, MainMenuActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment()
    {
        return LevelMenuFragment.newInstance();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPlaySound=QueryPreferences.isSoundPlayed(this);
    }

    protected void playSound(int soundId)
    {
        mMenuSoundPlayer =MediaPlayer.create(this, soundId);
        mMenuSoundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                if(mPlaySound)
                {
                    mMenuSoundPlayer.start();
                }
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mMenuSoundPlayer !=null)
        {
            mMenuSoundPlayer.release();
        }
    }

}
