package com.vividgames.android.gotquiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class QuestionFragment extends Fragment
{
    private static final String ARG_QUESTION_ID = "question_id";
    private static final String ARG_LEVEL_ID="level_id";


    private Question mQuestion;

    private TextView mQuestionTextView;
    private Button mAnswer1_Button;
    private Button mAnswer2_Button;
    private Button mAnswer3_Button;
    private Button mAnswer4_Button;


    public static QuestionFragment newInstance(UUID levelId, int questionIndex)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_QUESTION_ID, questionIndex);
        args.putSerializable(ARG_LEVEL_ID, levelId);
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int mQuestionId=getArguments().getInt(ARG_QUESTION_ID);
        UUID levelId=(UUID)getArguments().getSerializable(ARG_LEVEL_ID);
        Level level=LevelManager.get(getActivity()).getLevel(levelId);
        mQuestion=level.getQuestion(mQuestionId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_question, container, false);

        mQuestionTextView=v.findViewById(R.id.question_text);
        mQuestionTextView.setText(mQuestion.getQuestionText());

        mAnswer1_Button=v.findViewById(R.id.answer1_button);
        mAnswer1_Button.setText(mQuestion.getAnswers()[0]);
        mAnswer1_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAnswer(1);
            }
        });

        mAnswer2_Button=v.findViewById(R.id.answer2_button);
        mAnswer2_Button.setText(mQuestion.getAnswers()[1]);
        mAnswer2_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAnswer(2);
            }
        });

        mAnswer3_Button=v.findViewById(R.id.answer3_button);
        mAnswer3_Button.setText(mQuestion.getAnswers()[2]);
        mAnswer3_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAnswer(3);
            }
        });

        mAnswer4_Button=v.findViewById(R.id.answer4_button);
        mAnswer4_Button.setText(mQuestion.getAnswers()[3]);
        mAnswer4_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAnswer(4);
            }
        });

        return v;
    }

    private void checkAnswer(int answerIndex)
    {
        boolean correct;
        if (answerIndex==mQuestion.getCorrectAnswerIndex())
        {
            correct=true;

        }
        else
        {
            correct=false;
        }
        ((GameplayActivity)getActivity()).onQuestionAnswered(correct);
    }
}
