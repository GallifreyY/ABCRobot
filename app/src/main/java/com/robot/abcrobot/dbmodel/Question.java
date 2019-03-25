package com.robot.abcrobot.dbmodel;

public class Question {
    @com.google.gson.annotations.SerializedName("ID")
    private String mID;

    public String getID() {
        return mID;
    }
    @com.google.gson.annotations.SerializedName("Title")
    private String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public int selectedAnswer;
    public int scoreLevel;

    public Question(String title) {
        mTitle = title;
        selectedAnswer = -1;
    }
}
