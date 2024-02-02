package com.example.slgrocery.Utils;

public class StockValidation {
    private final String itemName;
    private final String quantity;
    private final String price;

    public String errorMessage;

    public StockValidation(String itemName, String quantity, String price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public boolean itemNameValidation() {
        errorMessage = null;
        if (itemName.trim().isEmpty()) {
            errorMessage = "Item name should not be empty";
            return false;
        }
        return true;
    }

    public boolean quantityValidation() {
        errorMessage = null;
        try {
            int intQuantity = Integer.parseInt(quantity);
            if (intQuantity <= 0) {
                errorMessage = "Quantity should be greater than 0";
                return false;
            }
        } catch (Exception e) {
            errorMessage = "Quantity should be integer";
            return false;
        }
        return true;
    }

    public boolean priceValidation() {
        errorMessage = null;
        try {
            Float floatPrice = Float.parseFloat(price);
            if (floatPrice <= 0) {
                errorMessage = "Price should be greater than 0";
                return false;
            }
        } catch (Exception e) {
            errorMessage = "Price should be number";
            return false;
        }
        return true;
    }
}
