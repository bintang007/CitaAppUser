package com.cita.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {
    private String childName, dateOfBirth, gender;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.childName);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.gender);
    }

    public Child() {
    }

    protected Child(Parcel in) {
        this.childName = in.readString();
        this.dateOfBirth = in.readString();
        this.gender = in.readString();
    }

    public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>() {
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
