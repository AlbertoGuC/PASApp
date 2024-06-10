package com.example.pasapp.Bebidas.Modelos;

import com.example.pasapp.Bebidas.Modelos.Drink;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DrinkResponse {
    @SerializedName("drinks")
    @Expose
    private List<Drink> drinks;

    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }
}
