package com.fooding.userapp.data;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class Filter {
    private Map<String, String> Ingredient = new LinkedHashMap<String, String>();

    public Map<String, String> getIngredient() {
        return Ingredient;
    }

    public void setIngredient(Map<String, String> nIngredients) {
        this.Ingredient.putAll(nIngredients);
    }

    public void addIngredient(String key, String name){
        this.Ingredient.put(key,name);
    }
}
