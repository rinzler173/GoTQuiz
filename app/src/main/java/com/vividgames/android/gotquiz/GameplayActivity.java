package com.vividgames.android.gotquiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.UUID;


public class GameplayActivity extends AppCompatActivity
{
    private static final String EXTRA_LEVEL_ID="level_id";
    private static final String EXTRA_VICTORY_ACHIEVED="vividgames.android.gotquiz.victory_achieved";
    private static final int CORRECT_ANSWER_SOUND_URI=R.raw.correct_answer;
    private static final int WRONG_ANSWER_SOUND_URI=R.raw.wrong_answer;

    private final Handler mDialogHandler=new Handler();
    final Runnable mRunnable=new Runnable()
    {
        @Override
        public void run()
        {
            if (mAnswerDialog.isShowing())
            {
                mAnswerDialog.dismiss();
            }
        }
    };

    private Dialog mAnswerDialog;
    private CustomViewPager mQuestionPager;
    private Level mCurrentLevel;
    private MediaPlayer mGameplaySoundPlayer;
    private boolean mPlaySound;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAnswerDialog=new Dialog(this);
        final UUID levelId= (UUID) getIntent().getSerializableExtra(EXTRA_LEVEL_ID);
        mCurrentLevel=LevelManager.get(this).getLevel(levelId);

        setContentView(R.layout.activity_gameplay);
        mQuestionPager =findViewById(R.id.question_view_pager);
        FragmentManager fragmentManager=getSupportFragmentManager();
        mQuestionPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {
            @Override
            public Fragment getItem(int position)
            {
                return QuestionFragment.newInstance(levelId, position);
            }

            @Override
            public int getCount()
            {
               return mCurrentLevel.getQuestions().size();
            }
        });
        mPlaySound=QueryPreferences.isSoundPlayed(this);
    }



    public static Intent newIntent(Context packageContext, UUID levelId)
    {
        Intent intent=new Intent(packageContext, GameplayActivity.class);
        intent.putExtra(EXTRA_LEVEL_ID, levelId);
        return intent;
    }

    //shows result pop-up and plays proper sound effect
    //makes sure pop-up is dismissed before creating result intent and finishing activity
    public void onQuestionAnswered(final boolean answerCorrect)
    {
        ConstraintLayout wholePopup;
        if (answerCorrect)
        {
            playSound(CORRECT_ANSWER_SOUND_URI);
            if (mCurrentLevel.getQuestions().size()==mQuestionPager.getCurrentItem()+1)
            {
                mAnswerDialog.setContentView(R.layout.level_completed_popup);
            }
            else
            {
                mAnswerDialog.setContentView(R.layout.correct_answer_popup);
            }
        }
        else
        {
            playSound(WRONG_ANSWER_SOUND_URI);
            mAnswerDialog.setContentView(R.layout.incorrect_answer_popup);
        }
        mAnswerDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mDialogHandler.removeCallbacks(mRunnable);
                if (answerCorrect)
                {
                    if (mCurrentLevel.getQuestions().size()==mQuestionPager.getCurrentItem()+1)
                    {
                        setVictoryAchieved(true);
                    }
                    else
                    {
                        mQuestionPager.setCurrentItem(mQuestionPager.getCurrentItem()+1);
                    }
                }
                else
                {
                    setVictoryAchieved(false);
                }
            }
        });
        wholePopup=mAnswerDialog.findViewById(R.id.answer_popup);
        wholePopup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAnswerDialog.dismiss();
            }
        });
        mAnswerDialog.show();
        mDialogHandler.postDelayed(mRunnable, 1500);
    }

    private void setVictoryAchieved(boolean isVictoryAchieved)
    {
        Intent data=new Intent();
        data.putExtra(EXTRA_VICTORY_ACHIEVED, isVictoryAchieved);
        setResult(RESULT_OK, data);
        finish();
    }

    public static boolean wasVictoryAchieved(Intent result)
    {
        return result.getBooleanExtra(EXTRA_VICTORY_ACHIEVED, false);
    }

    private void playSound(int soundId)
    {
        mGameplaySoundPlayer=MediaPlayer.create(this, soundId);
        mGameplaySoundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                if (mPlaySound)
                {
                    mGameplaySoundPlayer.start();
                }
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mGameplaySoundPlayer!=null && mGameplaySoundPlayer.isPlaying())
        {
            mGameplaySoundPlayer.pause();
            mGameplaySoundPlayer.stop();
            mGameplaySoundPlayer.release();
        }
    }
}
