package com.vividgames.android.gotquiz.databases;

public class ProgressDbSchema
{
    //each record represents different level
    public static final class ProgressTable
    {
        public static final String NAME="progress";
        public static final class Cols
        {
            public static final String NAME="name";
            public static final String STATUS ="status";
        }
    }
}
