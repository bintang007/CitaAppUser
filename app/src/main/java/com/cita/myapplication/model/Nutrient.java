package com.cita.myapplication.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Nutrient implements Parcelable {
    private String nutrientName, carbohydrate, calories, fat, protein;
    private int nutrientId;

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public String getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(String carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public int getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(int nutrientId) {
        this.nutrientId = nutrientId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nutrientName);
        dest.writeString(this.carbohydrate);
        dest.writeString(this.calories);
        dest.writeString(this.fat);
        dest.writeString(this.protein);
        dest.writeInt(this.nutrientId);
    }

    public Nutrient() {
    }

    protected Nutrient(Parcel in) {
        this.nutrientName = in.readString();
        this.carbohydrate = in.readString();
        this.calories = in.readString();
        this.fat = in.readString();
        this.protein = in.readString();
        this.nutrientId = in.readInt();
    }

    public static final Parcelable.Creator<Nutrient> CREATOR = new Parcelable.Creator<Nutrient>() {
        @Override
        public Nutrient createFromParcel(Parcel source) {
            return new Nutrient(source);
        }

        @Override
        public Nutrient[] newArray(int size) {
            return new Nutrient[size];
        }
    };
}