package com.cita.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Diagnoses implements Parcelable {
    private int diagnosesId;
    private int childAge;
    private int weightChild;
    private int heightCild;
    private String description;
    private String diagnosesDate;
    private String diagnosesResult;
    private String childName;
    private String gender;


    public int getDiagnosesId() {
        return diagnosesId;
    }

    public void setDiagnosesId(int diagnosesId) {
        this.diagnosesId = diagnosesId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDiagnosesDate() {
        return diagnosesDate;
    }

    public void setDiagnosesDate(String diagnosesDate) {
        this.diagnosesDate = diagnosesDate;
    }

    public int getChildAge() {
        return childAge;
    }

    public void setChildAge(int childAge) {
        this.childAge = childAge;
    }


    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getWeightChild() {
        return weightChild;
    }

    public void setWeightChild(int weightChild) {
        this.weightChild = weightChild;
    }

    public int getHeightCild() {
        return heightCild;
    }

    public void setHeightCild(int heightCild) {
        this.heightCild = heightCild;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiagnosesResult() {
        return diagnosesResult;
    }

    public void setDiagnosesResult(String diagnosesResult) {
        this.diagnosesResult = diagnosesResult;
    }

    public Diagnoses() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.diagnosesId);
        dest.writeInt(this.childAge);
        dest.writeInt(this.weightChild);
        dest.writeInt(this.heightCild);
        dest.writeString(this.diagnosesDate);
        dest.writeString(this.description);
        dest.writeString(this.diagnosesResult);
        dest.writeString(this.childName);
        dest.writeString(this.gender);
    }

    protected Diagnoses(Parcel in) {
        this.diagnosesId = in.readInt();
        this.diagnosesDate = in.readString();
        this.childAge = in.readInt();
        this.weightChild = in.readInt();
        this.heightCild = in.readInt();
        this.description = in.readString();
        this.diagnosesResult = in.readString();
        this.childName = in.readString();
        this.gender = in.readString();
    }

    public static final Creator<Diagnoses> CREATOR = new Creator<Diagnoses>() {
        @Override
        public Diagnoses createFromParcel(Parcel source) {
            return new Diagnoses(source);
        }

        @Override
        public Diagnoses[] newArray(int size) {
            return new Diagnoses[size];
        }
    };
}
