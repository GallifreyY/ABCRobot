package com.robot.abcrobot.dbmodel;

public class ScreeningQuestionnaire {
    @com.google.gson.annotations.SerializedName("ID")
    private String mID;
    public String getID() {
        return mID;
    }

    @com.google.gson.annotations.SerializedName("ChildID")
    private String mChildID;
    public String getChildID() {
        return mChildID;
    }

    public void Set(String childID){
        mChildID= childID;
    }

    @com.google.gson.annotations.SerializedName("Question")
    private String mQuestion;
    public String getQuestion() {
        return mQuestion;
    }

    public void SetQuestionID(String question) {
        mQuestion = question;
    }

    @com.google.gson.annotations.SerializedName("Result")
    private int mResult;
    public int getResult() {
        return mResult;
    }

    public void setResult(int result) {
        mResult = result;
    }

    public ScreeningQuestionnaire(){}
    public ScreeningQuestionnaire(String childID, String question, int result) {
        mChildID = childID;
        mQuestion = question;
        mResult = result;
    }
}
