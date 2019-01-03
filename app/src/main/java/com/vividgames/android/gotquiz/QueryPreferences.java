package com.vividgames.android.gotquiz;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences
{
    private static final String PLAY_SOUND="play_sound";

    public static boolean isSoundPlayed(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PLAY_SOUND, true);
    }

    public static void setPlaySound(Context context, boolean play)
    {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PLAY_SOUND, play)
                .apply();
    }
}
