package com.vividgames.android.gotquiz;

public class Question
{
    private String mQuestionText;
    private String[] mAnswers;
    private int mCorrectAnswerIndex;
    private int mId;

    public Question(String questionText, String[] answers, int correctAnswerIndex, int id)
    {
        mQuestionText = questionText;
        mAnswers = answers;
        mCorrectAnswerIndex=correctAnswerIndex;
        mId=id;
    }

    public int getId()
    {
        return mId;
    }

    public String[] getAnswers()
    {
        return mAnswers;
    }

    public String getQuestionText()
    {
        return mQuestionText;
    }

    public int getCorrectAnswerIndex()
    {
        return mCorrectAnswerIndex;
    }
}
