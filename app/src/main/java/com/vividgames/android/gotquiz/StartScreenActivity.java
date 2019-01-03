package com.vividgames.android.gotquiz;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.util.Log;


public class StartScreenActivity extends SingleFragmentActivity
{
    private static final String TAG="StartScreenActivity";
    private MediaPlayer mThemePlayer;
    private boolean mPlaySound;

    @Override
    protected Fragment createFragment()
    {
        return StartScreenAnimationFragment.newInstance();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!mThemePlayer.isPlaying())
        {
            try
            {
                mThemePlayer.prepareAsync();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error preparing mThemePlayer!", e);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mThemePlayer.isPlaying())
        {
            mThemePlayer.pause();
            mThemePlayer.stop();
        }
    }

    @Override
    protected void additionalActions()
    {
        mPlaySound=QueryPreferences.isSoundPlayed(this);
        mThemePlayer =MediaPlayer.create(this, R.raw.start_screen_theme);
        mThemePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                if (mPlaySound)
                {
                    mThemePlayer.start();
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mThemePlayer.release();
    }
}
