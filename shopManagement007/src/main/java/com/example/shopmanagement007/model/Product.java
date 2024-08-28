package com.example.shopmanagement007.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private final StringProperty description;
    private final StringProperty code;
    private final StringProperty unit;
    private final IntegerProperty quantity;
    private final FloatProperty price;
    private final FloatProperty salePrice;
    private final IntegerProperty vat;

    public Product(String code, String description, String unit, float price, float salePrice, int quantity, int vat) {
        this.code = new SimpleStringProperty(code);
        this.description = new SimpleStringProperty(description);
        this.unit = new SimpleStringProperty(unit);
        this.price = new SimpleFloatProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.salePrice = new SimpleFloatProperty(salePrice);
        this.vat = new SimpleIntegerProperty(vat);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public StringProperty codeProperty() {
        return code;
    }

    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public float getPrice() {
        return price.get();
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public FloatProperty priceProperty() {
        return price;
    }

    public float getSalePrice() {
        return salePrice.get();
    }

    public void setSalePrice(float salePrice) {
        this.salePrice.set(salePrice);
    }

    public FloatProperty salePriceProperty() {
        return salePrice;
    }

    public int getVat() {
        return vat.get();
    }

    public void setVat(int vat) {
        this.vat.set(vat);
    }

    public IntegerProperty vatProperty() {
        return vat;
    }
}
