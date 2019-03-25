package com.robot.abcrobot.dbmodel;

import java.util.Date;
import java.util.UUID;

public class ChildInfo {
    @com.google.gson.annotations.SerializedName("ID")
    private String mID;
    public String getID() {
        return mID;
    }
    public void setID(String id) {
         mID = id;
    }

    @com.google.gson.annotations.SerializedName("Name")
    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @com.google.gson.annotations.SerializedName("Age")
    private int mAge;

    public Integer getAge() {
        return mAge;
    }

    public void setAge(Integer age) {
        mAge = age;

    }

    @com.google.gson.annotations.SerializedName("Sex")
    private String mSex;

    public String getSex() {
        return mSex;
    }

    public void setSex(String sex) {
        mSex = sex;
    }

    @com.google.gson.annotations.SerializedName("Preparer")
    private String mPreparer;

    public String getPreparer() {
        return mPreparer;
    }

    public void setPreparer(String preparer) {
        mPreparer = preparer;
    }

    @com.google.gson.annotations.SerializedName("AssessedDate")
    private Date mAssessedDate;

    public Date getAssessedDate() {
        return mAssessedDate;
    }

    public void setAssessedDate(Date assessedDate) {
        mAssessedDate = assessedDate;
    }

    @com.google.gson.annotations.SerializedName("AssessedScore")
    private int mAssessedScore;

    public int getAssessedScore() {
        return mAssessedScore;
    }

    public void setAssessedScore(int assessedScore) {
        mAssessedScore = assessedScore;
    }


    public  ChildInfo(){}

}
