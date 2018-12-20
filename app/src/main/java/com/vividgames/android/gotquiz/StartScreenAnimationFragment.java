package com.vividgames.android.gotquiz;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StartScreenAnimationFragment extends Fragment
{
    TextView mStartScreenText;
    ConstraintLayout mStartScreen;


    public static StartScreenAnimationFragment newInstance()
    {
        return new StartScreenAnimationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_start_screen_animation, container, false);
        mStartScreenText=v.findViewById(R.id.start_screen_text);
        startAnimation();
        mStartScreen=v.findViewById(R.id.start_screen);
        mStartScreen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=MainMenuActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });


        return v;
    }

    private void startAnimation()
    {
        ObjectAnimator pulseAnimator=ObjectAnimator
                .ofFloat(mStartScreenText, "textSize", 20.0f, 25.0f)
                .setDuration(1000);
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        pulseAnimator.start();
    }

}
