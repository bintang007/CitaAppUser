package com.cita.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {
    private String childName, dateOfBirth, gender;
    private int childId;

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public Child() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.childName);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.gender);
        dest.writeInt(this.childId);
    }

    protected Child(Parcel in) {
        this.childName = in.readString();
        this.dateOfBirth = in.readString();
        this.gender = in.readString();
        this.childId = in.readInt();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel source) {
            return new Child(source);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };
}
