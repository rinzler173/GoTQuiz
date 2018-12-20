package com.vividgames.android.gotquiz.databases;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.vividgames.android.gotquiz.Question;

public class QuestionCursorWrapper extends CursorWrapper

{
    public QuestionCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Question getQuestion()
    {
        int QuestionId=getInt(getColumnIndex("_id"));
        String QuestionText=getString(getColumnIndex("Question"));
        String PossibleAnswers[]=new String[4];
        for (int i=0; i<=3; i++)
        {
            PossibleAnswers[i]=getString(getColumnIndex("Answer"+(i+1)));
        }
        int CorrectAnswerIndex=getInt(getColumnIndex("CorrectAnswer"));

        Question question=new Question(QuestionText, PossibleAnswers, CorrectAnswerIndex, QuestionId);

        return question;
    }
}
