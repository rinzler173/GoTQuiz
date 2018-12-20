package com.vividgames.android.gotquiz;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.util.Log;


public class StartScreenActivity extends SingleFragmentActivity
{
    private static final String TAG="StartScreenActivity";
    private MediaPlayer themePlayer;

    @Override
    protected Fragment createFragment()
    {
        return StartScreenAnimationFragment.newInstance();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!themePlayer.isPlaying())
        {
            try
            {
                themePlayer.prepareAsync();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error preparing themePlayer!", e);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        themePlayer.pause();
        themePlayer.stop();
    }

    @Override
    protected void additionalActions()
    {
        themePlayer=MediaPlayer.create(this, R.raw.start_screen_theme);
        themePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                themePlayer.start();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        themePlayer.release();
    }
}
