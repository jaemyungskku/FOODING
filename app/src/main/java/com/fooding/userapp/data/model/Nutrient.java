package com.fooding.userapp.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ajidmasterz on 12/5/2017.
 */

public class Nutrient {
    @SerializedName("Calorie")
    @Expose
    private String cal;
    @SerializedName("Carbohydrate")
    @Expose
    private String carb;
    @SerializedName("Protein")
    @Expose
    private String protein;
    @SerializedName("Fat")
    @Expose
    private String fat;
    @SerializedName("Sugar")
    @Expose
    private String sugar;
    @SerializedName("Na")
    @Expose
    private String na;
    @SerializedName("Cholesterol")
    @Expose
    private String cholesterol;
    @SerializedName("FattyAcid")
    @Expose
    private String fattyAcid;
    @SerializedName("TransFattyAcid")
    @Expose
    private String transFattyAcid;


    public String getCal() { return cal;}
    public String getCarb() { return carb;}
    public String getProtein() { return protein;}
    public String getFat() { return fat;}
    public String getSugar() { return sugar;}
    public String getNa() { return na;}
    public String getCholesterol() { return cholesterol;}
    public String getFattyAcid() { return fattyAcid;}
    public String getTransFattyAcid() { return transFattyAcid;}

    public void setCal(String cal) {
        this.cal = cal;
    }
    public void setCarb(String carb) {
        this.carb = carb;
    }
    public void setProtein(String protein) {
        this.protein = protein;
    }
    public void setFat(String fat) {
        this.fat = fat;
    }
    public void setSugar(String sugar) {
        this.sugar = sugar;
    }
    public void setNa(String na) {
        this.na = na;
    }
    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }
    public void setFattyAcide(String fattyAcid) {
        this.fattyAcid = fattyAcid;
    }
    public void setTransFattyAcid(String transFattyAcid) {
        this.transFattyAcid = transFattyAcid;
    }
}
